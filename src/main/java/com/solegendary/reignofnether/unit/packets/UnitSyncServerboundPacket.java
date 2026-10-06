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
import com.solegendary.reignofnether.unit.UnitServerEvents;
import com.solegendary.reignofnether.unit.UnitSyncAction;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.LivingEntity;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;

public class UnitSyncServerboundPacket implements CustomPacketPayload  {
    public static final Type<UnitSyncServerboundPacket> TYPE = new Type<>(ResourceLocation.parse("reignofnether:unit_sync_serverbound_packet"));
    public static final StreamCodec<FriendlyByteBuf, UnitSyncServerboundPacket> STREAM_CODEC =
            StreamCodec.of((buf, payload) -> payload.encode(buf), UnitSyncServerboundPacket::new);

    @Override
    public Type<? extends CustomPacketPayload> type() { return TYPE; }


    private final UnitSyncAction syncAction;
    private final int entityId;

    public static void requestSyncAbilities(int unitId) {
        PacketDistributor.sendToServer(new UnitSyncServerboundPacket(UnitSyncAction.REQUEST_SYNC_ABILITIES, unitId));
    }

    // packet-handler functions
    public UnitSyncServerboundPacket(
        UnitSyncAction syncAction,
        int unitId
    ) {
        this.syncAction = syncAction;
        this.entityId = unitId;
    }

    public UnitSyncServerboundPacket(FriendlyByteBuf buffer) {
        this.syncAction = buffer.readEnum(UnitSyncAction.class);
        this.entityId = buffer.readInt();
    }

    public void encode(FriendlyByteBuf buffer) {
        buffer.writeEnum(this.syncAction);
        buffer.writeInt(this.entityId);
    }

    // server-side packet-consuming functions
    public void handle(IPayloadContext ctx) {
 ctx.enqueueWork(() -> {
            if (this.syncAction == UnitSyncAction.REQUEST_SYNC_ABILITIES) {
                for (LivingEntity entity : UnitServerEvents.getAllUnits()) {
                    if (entity.getId() == this.entityId) {
                        UnitSyncAbilityClientboundPacket.sendSyncAbilitiesPacket(entity);
                    }
                }
            }
        });
    }
}
