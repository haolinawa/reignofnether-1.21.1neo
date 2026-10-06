package com.solegendary.reignofnether;


import com.solegendary.reignofnether.util.DistUtil;
import com.solegendary.reignofnether.building.Buildings;
import com.solegendary.reignofnether.building.production.ProductionItems;
import com.solegendary.reignofnether.commands.rtsapi.ResourceObjectiveCriteria;
import com.solegendary.reignofnether.commands.rtsapi.argument.BuildingArgument;
import com.solegendary.reignofnether.commands.rtsapi.argument.PlayerNameArgument;
import com.solegendary.reignofnether.commands.rtsapi.argument.UnitArgument;
import com.solegendary.reignofnether.commands.rtsapi.argument.options.BuildingSelectorOptions;
import com.solegendary.reignofnether.config.ReignOfNetherCommonConfigs;
import com.solegendary.reignofnether.faction.Factions;
import com.solegendary.reignofnether.hud.custombutton.CustomButton;
import com.solegendary.reignofnether.hud.custombutton.CustomButtonActions;
import com.solegendary.reignofnether.hud.custombutton.CustomButtonMappingManager;
import com.solegendary.reignofnether.hud.custombutton.CustomButtonServerEvents;
import com.solegendary.reignofnether.items.CreativeModeTabsRegistrar;
import com.solegendary.reignofnether.registrars.AttributeRegistrar;
import com.solegendary.reignofnether.registrars.BlockEntityRegistrar;
import com.solegendary.reignofnether.registrars.BlockRegistrar;
import com.solegendary.reignofnether.registrars.ClientEventRegistrar;
import com.solegendary.reignofnether.registrars.CommandArgumentRegistrar;
import com.solegendary.reignofnether.registrars.ContainerRegistrar;
import com.solegendary.reignofnether.registrars.EnchantmentRegistrar;
import com.solegendary.reignofnether.registrars.EntityRegistrar;
import com.solegendary.reignofnether.registrars.GameRuleRegistrar;
import com.solegendary.reignofnether.registrars.ItemRegistrar;
import com.solegendary.reignofnether.registrars.MobEffectRegistrar;
import com.solegendary.reignofnether.registrars.ParticleRegistrar;
import com.solegendary.reignofnether.registrars.ServerEventRegistrar;
import com.solegendary.reignofnether.registrars.SoundRegistrar;
import com.solegendary.reignofnether.resources.ResourceCosts;

import net.minecraft.commands.synchronization.ArgumentTypeInfos;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import com.solegendary.reignofnether.util.DistUtil;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.AddReloadListenerEvent;
import net.neoforged.neoforge.registries.DataPackRegistryEvent;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

// The value here should match an entry in the META-INF/neoforge.mods.toml file
@Mod(ReignOfNether.MOD_ID)
public class ReignOfNether {
    public static final Logger LOGGER = LogManager.getLogger();
    public static final String MOD_ID = "reignofnether";
    public static final String VERSION_STRING = "1.5.0-1.21.1-beta-1";

    public ReignOfNether(IEventBus modBus, ModContainer container, Dist dist) {
        EnchantmentRegistrar.init(modBus);
        AttributeRegistrar.init(modBus);
        ItemRegistrar.init(modBus);
        EntityRegistrar.init(modBus);
        ContainerRegistrar.init(modBus);
        SoundRegistrar.init(modBus);
        BlockRegistrar.init(modBus);
        BlockEntityRegistrar.init(modBus);
        GameRuleRegistrar.init();
        Buildings.init();
        ProductionItems.init();
        MobEffectRegistrar.init(modBus);
        ParticleRegistrar.init(modBus);
        CommandArgumentRegistrar.init(modBus);
        CustomButtonActions.init(modBus);
        BuildingSelectorOptions.bootStrap();
        ResourceObjectiveCriteria.init();
        CreativeModeTabsRegistrar.init(modBus);

        final ClientEventRegistrar clientRegistrar = new ClientEventRegistrar();
        DistUtil.runWhenOn(Dist.CLIENT, () -> clientRegistrar::registerClientEvents);

        final ServerEventRegistrar serverRegistrar = new ServerEventRegistrar();
        DistUtil.runWhenOn(Dist.DEDICATED_SERVER, () -> serverRegistrar::registerServerEvents);

        modBus.addListener(ReignOfNether::init);
        modBus.addListener(ReignOfNether::loadDatapacks);
        NeoForge.EVENT_BUS.addListener(ReignOfNether::reloadListener);

        container.registerConfig(ModConfig.Type.COMMON, ReignOfNetherCommonConfigs.SPEC,
                "reignofnether-common-" + VERSION_STRING + ".toml");

        // client-only config
        DistUtil.unsafeRunWhenOn(Dist.CLIENT, () -> () -> ClientModConfigs.registerClientConfigs(container));
    }

    @SubscribeEvent
    public static void init(FMLCommonSetupEvent event) {
        ResourceCosts.deferredLoadResourceCosts();
        event.enqueueWork(() -> {
            Factions.register();

            ArgumentTypeInfos.registerByClass(BuildingArgument.class, CommandArgumentRegistrar.BUILDING_ARGUMENT.get());
            ArgumentTypeInfos.registerByClass(PlayerNameArgument.class, CommandArgumentRegistrar.PLAYER_NAME_ARGUMENT.get());
            ArgumentTypeInfos.registerByClass(UnitArgument.class, CommandArgumentRegistrar.UNIT_ARGUMENT.get());
        });
    }

    @SubscribeEvent
    public static void loadDatapacks(DataPackRegistryEvent.NewRegistry evt) {
        evt.dataPackRegistry(
            CustomButtonServerEvents.CUSTOM_BUTTON_REGISTRY_KEY,
            CustomButton.CODEC,
            CustomButton.CODEC
        );
    }

    public static void reloadListener(AddReloadListenerEvent evt) {
        evt.addListener(new CustomButtonMappingManager());
    }

    // ------------------------------------------------------------------
    // NOTE (port): The original 1.20.1 build hooked Forge's internal FML
    // handshake channel ("NetworkConstants.handshakeChannel") to send an
    // S2CReset packet that forced a client-side registry reset
    // (NetworkHooks.registerClientLoginChannel / GameData.revertToFrozen /
    // removal of the "forge:forge_fixes" pipeline). None of these internals
    // exist in NeoForge 1.21.1. This mechanism must be re-implemented with
    // NeoForge's configuration-phase networking (ConfigurationTask) or a
    // custom payload channel. See PORT_REPORT for details.
    // ------------------------------------------------------------------
}
