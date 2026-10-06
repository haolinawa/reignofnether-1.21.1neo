package com.solegendary.reignofnether.ability;









import net.neoforged.neoforge.network.PacketDistributor;
import com.solegendary.reignofnether.ReignOfNether;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.FriendlyByteBuf;
import com.solegendary.reignofnether.ReignOfNether;
import com.solegendary.reignofnether.registrars.PacketHandler;
import com.solegendary.reignofnether.unit.UnitAction;
import com.solegendary.reignofnether.unit.UnitServerEvents;
import com.solegendary.reignofnether.unit.interfaces.HeroUnit;
import com.solegendary.reignofnether.unit.interfaces.Unit;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;

public class AbilityServerboundPacket implements CustomPacketPayload  {
    public static final Type<AbilityServerboundPacket> TYPE = new Type<>(ResourceLocation.parse("reignofnether:ability_serverbound_packet"));
    public static final StreamCodec<FriendlyByteBuf, AbilityServerboundPacket> STREAM_CODEC =
            StreamCodec.of((buf, payload) -> payload.encode(buf), AbilityServerboundPacket::new);

    @Override
    public Type<? extends CustomPacketPayload> type() { return TYPE; }


    private final int unitId;
    private final UnitAction unitAction;

    public static void rankUpAbility(int unitId, UnitAction abilityAction) {
        PacketDistributor.sendToServer(new AbilityServerboundPacket(unitId, abilityAction));
    }

    public AbilityServerboundPacket(
        int unitId,
        UnitAction unitAction
    ) {
        this.unitId = unitId;
        this.unitAction = unitAction;
    }

    public AbilityServerboundPacket(FriendlyByteBuf buffer) {
        this.unitId = buffer.readInt();
        this.unitAction = buffer.readEnum(UnitAction.class);
    }

    public void encode(FriendlyByteBuf buffer) {
        buffer.writeInt(this.unitId);
        buffer.writeEnum(this.unitAction);
    }

    // server-side packet-consuming functions
    public void handle(IPayloadContext ctx) {
 ctx.enqueueWork(() -> {

            ServerPlayer player = (net.minecraft.server.level.ServerPlayer) ctx.player();
            if (player == null) {
                ReignOfNether.LOGGER.warn("AbilityServerboundPacket: Sender was null");
                return;
            }
            for (LivingEntity entity : UnitServerEvents.getAllUnits()) {
                if (entity.getId() == this.unitId && entity instanceof Unit unit) {

                    if (!player.getName().getString().equals(unit.getOwnerName())) {
                        ReignOfNether.LOGGER.warn("AbilityServerboundPacket: Tried to process packet from " + player.getName() + " for: " + unit.getOwnerName());
                        return;
                    }

                    for (Ability ability : unit.getAbilities().get()) {
                        if (ability.action == this.unitAction && ability instanceof HeroAbility heroAbility && unit instanceof HeroUnit hero) {
                            ReignOfNether.LOGGER.info("[Ability] {} ranked up ability {} on unit {}", player.getName(), this.unitAction, this.unitId);
                            heroAbility.rankUp(hero);
                        }
                    }
                }
            }
        });
    }
}
