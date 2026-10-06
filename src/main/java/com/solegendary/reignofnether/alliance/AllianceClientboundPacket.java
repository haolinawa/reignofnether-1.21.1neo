package com.solegendary.reignofnether.alliance;










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
import com.solegendary.reignofnether.resources.Resources;
import com.solegendary.reignofnether.resources.ResourcesAction;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.neoforged.api.distmarker.Dist;
import com.solegendary.reignofnether.util.DistUtil;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;

public class AllianceClientboundPacket implements CustomPacketPayload  {
    public static final Type<AllianceClientboundPacket> TYPE = new Type<>(ResourceLocation.parse("reignofnether:alliance_clientbound_packet"));
    public static final StreamCodec<FriendlyByteBuf, AllianceClientboundPacket> STREAM_CODEC =
            StreamCodec.of((buf, payload) -> payload.encode(buf), AllianceClientboundPacket::new);

    @Override
    public Type<? extends CustomPacketPayload> type() { return TYPE; }


    // pos is used to identify the building object serverside
    AllianceAction action;
    public String player1;
    public String player2;
    public boolean boolValue;

    public static void addAlliance(String playerName1, String playerName2) {
        PacketDistributor.sendToAllPlayers(new AllianceClientboundPacket(AllianceAction.ACCEPT_REQUEST, playerName1, playerName2, true));
    }

    public static void addPendingAlliance(String toPlayer, String fromPlayer) {
        PacketDistributor.sendToAllPlayers(new AllianceClientboundPacket(AllianceAction.REQUEST, toPlayer, fromPlayer, true));
    }

    public static void cancelPendingAlliance(String toPlayer, String fromPlayer) {
        PacketDistributor.sendToAllPlayers(new AllianceClientboundPacket(AllianceAction.CANCEL_REQUEST, toPlayer, fromPlayer, true));
    }

    public static void removeAlliance(String playerName1, String playerName2) {
        PacketDistributor.sendToAllPlayers(new AllianceClientboundPacket(AllianceAction.DISBAND, playerName1, playerName2, false));
    }

    public static void resetAlliances() {
        PacketDistributor.sendToAllPlayers(new AllianceClientboundPacket(AllianceAction.DISBAND, "", "", true));
    }

    public static void setAllyControl(String playerName1, boolean setValue) {
        PacketDistributor.sendToAllPlayers(new AllianceClientboundPacket(AllianceAction.SET_ALLY_CONTROL, playerName1, "", setValue));
    }

    public AllianceClientboundPacket(
            AllianceAction action,
            String player1,
            String player2,
            boolean boolValue
    ) {
        this.action = action;
        this.player1 = player1;
        this.player2 = player2;
        this.boolValue = boolValue;
    }

    public AllianceClientboundPacket(FriendlyByteBuf buffer) {
        this.action = buffer.readEnum(AllianceAction.class);
        this.player1 = buffer.readUtf();
        this.player2 = buffer.readUtf();
        this.boolValue = buffer.readBoolean();
    }

    public void encode(FriendlyByteBuf buffer) {
        buffer.writeEnum(this.action);
        buffer.writeUtf(this.player1);
        buffer.writeUtf(this.player2);
        buffer.writeBoolean(this.boolValue);
    }

    // server-side packet-consuming functions
    public void handle(IPayloadContext ctx) {

 ctx.enqueueWork(() -> {
            DistUtil.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
                switch (this.action) {

                    case REQUEST -> {
                        // receives a pending alliance request
                        AlliancesClient.addPendingAlliance(player1, player2);
                    }
                    case CANCEL_REQUEST -> {
                        // removes a pending alliance request
                        AlliancesClient.cancelPendingAlliance(player1, player2);
                    }
                    case ACCEPT_REQUEST -> {
                        AlliancesClient.addAlliance(player1, player2);
                    }
                    case DISBAND -> {
                        if (boolValue)
                            AlliancesClient.resetAllAlliances();
                        else
                            AlliancesClient.removeAlliance(player1, player2);
                    }
                    case SET_ALLY_CONTROL -> {
                        if (boolValue)
                            AlliancesClient.playersWithAlliedControl.add(player1);
                        else
                            AlliancesClient.playersWithAlliedControl.remove(player1);
                    }
                }
            });
        });
    }
}
