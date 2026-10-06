package com.solegendary.reignofnether.survival;










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

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;

public class SurvivalClientboundPacket implements CustomPacketPayload  {
    public static final Type<SurvivalClientboundPacket> TYPE = new Type<>(ResourceLocation.parse("reignofnether:survival_clientbound_packet"));
    public static final StreamCodec<FriendlyByteBuf, SurvivalClientboundPacket> STREAM_CODEC =
            StreamCodec.of((buf, payload) -> payload.encode(buf), SurvivalClientboundPacket::new);

    @Override
    public Type<? extends CustomPacketPayload> type() { return TYPE; }


    SurvivalSyncAction action;
    WaveDifficulty difficulty;
    long value;

    public static void enableAndSetDifficulty(WaveDifficulty diff) {
        PacketDistributor.sendToAllPlayers(new SurvivalClientboundPacket(SurvivalSyncAction.ENABLE_AND_SET_DIFFICULTY, diff, 0, 0L));
    }

    public static void setWaveNumber(long waveNumber) {
        PacketDistributor.sendToAllPlayers(new SurvivalClientboundPacket(SurvivalSyncAction.SET_WAVE_NUMBER, WaveDifficulty.EASY, waveNumber, 0L));
    }

    public static void setWaveRandomSeed(long seed) {
        PacketDistributor.sendToAllPlayers(new SurvivalClientboundPacket(SurvivalSyncAction.SET_WAVE_RANDOM_SEED, WaveDifficulty.EASY, seed, 0L));
    }

    public SurvivalClientboundPacket(SurvivalSyncAction action, WaveDifficulty difficulty, long value, long bonusTicks) {
        this.action = action;
        this.difficulty = difficulty;
        this.value = value;
    }

    public SurvivalClientboundPacket(FriendlyByteBuf buffer) {
        this.action = buffer.readEnum(SurvivalSyncAction.class);
        this.difficulty = buffer.readEnum(WaveDifficulty.class);
        this.value = buffer.readLong();
    }

    public void encode(FriendlyByteBuf buffer) {
        buffer.writeEnum(this.action);
        buffer.writeEnum(this.difficulty);
        buffer.writeLong(this.value);
    }

    // server-side packet-consuming functions
    public void handle(IPayloadContext ctx) {

 ctx.enqueueWork(() -> {
            DistUtil.unsafeRunWhenOn(Dist.CLIENT,
                    () -> () -> {
                        switch (action) {
                            case ENABLE_AND_SET_DIFFICULTY -> SurvivalClientEvents.enable(difficulty);
                            case SET_WAVE_NUMBER -> SurvivalClientEvents.setWaveNumber(value);
                            case SET_WAVE_RANDOM_SEED -> SurvivalClientEvents.setRandomSeed(value);
                        }
                    });
        });
    }
}
