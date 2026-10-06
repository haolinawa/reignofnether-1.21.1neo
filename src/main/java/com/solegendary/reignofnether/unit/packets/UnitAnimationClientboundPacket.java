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
import com.solegendary.reignofnether.unit.UnitAnimationAction;
import com.solegendary.reignofnether.unit.UnitClientEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.api.distmarker.Dist;
import com.solegendary.reignofnether.util.DistUtil;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;

public class UnitAnimationClientboundPacket implements CustomPacketPayload  {
    public static final Type<UnitAnimationClientboundPacket> TYPE = new Type<>(ResourceLocation.parse("reignofnether:unit_animation_clientbound_packet"));
    public static final StreamCodec<FriendlyByteBuf, UnitAnimationClientboundPacket> STREAM_CODEC =
            StreamCodec.of((buf, payload) -> payload.encode(buf), UnitAnimationClientboundPacket::new);

    @Override
    public Type<? extends CustomPacketPayload> type() { return TYPE; }


    private final UnitAnimationAction animAction;
    private final int entityId;
    private final int targetId;
    private final double posX;
    private final double posY;
    private final double posZ;

    // no targets
    public static void sendBasicPacket(UnitAnimationAction animAction, LivingEntity entity) {
        PacketDistributor.sendToAllPlayers(new UnitAnimationClientboundPacket(
                        animAction,
                        entity.getId(),
                        0,0,0,0
                )
        );
    }

    public static void sendEntityPacket(UnitAnimationAction animAction, LivingEntity entity, LivingEntity target) {
        PacketDistributor.sendToAllPlayers(new UnitAnimationClientboundPacket(
                        animAction,
                        entity.getId(), target.getId(),
                        0,0,0)
        );
    }

    public static void sendBlockPosPacket(UnitAnimationAction animAction, LivingEntity entity, BlockPos bp) {
        PacketDistributor.sendToAllPlayers(new UnitAnimationClientboundPacket(
                        animAction,
                        entity.getId(), 0,
                        bp.getX(), bp.getY(), bp.getZ())
        );
    }

    public static void sendEatFoodPacket(LivingEntity entity, int itemId) {
        PacketDistributor.sendToAllPlayers(new UnitAnimationClientboundPacket(
                        UnitAnimationAction.EAT_FOOD_ITEM,
                        entity.getId(), itemId,
                        0,0,0)
        );
    }

    // packet-handler functions
    public UnitAnimationClientboundPacket(
        UnitAnimationAction animAction,
        int unitId,
        int targetId,
        double posX,
        double posY,
        double posZ
    ) {
        // filter out non-owned entities so we can't control them
        this.animAction = animAction;
        this.entityId = unitId;
        this.targetId = targetId;
        this.posX = posX;
        this.posY = posY;
        this.posZ = posZ;
    }

    public UnitAnimationClientboundPacket(FriendlyByteBuf buffer) {
        this.animAction = buffer.readEnum(UnitAnimationAction.class);
        this.entityId = buffer.readInt();
        this.targetId = buffer.readInt();
        this.posX = buffer.readDouble();
        this.posY = buffer.readDouble();
        this.posZ = buffer.readDouble();

    }

    public void encode(FriendlyByteBuf buffer) {
        buffer.writeEnum(this.animAction);
        buffer.writeInt(this.entityId);
        buffer.writeInt(this.targetId);
        buffer.writeDouble(this.posX);
        buffer.writeDouble(this.posY);
        buffer.writeDouble(this.posZ);
    }

    // client-side packet-consuming functions
    public void handle(IPayloadContext ctx) {

 ctx.enqueueWork(() -> {
            DistUtil.unsafeRunWhenOn(Dist.CLIENT,
                () -> () -> {
                    switch (this.animAction) {
                        case EAT_FOOD_ITEM -> UnitClientEvents.syncUnitEatingFood(this.entityId, this.targetId);
                        case NON_KEYFRAME_START -> UnitClientEvents.syncUnitAnimation(this.animAction, true,
                                this.entityId, this.targetId, new BlockPos((int) this.posX, (int) this.posY, (int) this.posZ));
                        case NON_KEYFRAME_STOP -> UnitClientEvents.syncUnitAnimation(this.animAction, false,
                                this.entityId, this.targetId, new BlockPos((int) this.posX, (int) this.posY, (int) this.posZ));
                        case NON_KEYFRAME_ATTACK -> UnitClientEvents.playAttackAnimation(this.entityId);
                        default -> UnitClientEvents.playKeyframeAnimation(this.animAction, this.entityId);
                    }
                });
        });
    }
}
