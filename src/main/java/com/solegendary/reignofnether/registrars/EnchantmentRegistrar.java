package com.solegendary.reignofnether.registrars;

import net.minecraft.core.Holder;


import net.minecraft.core.registries.Registries;
import com.solegendary.reignofnether.ReignOfNether;
import com.solegendary.reignofnether.enchantments.*;
import net.minecraft.world.item.enchantment.Enchantment;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import net.neoforged.neoforge.registries.DeferredHolder;

public class EnchantmentRegistrar {

    public static final DeferredRegister<Enchantment> ENCHANTMENTS =
            DeferredRegister.create(Registries.ENCHANTMENT, ReignOfNether.MOD_ID);

    public static final DeferredHolder<Enchantment, Enchantment> VIGOR = ENCHANTMENTS.register("vigor", VigorEnchantment::create);

    public static final DeferredHolder<Enchantment, Enchantment> BREACHING = ENCHANTMENTS.register("breaching", BreachingEnchantment::create);

    public static final DeferredHolder<Enchantment, Enchantment> FORTYIFYING = ENCHANTMENTS.register("fortifying", FortifyingEnchantment::create);

    public static final DeferredHolder<Enchantment, Enchantment> MAIMING = ENCHANTMENTS.register("maiming", MaimingEnchantment::create);

    public static final DeferredHolder<Enchantment, Enchantment> ZEAL = ENCHANTMENTS.register("zeal", ZealEnchantment::create);

    public static final DeferredHolder<Enchantment, Enchantment> GUST = ENCHANTMENTS.register("gust", GustEnchantment::create);

    public static final DeferredHolder<Enchantment, Enchantment> LONGSHOT = ENCHANTMENTS.register("longshot", GustEnchantment::create);

    public static void init(IEventBus bus) {
        ENCHANTMENTS.register(bus);
    }
}