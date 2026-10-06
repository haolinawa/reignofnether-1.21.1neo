package com.solegendary.reignofnether.fogofwar;










import net.neoforged.neoforge.network.PacketDistributor;
import com.solegendary.reignofnether.util.DistUtil;
import com.solegendary.reignofnether.ReignOfNether;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.FriendlyByteBuf;
import com.solegendary.reignofnether.registrars.PacketHandler;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.ChunkPos;
import net.neoforged.api.distmarker.Dist;
import com.solegendary.reignofnether.util.DistUtil;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;

// per-player live (currently visible/tracked) chunks and sent (already streamed to client) chunks.
// Sent to a client for debug/overlay purposes - shows what the server thinks each player can see.
public class PlayerChunksClientboundPacket implements CustomPacketPayload  {
    public static final Type<PlayerChunksClientboundPacket> TYPE = new Type<>(ResourceLocation.parse("reignofnether:player_chunks_clientbound_packet"));
    public static final StreamCodec<FriendlyByteBuf, PlayerChunksClientboundPacket> STREAM_CODEC =
            StreamCodec.of((buf, payload) -> payload.encode(buf), PlayerChunksClientboundPacket::new);

    @Override
    public Type<? extends CustomPacketPayload> type() { return TYPE; }


    public final Map<UUID, Set<ChunkPos>> liveChunks;
    public final Map<UUID, Set<ChunkPos>> edgeChunks;

    public static void send(ServerPlayer player, Map<UUID, Set<ChunkPos>> liveChunks, Map<UUID, Set<ChunkPos>> sentChunks) {
        PacketDistributor.sendToPlayer(player, new PlayerChunksClientboundPacket(liveChunks, sentChunks)
        );
    }

    public PlayerChunksClientboundPacket(Map<UUID, Set<ChunkPos>> liveChunks, Map<UUID, Set<ChunkPos>> edgeChunks) {
        this.liveChunks = liveChunks;
        this.edgeChunks = edgeChunks;
    }

    public PlayerChunksClientboundPacket(FriendlyByteBuf buf) {
        this.liveChunks = readMap(buf);
        this.edgeChunks = readMap(buf);
    }

    private static Map<UUID, Set<ChunkPos>> readMap(FriendlyByteBuf buf) {
        int players = buf.readVarInt();
        Map<UUID, Set<ChunkPos>> map = new HashMap<>(players * 2);
        for (int i = 0; i < players; i++) {
            UUID uuid = buf.readUUID();
            int n = buf.readVarInt();
            Set<ChunkPos> chunks = new HashSet<>(n * 2);
            for (int j = 0; j < n; j++)
                chunks.add(new ChunkPos(buf.readLong()));
            map.put(uuid, chunks);
        }
        return map;
    }

    private static void writeMap(FriendlyByteBuf buf, Map<UUID, Set<ChunkPos>> map) {
        buf.writeVarInt(map.size());
        for (Map.Entry<UUID, Set<ChunkPos>> entry : map.entrySet()) {
            buf.writeUUID(entry.getKey());
            Set<ChunkPos> chunks = entry.getValue();
            buf.writeVarInt(chunks.size());
            for (ChunkPos cp : chunks)
                buf.writeLong(cp.toLong());
        }
    }

    public void encode(FriendlyByteBuf buf) {
        writeMap(buf, liveChunks);
        writeMap(buf, edgeChunks);
    }

    public void handle(IPayloadContext ctx) {
 ctx.enqueueWork(() -> {
            DistUtil.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
                PlayerChunksClientEvents.applyServerState(liveChunks, edgeChunks);
            });
        });
    }
}