package com.solegendary.reignofnether.gamemode;










import net.neoforged.neoforge.network.PacketDistributor;
import com.solegendary.reignofnether.util.DistUtil;
import com.solegendary.reignofnether.ReignOfNether;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.FriendlyByteBuf;
import com.solegendary.reignofnether.faction.Factions;
import com.solegendary.reignofnether.registrars.PacketHandler;
import com.solegendary.reignofnether.startpos.StartPosClientEvents;
import com.solegendary.reignofnether.startpos.StartPosServerboundPacket;

import net.minecraft.network.FriendlyByteBuf;
import net.neoforged.api.distmarker.Dist;
import com.solegendary.reignofnether.util.DistUtil;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;

public class GameModeClientboundPacket implements CustomPacketPayload  {
    public static final Type<GameModeClientboundPacket> TYPE = new Type<>(ResourceLocation.parse("reignofnether:game_mode_clientbound_packet"));
    public static final StreamCodec<FriendlyByteBuf, GameModeClientboundPacket> STREAM_CODEC =
            StreamCodec.of((buf, payload) -> payload.encode(buf), GameModeClientboundPacket::new);

    @Override
    public Type<? extends CustomPacketPayload> type() { return TYPE; }


    public GameMode gameMode;

    // sets the gamemode of all players
    // unlocked and reset back to
    public static void setAndLockAllClientGameModes(GameMode mode) {
        PacketDistributor.sendToAllPlayers(new GameModeClientboundPacket(mode));
    }

    public GameModeClientboundPacket(GameMode gameMode) {
        this.gameMode = gameMode;
    }

    public GameModeClientboundPacket(FriendlyByteBuf buffer) {
        this.gameMode = buffer.readEnum(GameMode.class);
    }

    public void encode(FriendlyByteBuf buffer) {
        buffer.writeEnum(this.gameMode);
    }

    // server-side packet-consuming functions
    public void handle(IPayloadContext ctx) {

 ctx.enqueueWork(() -> {
            DistUtil.unsafeRunWhenOn(Dist.CLIENT,
                    () -> () -> {
                        if (gameMode != GameMode.NONE) {
                            ClientGameModeHelper.gameModeLocked = true;
                            ClientGameModeHelper.gameMode = this.gameMode;
                            if (gameMode != GameMode.CLASSIC && StartPosClientEvents.hasReservedPos()) {
                                StartPosClientEvents.selectedFaction = Factions.NONE;
                                StartPosServerboundPacket.unreservePos(StartPosClientEvents.getPos().pos);
                            }
                        } else {
                            ClientGameModeHelper.gameModeLocked = false;
                        }
                    });
        });
    }
}
