package com.solegendary.reignofnether.alliance;









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
import com.solegendary.reignofnether.util.MiscUtil;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;

public class AllianceServerboundPacket implements CustomPacketPayload  {
    public static final Type<AllianceServerboundPacket> TYPE = new Type<>(ResourceLocation.parse("reignofnether:alliance_serverbound_packet"));
    public static final StreamCodec<FriendlyByteBuf, AllianceServerboundPacket> STREAM_CODEC =
            StreamCodec.of((buf, payload) -> payload.encode(buf), AllianceServerboundPacket::new);

    @Override
    public Type<? extends CustomPacketPayload> type() { return TYPE; }


    AllianceAction action;
    public String targetPlayerName;
    public boolean boolValue;

    public static void doAllianceAction(AllianceAction action, String targetPlayerName) {
        PacketDistributor.sendToServer(new AllianceServerboundPacket(
                action,
                targetPlayerName,
                false
        ));
    }

    public static void doAllianceAction(AllianceAction action, boolean value) {
        PacketDistributor.sendToServer(new AllianceServerboundPacket(
                action,
                "",
                value
        ));
    }

    public AllianceServerboundPacket(AllianceAction action, String targetPlayerName, boolean boolValue) {
        this.action = action;
        this.targetPlayerName = targetPlayerName;
        this.boolValue = boolValue;
    }

    public AllianceServerboundPacket(FriendlyByteBuf buffer) {
        this.action = buffer.readEnum(AllianceAction.class);
        this.targetPlayerName = buffer.readUtf();
        this.boolValue = buffer.readBoolean();
    }

    public void encode(FriendlyByteBuf buffer) {
        buffer.writeEnum(this.action);
        buffer.writeUtf(this.targetPlayerName);
        buffer.writeBoolean(this.boolValue);
    }

    // server-side packet-consuming functions
    public void handle(IPayloadContext ctx) {
 ctx.enqueueWork(() -> {

            ServerPlayer player = (net.minecraft.server.level.ServerPlayer) ctx.player();
            if (player == null) {
                ReignOfNether.LOGGER.warn("AbilityServerboundPacket: Sender was null");
                return;
            }
            ReignOfNether.LOGGER.info("[Alliance] {} performed {} (target: {}, boolValue: {})", player.getName(), action, targetPlayerName, boolValue);
            switch (action) {
                case REQUEST -> MiscUtil.runPlayerCommand(player, "ally " + targetPlayerName);
                case CANCEL_REQUEST -> MiscUtil.runPlayerCommand(player, "allycancelrequest " + targetPlayerName);
                case ACCEPT_REQUEST -> MiscUtil.runPlayerCommand(player, "allyconfirm " + targetPlayerName);
                case DISBAND -> MiscUtil.runPlayerCommand(player, "disband " + targetPlayerName);
                case SET_ALLY_CONTROL -> MiscUtil.runPlayerCommand(player, "allycontrol " + boolValue);
            }
        });
    }
}
