package com.solegendary.reignofnether.registrars;

import net.minecraft.core.Holder;


import net.minecraft.core.registries.Registries;
import com.solegendary.reignofnether.ReignOfNether;
import com.solegendary.reignofnether.guiscreen.TopdownGuiContainer;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import net.neoforged.neoforge.registries.DeferredHolder;

public class ContainerRegistrar {

    public static final DeferredRegister<MenuType<?>> CONTAINERS = DeferredRegister.create(Registries.MENU,
            ReignOfNether.MOD_ID);

    public static final DeferredHolder<MenuType<?>, MenuType<TopdownGuiContainer>> TOPDOWNGUI_CONTAINER = CONTAINERS
            .register("topdowngui_container", () -> new MenuType<>(TopdownGuiContainer::new, FeatureFlagSet.of()));

    public static void init(IEventBus bus) {
        CONTAINERS.register(bus);
    }
}
