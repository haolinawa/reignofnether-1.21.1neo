package com.solegendary.reignofnether.gamemode;









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
import net.minecraft.network.FriendlyByteBuf;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;

public class GameModeServerboundPacket implements CustomPacketPayload  {
    public static final Type<GameModeServerboundPacket> TYPE = new Type<>(ResourceLocation.parse("reignofnether:game_mode_serverbound_packet"));
    public static final StreamCodec<FriendlyByteBuf, GameModeServerboundPacket> STREAM_CODEC =
            StreamCodec.of((buf, payload) -> payload.encode(buf), GameModeServerboundPacket::new);

    @Override
    public Type<? extends CustomPacketPayload> type() { return TYPE; }


    public GameMode gameMode;

    // copies the gamemode to all other clients
    public static void setAndLockAllClientGameModes(GameMode mode) {
        PacketDistributor.sendToServer(new GameModeServerboundPacket(mode));
    }

    public GameModeServerboundPacket(GameMode gameMode) {
        this.gameMode = gameMode;
    }

    public GameModeServerboundPacket(FriendlyByteBuf buffer) {
        this.gameMode = buffer.readEnum(GameMode.class);
    }

    public void encode(FriendlyByteBuf buffer) {
        buffer.writeEnum(this.gameMode);
    }

    // server-side packet-consuming functions
    public void handle(IPayloadContext ctx) {
 ctx.enqueueWork(() -> {
            ReignOfNether.LOGGER.info("[GameMode] Setting game mode to: {}", this.gameMode);
            GameModeClientboundPacket.setAndLockAllClientGameModes(this.gameMode);
        });
    }
}