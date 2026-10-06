package com.solegendary.reignofnether.mixin.fogofwar;

import com.solegendary.reignofnether.fogofwar.FogOfWarServerEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

// Gate entity packets behind fog; target by name since TrackedEntity is package-private.
//
// This MUST NOT cancel updatePlayer at HEAD: that method also owns the seenBy/addPairing bookkeeping
// ("if (flag) { if (this.seenBy.add(conn)) addPairing } else if (remove) removePairing"). Cancelling it
// skipped the pairing entirely, so an entity that later became visible was only paired the next time
// vanilla happened to re-evaluate that player (i.e. after the player moved) - which is why other players
// and entities took a while to appear. Instead, only the fog result is folded into vanilla's own boolean
// so its pairing/unpairing logic still runs every time.
@Mixin(targets = "net.minecraft.server.level.ChunkMap$TrackedEntity")
public abstract class TrackedEntityMixin {

    @Shadow @Final Entity entity;

    // Fold the fog result into vanilla's own tracking flag. This redirects the broadcastToPlayer() term of
    // vanilla's "flag" expression instead of cancelling the method, so vanilla's own
    // addPairing/removePairing bookkeeping still runs on every evaluation. Returning false makes vanilla
    // take its normal "not tracked" path (removePairing), keeping pairing consistent in both directions.
    @Redirect(
            method = "updatePlayer",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/Entity;broadcastToPlayer(Lnet/minecraft/server/level/ServerPlayer;)Z")
    )
    private boolean reignofnether$gateEntityVisibility(Entity instance, ServerPlayer player) {
        if (!instance.broadcastToPlayer(player)) return false;
        if (!FogOfWarServerEvents.isFogActiveFor(player)) return true;
        // block-level gate: an enemy entity hides unless its own column is inside the viewer's circle
        BlockPos bp = this.entity.blockPosition();
        return FogOfWarServerEvents.isBlockVisibleFor(player, bp.getX(), bp.getZ());
    }
}
