package com.solegendary.reignofnether.registrars;

import net.minecraft.core.Holder;

import net.minecraft.network.codec.StreamCodec;

import net.minecraft.network.RegistryFriendlyByteBuf;

import com.mojang.serialization.MapCodec;


import net.minecraft.core.registries.Registries;
import com.mojang.serialization.Codec;
import com.solegendary.reignofnether.ReignOfNether;
import com.solegendary.reignofnether.particles.BigVibrationParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.particles.VibrationParticleOption;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import net.neoforged.neoforge.registries.DeferredHolder;

import java.util.function.Function;

public class ParticleRegistrar {

    public static final DeferredRegister<ParticleType<?>> PARTICLES =
            DeferredRegister.create(Registries.PARTICLE_TYPE, ReignOfNether.MOD_ID);

    // mirrors vanilla ParticleTypes' private generic register(...) helper
    // mirrors vanilla ParticleTypes' private generic register(...) helper (1.21 codec model)
    private static <T extends ParticleOptions> DeferredHolder<ParticleType<?>, ParticleType<T>> register(
            String name,
            boolean overrideLimiter,
            Function<ParticleType<T>, MapCodec<T>> codecFactory,
            Function<ParticleType<T>, StreamCodec<? super RegistryFriendlyByteBuf, T>> streamCodecFactory
    ) {
        return PARTICLES.register(name, () -> new ParticleType<T>(overrideLimiter) {
            @Override
            public MapCodec<T> codec() {
                return codecFactory.apply(this);
            }

            @Override
            public StreamCodec<? super RegistryFriendlyByteBuf, T> streamCodec() {
                return streamCodecFactory.apply(this);
            }
        });
    }

    public static final DeferredHolder<ParticleType<?>, ParticleType<BigVibrationParticleOption>> BIG_VIBRATION =
            register("big_vibration", true, (type) -> BigVibrationParticleOption.CODEC, (type) -> BigVibrationParticleOption.STREAM_CODEC);

    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> BIG_ENCHANT =
            PARTICLES.register("big_enchant",
                    () -> new SimpleParticleType(false));

    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> BIG_SOUL_FLAME =
            PARTICLES.register("big_soul_flame",
                    () -> new SimpleParticleType(false));

    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> LEVEL_UP =
            PARTICLES.register("level_up",
                    () -> new SimpleParticleType(false));

    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> FLOATING_CRIT =
            PARTICLES.register("floating_crit",
                    () -> new SimpleParticleType(false));

    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> FLOATING_HEART =
            PARTICLES.register("floating_heart",
                    () -> new SimpleParticleType(false));

    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> MANA =
            PARTICLES.register("mana",
                    () -> new SimpleParticleType(false));

    public static void init(IEventBus bus) {
        PARTICLES.register(bus);
    }
}
