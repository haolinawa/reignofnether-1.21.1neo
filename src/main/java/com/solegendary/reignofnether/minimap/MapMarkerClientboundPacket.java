package com.solegendary.reignofnether.minimap;









import com.solegendary.reignofnether.util.DistUtil;
import com.solegendary.reignofnether.ReignOfNether;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.FriendlyByteBuf;
import net.neoforged.api.distmarker.Dist;
import com.solegendary.reignofnether.util.DistUtil;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;

public class MapMarkerClientboundPacket implements CustomPacketPayload  {
    public static final Type<MapMarkerClientboundPacket> TYPE = new Type<>(ResourceLocation.parse("reignofnether:map_marker_clientbound_packet"));
    public static final StreamCodec<FriendlyByteBuf, MapMarkerClientboundPacket> STREAM_CODEC =
            StreamCodec.of((buf, payload) -> payload.encode(buf), MapMarkerClientboundPacket::new);

    @Override
    public Type<? extends CustomPacketPayload> type() { return TYPE; }

    private final int x;
    private final int z;
    private final String playerName;

    public MapMarkerClientboundPacket(int x, int z, String playerName) {
        this.x = x;
        this.z = z;
        this.playerName = playerName;
    }

    public MapMarkerClientboundPacket(FriendlyByteBuf buffer) {
        this.x = buffer.readInt();
        this.z = buffer.readInt();
        this.playerName = buffer.readUtf();
    }

    public void encode(FriendlyByteBuf buffer) {
        buffer.writeInt(this.x);
        buffer.writeInt(this.z);
        buffer.writeUtf(this.playerName);
    }

    public void handle(IPayloadContext ctx) {
 ctx.enqueueWork(() -> {
            DistUtil.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
                MinimapClientEvents.addMapMarker(x, z, playerName);
            });
        });
    }
}

