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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;

// per-client vision state: the sent (bright) chunk set plus the covered-column bitmask of each edge chunk.
// Server-authoritative — the client renders exactly these masks. Resent whenever either changes.
public class FogChunksClientboundPacket implements CustomPacketPayload  {
    public static final Type<FogChunksClientboundPacket> TYPE = new Type<>(ResourceLocation.parse("reignofnether:fog_chunks_clientbound_packet"));
    public static final StreamCodec<FriendlyByteBuf, FogChunksClientboundPacket> STREAM_CODEC =
            StreamCodec.of((buf, payload) -> payload.encode(buf), FogChunksClientboundPacket::new);

    @Override
    public Type<? extends CustomPacketPayload> type() { return TYPE; }


    public final Set<ChunkPos> bright;
    public final Map<ChunkPos, long[]> edgeMasks;

    public static void send(ServerPlayer player, Set<ChunkPos> bright, Map<ChunkPos, long[]> edgeMasks) {
        PacketDistributor.sendToPlayer(player, new FogChunksClientboundPacket(bright, edgeMasks)
        );
    }

    public FogChunksClientboundPacket(Set<ChunkPos> bright, Map<ChunkPos, long[]> edgeMasks) {
        this.bright = bright;
        this.edgeMasks = edgeMasks;
    }

    public FogChunksClientboundPacket(FriendlyByteBuf buf) {
        int n = buf.readVarInt();
        this.bright = new HashSet<>(n * 2);
        for (int i = 0; i < n; i++)
            this.bright.add(new ChunkPos(buf.readLong()));
        int m = buf.readVarInt();
        this.edgeMasks = new HashMap<>(m * 2);
        for (int i = 0; i < m; i++) {
            ChunkPos cp = new ChunkPos(buf.readLong());
            long[] mask = new long[] { buf.readLong(), buf.readLong(), buf.readLong(), buf.readLong() };
            this.edgeMasks.put(cp, mask);
        }
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeVarInt(bright.size());
        for (ChunkPos p : bright) buf.writeLong(p.toLong());
        buf.writeVarInt(edgeMasks.size());
        for (Map.Entry<ChunkPos, long[]> e : edgeMasks.entrySet()) {
            buf.writeLong(e.getKey().toLong());
            long[] mask = e.getValue();
            buf.writeLong(mask[0]);
            buf.writeLong(mask[1]);
            buf.writeLong(mask[2]);
            buf.writeLong(mask[3]);
        }
    }

    public void handle(IPayloadContext ctx) {
 ctx.enqueueWork(() -> {
            DistUtil.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
                FogOfWarClientEvents.applyServerFogState(bright, edgeMasks);
            });
        });
    }
}
