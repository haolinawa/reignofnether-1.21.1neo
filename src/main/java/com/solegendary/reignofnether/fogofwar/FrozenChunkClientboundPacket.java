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
import com.solegendary.reignofnether.registrars.PacketHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.neoforged.api.distmarker.Dist;
import com.solegendary.reignofnether.util.DistUtil;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;

public class FrozenChunkClientboundPacket implements CustomPacketPayload  {
    public static final Type<FrozenChunkClientboundPacket> TYPE = new Type<>(ResourceLocation.parse("reignofnether:frozen_chunk_clientbound_packet"));
    public static final StreamCodec<FriendlyByteBuf, FrozenChunkClientboundPacket> STREAM_CODEC =
            StreamCodec.of((buf, payload) -> payload.encode(buf), FrozenChunkClientboundPacket::new);

    @Override
    public Type<? extends CustomPacketPayload> type() { return TYPE; }


    FrozenChunkAction action;
    BlockPos blockPos;

    public static void setBuildingDestroyedServerside(BlockPos buildingOrigin) {
        PacketDistributor.sendToAllPlayers(new FrozenChunkClientboundPacket(FrozenChunkAction.SET_BUILDING_DESTROYED, buildingOrigin));
    }

    public static void setBuildingBuiltServerside(BlockPos buildingOrigin) {
        PacketDistributor.sendToAllPlayers(new FrozenChunkClientboundPacket(FrozenChunkAction.SET_BUILDING_BUILT, buildingOrigin));
    }

    // packet-handler functions
    public FrozenChunkClientboundPacket(FrozenChunkAction action, BlockPos blockPos) {
        this.action = action;
        this.blockPos = blockPos;
    }

    public FrozenChunkClientboundPacket(FriendlyByteBuf buffer) {
        this.action = buffer.readEnum(FrozenChunkAction.class);
        this.blockPos = buffer.readBlockPos();
    }

    public void encode(FriendlyByteBuf buffer) {
        buffer.writeEnum(this.action);
        buffer.writeBlockPos(this.blockPos);
    }

    // client-side packet-consuming functions
    public void handle(IPayloadContext ctx) {

 ctx.enqueueWork(() -> {
            DistUtil.unsafeRunWhenOn(Dist.CLIENT,
                    () -> () -> {
                        switch (action) {
                            case SET_BUILDING_DESTROYED -> FogOfWarClientEvents.setBuildingDestroyedServerside(blockPos);
                            case SET_BUILDING_BUILT -> FogOfWarClientEvents.setBuildingBuiltServerside(blockPos);
                        }
                    });
        });
    }
}