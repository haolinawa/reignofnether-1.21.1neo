package com.solegendary.reignofnether.building;










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
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.neoforged.api.distmarker.Dist;
import com.solegendary.reignofnether.util.DistUtil;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;

public class FogBuildingClientboundPacket implements CustomPacketPayload  {
    public static final Type<FogBuildingClientboundPacket> TYPE = new Type<>(ResourceLocation.parse("reignofnether:fog_building_clientbound_packet"));
    public static final StreamCodec<FriendlyByteBuf, FogBuildingClientboundPacket> STREAM_CODEC =
            StreamCodec.of((buf, payload) -> payload.encode(buf), FogBuildingClientboundPacket::new);

    @Override
    public Type<? extends CustomPacketPayload> type() { return TYPE; }


    private final BlockPos pos;

    public static void removeFogQueuedBuilding(BlockPos pos) {
        PacketDistributor.sendToAllPlayers(new FogBuildingClientboundPacket(pos)
        );
    }

    public FogBuildingClientboundPacket(BlockPos pos) {
        this.pos = pos;
    }

    public FogBuildingClientboundPacket(FriendlyByteBuf buffer) {
        this.pos = buffer.readBlockPos();
    }

    public void encode(FriendlyByteBuf buffer) {
        buffer.writeBlockPos(this.pos);
    }

    public void handle(IPayloadContext ctx) {

 ctx.enqueueWork(() -> {
            DistUtil.unsafeRunWhenOn(Dist.CLIENT,
                    () -> () -> {
                        BuildingClientEvents.removeFogQueuedBuilding(pos);
                    });
        });
    }
}
