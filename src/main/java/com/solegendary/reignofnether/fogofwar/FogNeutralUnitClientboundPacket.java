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
import com.solegendary.reignofnether.minimap.MinimapClientEvents;
import com.solegendary.reignofnether.registrars.PacketHandler;
import com.solegendary.reignofnether.unit.interfaces.RangedAttackerUnit;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.AABB;
import net.neoforged.api.distmarker.Dist;
import com.solegendary.reignofnether.util.DistUtil;
import net.neoforged.neoforge.network.PacketDistributor;
import org.joml.Vector3f;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;

public class FogNeutralUnitClientboundPacket implements CustomPacketPayload  {
    public static final Type<FogNeutralUnitClientboundPacket> TYPE = new Type<>(ResourceLocation.parse("reignofnether:fog_neutral_unit_clientbound_packet"));
    public static final StreamCodec<FriendlyByteBuf, FogNeutralUnitClientboundPacket> STREAM_CODEC =
            StreamCodec.of((buf, payload) -> payload.encode(buf), FogNeutralUnitClientboundPacket::new);

    @Override
    public Type<? extends CustomPacketPayload> type() { return TYPE; }


    public int unitId;
    public Vector3f vec3fMin;
    public Vector3f vec3fMax;
    public boolean remove;

    public static void sendNeutralFogUnitToAll(int unitId, AABB aabb) {
        PacketDistributor.sendToAllPlayers(new FogNeutralUnitClientboundPacket(unitId, aabb, false));
    }

    public static void sendNeutralFogUnit(ServerPlayer serverPlayer, int unitId, AABB aabb) {
        PacketDistributor.sendToPlayer(serverPlayer, new FogNeutralUnitClientboundPacket(unitId, aabb, false));
    }

    // remove if the player has explored the pos and the unit is dead
    public static void removeNeutralFogUnit(ServerPlayer serverPlayer, int unitId) {
        PacketDistributor.sendToPlayer(serverPlayer, new FogNeutralUnitClientboundPacket(unitId, new AABB(0,0,0,0,0,0), true));
    }

    public FogNeutralUnitClientboundPacket(int unitId, AABB aabb, boolean remove) {
        this.unitId = unitId;
        this.vec3fMin = new Vector3f((float) aabb.minX, (float) aabb.minY, (float) aabb.minZ);
        this.vec3fMax = new Vector3f((float) aabb.maxX, (float) aabb.minY, (float) aabb.maxZ);
        this.remove = remove;
    }

    public FogNeutralUnitClientboundPacket(FriendlyByteBuf buffer) {
        this.unitId = buffer.readInt();
        this.vec3fMin = buffer.readVector3f();
        this.vec3fMax = buffer.readVector3f();
        this.remove = buffer.readBoolean();
    }

    public void encode(FriendlyByteBuf buffer) {
        buffer.writeInt(this.unitId);
        buffer.writeVector3f(this.vec3fMin);
        buffer.writeVector3f(this.vec3fMax);
        buffer.writeBoolean(this.remove);
    }

    // server-side packet-consuming functions
    public void handle(IPayloadContext ctx) {

 ctx.enqueueWork(() -> {
            DistUtil.unsafeRunWhenOn(Dist.CLIENT,
                () -> () -> {
                    if (remove)
                        MinimapClientEvents.removeNeutralFogUnit(this.unitId);
                    else
                        MinimapClientEvents.addNeutralFogUnit(this.unitId, this.vec3fMin, this.vec3fMax);
                });
        });
    }
}
