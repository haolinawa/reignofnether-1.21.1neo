package com.solegendary.reignofnether.rtsmap;










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
import net.minecraft.network.FriendlyByteBuf;
import net.neoforged.api.distmarker.Dist;
import com.solegendary.reignofnether.util.DistUtil;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.function.Supplier;

public class RTSMapInfoClientboundPacket implements CustomPacketPayload  {
    public static final Type<RTSMapInfoClientboundPacket> TYPE = new Type<>(ResourceLocation.parse("reignofnether:r_t_s_map_info_clientbound_packet"));
    public static final StreamCodec<FriendlyByteBuf, RTSMapInfoClientboundPacket> STREAM_CODEC =
            StreamCodec.of((buf, payload) -> payload.encode(buf), RTSMapInfoClientboundPacket::new);

    @Override
    public Type<? extends CustomPacketPayload> type() { return TYPE; }


    private final RTSMapInfoAction action;
    private final String value;

    public static void sendValue(RTSMapInfoAction action, String value) {
        PacketDistributor.sendToAllPlayers(new RTSMapInfoClientboundPacket(action, value));
    }

    public RTSMapInfoClientboundPacket(RTSMapInfoAction action, String value) {
        this.action = action;
        this.value = value;
    }

    public RTSMapInfoClientboundPacket(FriendlyByteBuf buffer) {
        this.action = buffer.readEnum(RTSMapInfoAction.class);
        this.value = buffer.readUtf();
    }

    public void encode(FriendlyByteBuf buffer) {
        buffer.writeEnum(this.action);
        buffer.writeUtf(this.value);
    }

    public void handle(IPayloadContext ctx) {
 ctx.enqueueWork(() -> {
            DistUtil.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
                switch (action) {
                    case SET_MODE -> RTSMapInfoClientEvents.selectedMode = value;
                    case ADD_MODE -> {
                        if (!RTSMapInfoClientEvents.modeNames.contains(value))
                            RTSMapInfoClientEvents.modeNames.add(value);
                    }
                    case SET_MAP_NAME -> RTSMapInfoClientEvents.mapName = value;
                    case SET_DESCRIPTION -> RTSMapInfoClientEvents.description = value;
                    case ADD_AUTHOR -> RTSMapInfoClientEvents.authors.add(value);
                    case SET_VERSION -> RTSMapInfoClientEvents.version = value;
                }
            });
        });
    }
}
