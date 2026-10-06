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
import com.solegendary.reignofnether.registrars.PacketHandler;
import com.solegendary.reignofnether.resources.ResourceName;
import com.solegendary.reignofnether.unit.UnitClientEvents;
import com.solegendary.reignofnether.unit.interfaces.WorkerUnit;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.api.distmarker.Dist;
import com.solegendary.reignofnether.util.DistUtil;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;

public class UnitSyncWorkerClientBoundPacket implements CustomPacketPayload  {
    public static final Type<UnitSyncWorkerClientBoundPacket> TYPE = new Type<>(ResourceLocation.parse("reignofnether:unit_sync_worker_client_bound_packet"));
    public static final StreamCodec<FriendlyByteBuf, UnitSyncWorkerClientBoundPacket> STREAM_CODEC =
            StreamCodec.of((buf, payload) -> payload.encode(buf), UnitSyncWorkerClientBoundPacket::new);

    @Override
    public Type<? extends CustomPacketPayload> type() { return TYPE; }


    private final int entityId;
    private final boolean isBuilding; // for workers to show arms swinging
    private final boolean isGathering; // server-authoritative in-range gathering state (client can't compute it)
    private final ResourceName gatherName; // for workers to show arms swinging and have the right tool
    private final BlockPos gatherPos;
    private final int gatherTicks;

    public static void sendSyncWorkerPacket(LivingEntity entity) {
        if (entity instanceof WorkerUnit workerUnit) {
            BlockPos bp = workerUnit.getGatherResourceGoal().getGatherTarget();

            PacketDistributor.sendToAllPlayers(new UnitSyncWorkerClientBoundPacket(entity.getId(),
                    workerUnit.getBuildRepairGoal().isBuilding(),
                    workerUnit.getGatherResourceGoal().isGathering(),
                    workerUnit.getGatherResourceGoal().getTargetResourceName(),
                    bp == null ? new BlockPos(0,0,0) : bp,
                    workerUnit.getGatherResourceGoal().getGatherTicksLeft())
            );
        }
    }

    // packet-handler functions
    public UnitSyncWorkerClientBoundPacket(
        int unitId,
        boolean isBuilding,
        boolean isGathering,
        ResourceName gatherName,
        BlockPos gatherPos,
        int gatherTicks
    ) {
        // filter out non-owned entities so we can't control them
        this.entityId = unitId;
        this.isBuilding = isBuilding;
        this.isGathering = isGathering;
        this.gatherName = gatherName;
        this.gatherPos = gatherPos;
        this.gatherTicks = gatherTicks;
    }

    public UnitSyncWorkerClientBoundPacket(FriendlyByteBuf buffer) {
        this.entityId = buffer.readInt();
        this.isBuilding = buffer.readBoolean();
        this.isGathering = buffer.readBoolean();
        this.gatherName = buffer.readEnum(ResourceName.class);
        this.gatherPos = buffer.readBlockPos();
        this.gatherTicks = buffer.readInt();
    }

    public void encode(FriendlyByteBuf buffer) {
        buffer.writeInt(this.entityId);
        buffer.writeBoolean(this.isBuilding);
        buffer.writeBoolean(this.isGathering);
        buffer.writeEnum(this.gatherName);
        buffer.writeBlockPos(this.gatherPos);
        buffer.writeInt(this.gatherTicks);
    }

    // client-side packet-consuming functions
    public void handle(IPayloadContext ctx) {

 ctx.enqueueWork(() -> {
            DistUtil.unsafeRunWhenOn(Dist.CLIENT,
                () -> () -> {
                    UnitClientEvents.syncWorkerUnit(
                        this.entityId,
                        this.isBuilding,
                        this.isGathering,
                        this.gatherName,
                        this.gatherPos.equals(new BlockPos(0,0,0)) ? null : this.gatherPos,
                        this.gatherTicks);
                });
        });
    }
}
