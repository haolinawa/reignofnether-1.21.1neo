package com.solegendary.reignofnether.survival;









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

public class SurvivalServerboundPacket implements CustomPacketPayload  {
    public static final Type<SurvivalServerboundPacket> TYPE = new Type<>(ResourceLocation.parse("reignofnether:survival_serverbound_packet"));
    public static final StreamCodec<FriendlyByteBuf, SurvivalServerboundPacket> STREAM_CODEC =
            StreamCodec.of((buf, payload) -> payload.encode(buf), SurvivalServerboundPacket::new);

    @Override
    public Type<? extends CustomPacketPayload> type() { return TYPE; }


    public WaveDifficulty difficulty;
    public int waveNumber;

    // copies the gamemode to all other clients
    public static void startSurvivalMode(WaveDifficulty mode) {
        PacketDistributor.sendToServer(new SurvivalServerboundPacket(mode, 0));
    }

    // copies the gamemode to all other clients
    public static void setWaveNumber(int number) {
        if (number > 0)
            PacketDistributor.sendToServer(new SurvivalServerboundPacket(WaveDifficulty.BEGINNER, number));
    }

    public SurvivalServerboundPacket(WaveDifficulty gameMode, int waveNumber) {
        this.difficulty = gameMode;
        this.waveNumber = waveNumber;
    }

    public SurvivalServerboundPacket(FriendlyByteBuf buffer) {
        this.difficulty = buffer.readEnum(WaveDifficulty.class);
        this.waveNumber = buffer.readInt();
    }

    public void encode(FriendlyByteBuf buffer) {
        buffer.writeEnum(this.difficulty);
        buffer.writeInt(this.waveNumber);
    }

    // server-side packet-consuming functions
    public void handle(IPayloadContext ctx) {
 ctx.enqueueWork(() -> {
            if (this.waveNumber <= 0) {
                ReignOfNether.LOGGER.info("[Survival] Enabling survival mode with difficulty: {}", difficulty);
                SurvivalServerEvents.enable(difficulty);
            } else {
                ReignOfNether.LOGGER.info("[Survival] Setting wave number to: {}", waveNumber);
                SurvivalServerEvents.setWaveNumber(waveNumber);
            }
        });
    }
}