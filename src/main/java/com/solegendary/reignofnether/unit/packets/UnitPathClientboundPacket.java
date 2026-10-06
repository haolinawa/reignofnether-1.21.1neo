package com.solegendary.reignofnether.unit.packets;










import net.neoforged.neoforge.network.PacketDistributor;
import com.solegendary.reignofnether.util.DistUtil;
import com.solegendary.reignofnether.ReignOfNether;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.FriendlyByteBuf;
import com.solegendary.reignofnether.debug.RtsDebugPathPreview;
import com.solegendary.reignofnether.fogofwar.FogOfWarServerEvents;
import com.solegendary.reignofnether.registrars.PacketHandler;
import com.solegendary.reignofnether.unit.UnitServerEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.pathfinder.Path;
import net.neoforged.api.distmarker.Dist;
import com.solegendary.reignofnether.util.DistUtil;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

// Sent server→client when a unit gets a fresh path. The client renders the path so the player
// can see the route their units will actually take. Gated by /rts-debug on the sender side.
public class UnitPathClientboundPacket implements CustomPacketPayload  {
    public static final Type<UnitPathClientboundPacket> TYPE = new Type<>(ResourceLocation.parse("reignofnether:unit_path_clientbound_packet"));
    public static final StreamCodec<FriendlyByteBuf, UnitPathClientboundPacket> STREAM_CODEC =
            StreamCodec.of((buf, payload) -> payload.encode(buf), UnitPathClientboundPacket::new);

    @Override
    public Type<? extends CustomPacketPayload> type() { return TYPE; }


    private final int entityId;
    private final byte pathType;
    private final List<BlockPos> nodes;

    public static void sendPath(LivingEntity entity, Path path, byte pathType) {
        // disallow with fog since that could be used to see hidden blocks
        if (path == null || path.nodes.isEmpty() || FogOfWarServerEvents.isEnabled())
            return;
        List<BlockPos> bps = new ArrayList<>(path.nodes.size());
        for (var node : path.nodes)
            bps.add(node.asBlockPos());
        PacketDistributor.sendToAllPlayers(new UnitPathClientboundPacket(entity.getId(), pathType, bps));
    }

    public UnitPathClientboundPacket(int entityId, byte pathType, List<BlockPos> nodes) {
        this.entityId = entityId;
        this.pathType = pathType;
        this.nodes = nodes;
    }

    public UnitPathClientboundPacket(FriendlyByteBuf buffer) {
        this.entityId = buffer.readInt();
        this.pathType = buffer.readByte();
        int n = buffer.readVarInt();
        this.nodes = new ArrayList<>(n);
        for (int i = 0; i < n; i++)
            this.nodes.add(buffer.readBlockPos());
    }

    public void encode(FriendlyByteBuf buffer) {
        buffer.writeInt(this.entityId);
        buffer.writeByte(this.pathType);
        buffer.writeVarInt(this.nodes.size());
        for (BlockPos bp : this.nodes)
            buffer.writeBlockPos(bp);
    }

    public void handle(IPayloadContext ctx) {
 ctx.enqueueWork(() -> {
            DistUtil.unsafeRunWhenOn(Dist.CLIENT,
                () -> () -> RtsDebugPathPreview.receiveUnitPath(this.entityId, this.pathType, this.nodes));
        });
    }
}
