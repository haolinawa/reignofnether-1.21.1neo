package com.solegendary.reignofnether.unit.packets;









import net.neoforged.neoforge.network.PacketDistributor;
import com.solegendary.reignofnether.ReignOfNether;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.FriendlyByteBuf;
import com.solegendary.reignofnether.registrars.PacketHandler;
import com.solegendary.reignofnether.unit.UnitClientEvents;
import com.solegendary.reignofnether.unit.UnitServerEvents;
import com.solegendary.reignofnether.unit.interfaces.WorkerUnit;
import com.solegendary.reignofnether.util.ArrayUtil;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;

// send a list of worker unit ids that are idle at this point in time
public class UnitIdleWorkerClientBoundPacket implements CustomPacketPayload  {
    public static final Type<UnitIdleWorkerClientBoundPacket> TYPE = new Type<>(ResourceLocation.parse("reignofnether:unit_idle_worker_client_bound_packet"));
    public static final StreamCodec<FriendlyByteBuf, UnitIdleWorkerClientBoundPacket> STREAM_CODEC =
            StreamCodec.of((buf, payload) -> payload.encode(buf), UnitIdleWorkerClientBoundPacket::new);

    @Override
    public Type<? extends CustomPacketPayload> type() { return TYPE; }


    private final int[] oldUnitIds; // units to be controlled

    public static void sendIdleWorkerPacket() {
        var units = new LinkedList<Integer>();
        for (LivingEntity livingEntity : UnitServerEvents.getAllUnits()) {
            if (livingEntity instanceof WorkerUnit wu && WorkerUnit.isIdle(wu)) units.add(livingEntity.getId());
        }
        PacketDistributor.sendToAllPlayers(new UnitIdleWorkerClientBoundPacket(ArrayUtil.intListToArray(units)));
    }

    // packet-handler functions
    public UnitIdleWorkerClientBoundPacket(
            int[] oldUnitIds
    ) {
        this.oldUnitIds = oldUnitIds;
    }

    public UnitIdleWorkerClientBoundPacket(FriendlyByteBuf buffer) {
        this.oldUnitIds = buffer.readVarIntArray();
    }

    public void encode(FriendlyByteBuf buffer) {
        buffer.writeVarIntArray(this.oldUnitIds);
    }

    // server-side packet-consuming functions
    public void handle(IPayloadContext ctx) {
 ctx.enqueueWork(() -> {
            UnitClientEvents.syncIdleWorkers(oldUnitIds);
        });
    }
}
