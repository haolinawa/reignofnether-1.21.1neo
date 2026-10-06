package com.solegendary.reignofnether.guiscreen;









import net.neoforged.neoforge.network.PacketDistributor;
import com.solegendary.reignofnether.ReignOfNether;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.FriendlyByteBuf;
import com.solegendary.reignofnether.ReignOfNether;
import com.solegendary.reignofnether.player.PlayerServerEvents;
import com.solegendary.reignofnether.registrars.PacketHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;

public class TopdownGuiServerboundPacket implements CustomPacketPayload  {
    public static final Type<TopdownGuiServerboundPacket> TYPE = new Type<>(ResourceLocation.parse("reignofnether:topdown_gui_serverbound_packet"));
    public static final StreamCodec<FriendlyByteBuf, TopdownGuiServerboundPacket> STREAM_CODEC =
            StreamCodec.of((buf, payload) -> payload.encode(buf), TopdownGuiServerboundPacket::new);

    @Override
    public Type<? extends CustomPacketPayload> type() { return TYPE; }

    public boolean topdownGuiOpen = false;
    public int playerId = -1; // to track

    // client-side helper functions
    public static void openTopdownGui(int playerId) {
        PacketDistributor.sendToServer(new TopdownGuiServerboundPacket(true, playerId));
    }
    public static void closeTopdownGui(int playerId) {
        Minecraft.getInstance().popGuiLayer();
        PacketDistributor.sendToServer(new TopdownGuiServerboundPacket(false, playerId));
    }


    // packet-handler functions
    public TopdownGuiServerboundPacket(Boolean pos, int playerId) {
        this.topdownGuiOpen = pos;
        this.playerId = playerId;
    }

    public TopdownGuiServerboundPacket(FriendlyByteBuf buffer) {
        this.topdownGuiOpen = buffer.readBoolean();
        this.playerId = buffer.readInt();
    }

    public void encode(FriendlyByteBuf buffer) {
        buffer.writeBoolean(this.topdownGuiOpen);
        buffer.writeInt(this.playerId);
    }


    // server-side packet-consuming functions
    public void handle(IPayloadContext ctx) {
 ctx.enqueueWork(() -> {

            ServerPlayer player = (net.minecraft.server.level.ServerPlayer) ctx.player();
            if (player == null) {
                ReignOfNether.LOGGER.warn("TopdownGuiServerboundPacket: Sender was null");
                return;
            } else if (player.getId() != playerId) {
                ReignOfNether.LOGGER.warn("TopdownGuiServerboundPacket: Tried to process packet from " + player.getName() + " for id: " + this.playerId);
                return;
            }

            if (this.topdownGuiOpen)
                PlayerServerEvents.openTopdownGui(this.playerId);
            else
                PlayerServerEvents.closeTopdownGui(this.playerId);

        });
    }
}