package com.solegendary.reignofnether.mixin.fogofwar;

import com.solegendary.reignofnether.fogofwar.FogOfWarServerEvents;
import net.minecraft.network.protocol.Packet;
import net.minecraft.server.level.ChunkHolder;
import net.minecraft.server.level.GenerationChunkHolder;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.ChunkPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

// Per-packet fog gate for chunk broadcasts (block updates, multi-block section updates, block entity data,
// light). ChunkMapMixin already dropped fog-dark players from the recipient list; this refines edge chunks
// per column via FogOfWarServerEvents.shouldSendChunkPacket so the visible part of an edge chunk updates
// live while its fogged part stays frozen.
// 1.21.1: the pos field moved to the parent GenerationChunkHolder -> read it via accessor
@Mixin(ChunkHolder.class)
public abstract class ChunkHolderMixin {

    @Inject(
            method = "broadcast(Ljava/util/List;Lnet/minecraft/network/protocol/Packet;)V",
            at = @At("HEAD"),
            cancellable = true
    )
    private void reignofnether$gateEdgeChunkPackets(List<ServerPlayer> players, Packet<?> packet, CallbackInfo ci) {
        if (!FogOfWarServerEvents.isEnabled()) return;
        ci.cancel();
        ChunkPos pos = ((GenerationChunkHolderAccessor) (Object) this).ron$getPos();
        for (ServerPlayer sp : players)
            if (FogOfWarServerEvents.shouldSendChunkPacket(sp, pos, packet))
                sp.connection.send(packet);
    }
}
