package com.solegendary.reignofnether.fogofwar;









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
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;

public class FogOfWarServerboundPacket implements CustomPacketPayload  {
    public static final Type<FogOfWarServerboundPacket> TYPE = new Type<>(ResourceLocation.parse("reignofnether:fog_of_war_serverbound_packet"));
    public static final StreamCodec<FriendlyByteBuf, FogOfWarServerboundPacket> STREAM_CODEC =
            StreamCodec.of((buf, payload) -> payload.encode(buf), FogOfWarServerboundPacket::new);

    @Override
    public Type<? extends CustomPacketPayload> type() { return TYPE; }


    boolean enable;

    public static void setServerFog(boolean enable) {
        Minecraft MC = Minecraft.getInstance();
        if (MC.player != null)
            PacketDistributor.sendToServer(new FogOfWarServerboundPacket(enable));
    }

    // packet-handler functions
    public FogOfWarServerboundPacket(boolean enable) {
        this.enable = enable;
    }

    public FogOfWarServerboundPacket(FriendlyByteBuf buffer) {
        this.enable = buffer.readBoolean();
    }

    public void encode(FriendlyByteBuf buffer) {
        buffer.writeBoolean(this.enable);
    }

    // server-side packet-consuming functions
    public void handle(IPayloadContext ctx) {
 ctx.enqueueWork(() -> {

            ServerPlayer player = (net.minecraft.server.level.ServerPlayer) ctx.player();
            if (player == null) {
                ReignOfNether.LOGGER.warn("FogOfWarServerboundPacket: Sender was null");
                return;
            } else if (!player.hasPermissions(4)) {
                ReignOfNether.LOGGER.warn("FogOfWarServerboundPacket: Tried to process packet from " + player.getName() + " with insufficient permissions");
                return;
            }

            ReignOfNether.LOGGER.info("[FogOfWar] {} set fog of war to {}", player.getName(), enable);

            FogOfWarServerEvents.setEnabled(enable);
        });
    }
}