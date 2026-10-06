package com.solegendary.reignofnether.hero;









import net.neoforged.neoforge.network.PacketDistributor;
import com.solegendary.reignofnether.ReignOfNether;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.FriendlyByteBuf;
import com.solegendary.reignofnether.ability.HeroAbility;
import com.solegendary.reignofnether.registrars.PacketHandler;
import com.solegendary.reignofnether.unit.UnitServerEvents;
import com.solegendary.reignofnether.unit.interfaces.HeroUnit;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.LivingEntity;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;

public class HeroServerboundPacket implements CustomPacketPayload  {
    public static final Type<HeroServerboundPacket> TYPE = new Type<>(ResourceLocation.parse("reignofnether:hero_serverbound_packet"));
    public static final StreamCodec<FriendlyByteBuf, HeroServerboundPacket> STREAM_CODEC =
            StreamCodec.of((buf, payload) -> payload.encode(buf), HeroServerboundPacket::new);

    @Override
    public Type<? extends CustomPacketPayload> type() { return TYPE; }


    private final int unitId;
    private final HeroAction heroAction;

    public static void requestHeroSync(int unitId) {
        PacketDistributor.sendToServer(new HeroServerboundPacket(unitId, HeroAction.REQUEST_SYNC));
    }

    public HeroServerboundPacket(
            int unitId,
            HeroAction heroAction
    ) {
        this.unitId = unitId;
        this.heroAction = heroAction;
    }

    public HeroServerboundPacket(FriendlyByteBuf buffer) {
        this.unitId = buffer.readInt();
        this.heroAction = buffer.readEnum(HeroAction.class);
    }

    public void encode(FriendlyByteBuf buffer) {
        buffer.writeInt(this.unitId);
        buffer.writeEnum(this.heroAction);
    }

    // server-side packet-consuming functions
    public void handle(IPayloadContext ctx) {
 ctx.enqueueWork(() -> {
            if (heroAction == HeroAction.REQUEST_SYNC) {
                for (LivingEntity entity : UnitServerEvents.getAllUnits()) {
                    if (entity.getId() == this.unitId && entity instanceof HeroUnit hero) {
                        hero.syncToClients();
                    }
                }
            }
        });
    }
}
