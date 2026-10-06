package com.solegendary.reignofnether.mixin.fogofwar;

import com.solegendary.reignofnether.fogofwar.FogChunkSnapshot;
import com.solegendary.reignofnether.fogofwar.FogOfWarServerEvents;
import net.minecraft.network.protocol.game.ClientboundLevelChunkWithLightPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.server.network.PlayerChunkSender;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.LevelChunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

// serve the pre-match snapshot when a dark player first loads a chunk
// 1.21.1: ChunkMap.playerLoadedChunk was removed; chunk packets are now sent via
// PlayerChunkSender.sendChunk(connection, level, chunk)
@Mixin(PlayerChunkSender.class)
public abstract class ChunkMapInitialSendMixin {

    @Inject(
            method = "sendChunk",
            at = @At("HEAD"),
            cancellable = true
    )
    private static void reignofnether$serveFogSnapshot(
            ServerGamePacketListenerImpl connection,
            ServerLevel level,
            LevelChunk chunk,
            CallbackInfo ci
    ) {
        ServerPlayer sp = connection.player;
        if (!FogOfWarServerEvents.isEnabled()) return;
        if (!FogOfWarServerEvents.isFogActiveFor(sp)) return;

        ChunkPos pos = chunk.getPos();

        if (FogOfWarServerEvents.isChunkLiveFor(sp, pos)) return;
        ClientboundLevelChunkWithLightPacket snap = FogChunkSnapshot.get(pos);
        if (snap != null) {
            sp.connection.send(snap);
        }
        if (FogOfWarServerEvents.isChunkSentFor(sp, pos))
            FogOfWarServerEvents.queueResend(sp.getUUID(), pos);

        ci.cancel();
    }
}
