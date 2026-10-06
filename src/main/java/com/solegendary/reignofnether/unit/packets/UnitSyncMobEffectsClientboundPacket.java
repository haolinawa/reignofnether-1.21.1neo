package com.solegendary.reignofnether.unit.packets;

import net.minecraft.core.Holder;










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
import com.solegendary.reignofnether.unit.UnitClientEvents;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.api.distmarker.Dist;
import com.solegendary.reignofnether.util.DistUtil;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;

public class UnitSyncMobEffectsClientboundPacket implements CustomPacketPayload  {
    public static final Type<UnitSyncMobEffectsClientboundPacket> TYPE = new Type<>(ResourceLocation.parse("reignofnether:unit_sync_mob_effects_clientbound_packet"));
    public static final StreamCodec<FriendlyByteBuf, UnitSyncMobEffectsClientboundPacket> STREAM_CODEC =
            StreamCodec.of((buf, payload) -> payload.encode(buf), UnitSyncMobEffectsClientboundPacket::new);

    @Override
    public Type<? extends CustomPacketPayload> type() { return TYPE; }


    private final int entityId;
    private final int effectId;
    private final int amplifier;
    private final int duration;

    public static void addEffectClientside(LivingEntity entity, MobEffectInstance mei) {
        PacketDistributor.sendToAllPlayers(new UnitSyncMobEffectsClientboundPacket(entity.getId(), net.minecraft.core.registries.BuiltInRegistries.MOB_EFFECT.getId(mei.getEffect().value()), mei.getAmplifier(), mei.getDuration())
        );
    }

    public static void removeEffectClientside(LivingEntity entity, net.minecraft.core.Holder<MobEffect> me) {
        PacketDistributor.sendToAllPlayers(new UnitSyncMobEffectsClientboundPacket(entity.getId(), net.minecraft.core.registries.BuiltInRegistries.MOB_EFFECT.getId(me.value()), 0, 0)
        );
    }

    // packet-handler functions
    public UnitSyncMobEffectsClientboundPacket(
        int entityId,
        int descriptionId,
        int amplifier,
        int duration
    ) {
        this.entityId = entityId;
        this.effectId = descriptionId;
        this.amplifier = amplifier;
        this.duration = duration;
    }

    public UnitSyncMobEffectsClientboundPacket(FriendlyByteBuf buffer) {
        this.entityId = buffer.readInt();
        this.effectId = buffer.readInt();
        this.amplifier = buffer.readInt();
        this.duration = buffer.readInt();
    }

    public void encode(FriendlyByteBuf buffer) {
        buffer.writeInt(this.entityId);
        buffer.writeInt(this.effectId);
        buffer.writeInt(this.amplifier);
        buffer.writeInt(this.duration);
    }

    // client-side packet-consuming functions
    public void handle(IPayloadContext ctx) {

 ctx.enqueueWork(() -> {
            DistUtil.unsafeRunWhenOn(Dist.CLIENT,
                () -> () -> {
                    // rest is handled by MobEffectEvent.Added event
                    UnitClientEvents.syncMobEffect(this.entityId, this.effectId, this.amplifier, this.duration);
                });
        });
    }
}
