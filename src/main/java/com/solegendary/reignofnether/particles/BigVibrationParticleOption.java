package com.solegendary.reignofnether.particles;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.solegendary.reignofnether.registrars.ParticleRegistrar;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.level.gameevent.PositionSource;

/**
 * 1.21 port: ParticleOptions no longer carries a Deserializer; codec() is a MapCodec<T>
 * and streamCodec() is a StreamCodec.
 */
public class BigVibrationParticleOption implements ParticleOptions {

    public static final MapCodec<BigVibrationParticleOption> CODEC = RecordCodecBuilder.mapCodec(
            inst -> inst.group(
                    PositionSource.CODEC.fieldOf("destination").forGetter(o -> o.destination),
                    Codec.INT.fieldOf("arrival_in_ticks").forGetter(o -> o.arrivalInTicks)
            ).apply(inst, BigVibrationParticleOption::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, BigVibrationParticleOption> STREAM_CODEC =
            StreamCodec.composite(
                    PositionSource.STREAM_CODEC, BigVibrationParticleOption::getDestination,
                    ByteBufCodecs.VAR_INT, BigVibrationParticleOption::getArrivalInTicks,
                    BigVibrationParticleOption::new);

    private final PositionSource destination;
    private final int arrivalInTicks;

    public BigVibrationParticleOption(PositionSource destination, int arrivalInTicks) {
        this.destination = destination;
        this.arrivalInTicks = arrivalInTicks;
    }

    @Override
    public ParticleType<BigVibrationParticleOption> getType() {
        return ParticleRegistrar.BIG_VIBRATION.get();
    }

    public PositionSource getDestination() {
        return this.destination;
    }

    public int getArrivalInTicks() {
        return this.arrivalInTicks;
    }
}
