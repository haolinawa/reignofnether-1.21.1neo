package com.solegendary.reignofnether.registrars;

import net.minecraft.core.Holder;


import net.minecraft.core.registries.Registries;
import com.solegendary.reignofnether.ReignOfNether;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import net.neoforged.neoforge.registries.DeferredHolder;

public class SoundRegistrar {

    // Note for some reason mp3 files from the AOE2 resources folder do not work when converted to .ogg
    // Instead try rerecording them on OBS and converting the .mkv to .ogg

    public static final DeferredRegister<SoundEvent> SOUND_EVENTS =
            DeferredRegister.create(Registries.SOUND_EVENT, ReignOfNether.MOD_ID);

    public static final DeferredHolder<SoundEvent, SoundEvent> UNDER_ATTACK =
            SOUND_EVENTS.register("under_attack", () ->
                    SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(ReignOfNether.MOD_ID, "under_attack")));

    public static final DeferredHolder<SoundEvent, SoundEvent> VICTORY =
            SOUND_EVENTS.register("victory", () ->
                    SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(ReignOfNether.MOD_ID, "victory")));

    public static final DeferredHolder<SoundEvent, SoundEvent> DEFEAT =
            SOUND_EVENTS.register("defeat", () ->
                    SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(ReignOfNether.MOD_ID, "defeat")));

    public static final DeferredHolder<SoundEvent, SoundEvent> ALLY =
            SOUND_EVENTS.register("ally", () ->
                    SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(ReignOfNether.MOD_ID, "ally")));

    public static final DeferredHolder<SoundEvent, SoundEvent> ENEMY =
            SOUND_EVENTS.register("enemy", () ->
                    SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(ReignOfNether.MOD_ID, "enemy")));

    public static final DeferredHolder<SoundEvent, SoundEvent> CHAT =
            SOUND_EVENTS.register("chat", () ->
                    SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(ReignOfNether.MOD_ID, "chat")));

    public static final DeferredHolder<SoundEvent, SoundEvent> SELL_ITEM =
            SOUND_EVENTS.register("sell_item", () ->
                    SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(ReignOfNether.MOD_ID, "sell_item")));

    public static final DeferredHolder<SoundEvent, SoundEvent> DAWN_ROOSTER =
            SOUND_EVENTS.register("dawn_rooster", () ->
                    SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(ReignOfNether.MOD_ID, "dawn_rooster")));

    public static final DeferredHolder<SoundEvent, SoundEvent> DUSK_WOLF =
            SOUND_EVENTS.register("dusk_wolf", () ->
                    SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(ReignOfNether.MOD_ID, "dusk_wolf")));

    public static final DeferredHolder<SoundEvent, SoundEvent> MAIN_MENU =
            SOUND_EVENTS.register("main_menu", () ->
                    SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(ReignOfNether.MOD_ID, "main_menu")));

    public static final DeferredHolder<SoundEvent, SoundEvent> BLOODLUST =
            SOUND_EVENTS.register("bloodlust", () ->
                    SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(ReignOfNether.MOD_ID, "bloodlust")));

    public static final DeferredHolder<SoundEvent, SoundEvent> BLOODLUST_2 =
            SOUND_EVENTS.register("bloodlust_2", () ->
                    SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(ReignOfNether.MOD_ID, "bloodlust_2")));

    public static final DeferredHolder<SoundEvent, SoundEvent> HEROISM =
            SOUND_EVENTS.register("heroism", () ->
                    SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(ReignOfNether.MOD_ID, "heroism")));

    public static final DeferredHolder<SoundEvent, SoundEvent> BLOOD_MOON_SONG =
            SOUND_EVENTS.register("blood_moon", () ->
                    SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(ReignOfNether.MOD_ID, "blood_moon")));

    public static final DeferredHolder<SoundEvent, SoundEvent> VILLAGER_CALM_THEME_SONG =
            SOUND_EVENTS.register("sharpened_grassblades_calm", () ->
                    SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(ReignOfNether.MOD_ID, "sharpened_grassblades_calm")));

    public static final DeferredHolder<SoundEvent, SoundEvent> MONSTER_CALM_THEME_SONG =
            SOUND_EVENTS.register("life_scourge_calm", () ->
                    SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(ReignOfNether.MOD_ID, "life_scourge_calm")));

    public static final DeferredHolder<SoundEvent, SoundEvent> PIGLIN_CALM_THEME_SONG =
            SOUND_EVENTS.register("soul_resonance_calm", () ->
                    SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(ReignOfNether.MOD_ID, "soul_resonance_calm")));

    public static final DeferredHolder<SoundEvent, SoundEvent> WRAITH_AMBIENT =
            SOUND_EVENTS.register("wraith_ambient", () ->
                    SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(ReignOfNether.MOD_ID, "wraith_ambient")));

    public static final DeferredHolder<SoundEvent, SoundEvent> WRAITH_HURT =
            SOUND_EVENTS.register("wraith_hurt", () ->
                    SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(ReignOfNether.MOD_ID, "wraith_hurt")));

    public static final DeferredHolder<SoundEvent, SoundEvent> WRAITH_DEATH =
            SOUND_EVENTS.register("wraith_death", () ->
                    SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(ReignOfNether.MOD_ID, "wraith_death")));

    public static final DeferredHolder<SoundEvent, SoundEvent> WRAITH_FEAR =
            SOUND_EVENTS.register("wraith_fear", () ->
                    SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(ReignOfNether.MOD_ID, "wraith_fear")));

    public static final DeferredHolder<SoundEvent, SoundEvent> WRAITH_POSSESS_CHANNEL =
            SOUND_EVENTS.register("wraith_possess_channel", () ->
                    SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(ReignOfNether.MOD_ID, "wraith_possess_channel")));

    public static final DeferredHolder<SoundEvent, SoundEvent> WRAITH_POSSESS_PARTIAL =
            SOUND_EVENTS.register("wraith_possess_partial", () ->
                    SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(ReignOfNether.MOD_ID, "wraith_possess_partial")));

    public static final DeferredHolder<SoundEvent, SoundEvent> WRAITH_POSSESS_FULL =
            SOUND_EVENTS.register("wraith_possess_full", () ->
                    SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(ReignOfNether.MOD_ID, "wraith_possess_full")));

    public static final DeferredHolder<SoundEvent, SoundEvent> WRETCHED_WRAITH_AMBIENT =
            SOUND_EVENTS.register("wretchedwraith_ambient", () ->
                    SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(ReignOfNether.MOD_ID, "wretchedwraith_ambient")));

    public static final DeferredHolder<SoundEvent, SoundEvent> WRETCHED_WRAITH_HURT =
            SOUND_EVENTS.register("wretchedwraith_hurt", () ->
                    SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(ReignOfNether.MOD_ID, "wretchedwraith_hurt")));

    public static final DeferredHolder<SoundEvent, SoundEvent> WRETCHED_WRAITH_DEATH =
            SOUND_EVENTS.register("wretchedwraith_death", () ->
                    SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(ReignOfNether.MOD_ID, "wretchedwraith_death")));

    public static final DeferredHolder<SoundEvent, SoundEvent> WRETCHED_WRAITH_ATTACK_QUIET =
            SOUND_EVENTS.register("wretchedwraith_attack_quiet", () ->
                    SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(ReignOfNether.MOD_ID, "wretchedwraith_attack_quiet")));

    public static final DeferredHolder<SoundEvent, SoundEvent> WRETCHED_WRAITH_ATTACK_LOUD =
            SOUND_EVENTS.register("wretchedwraith_attack_loud", () ->
                    SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(ReignOfNether.MOD_ID, "wretchedwraith_attack_loud")));

    public static final DeferredHolder<SoundEvent, SoundEvent> WRETCHED_WRAITH_TELEPORT_START =
            SOUND_EVENTS.register("wretchedwraith_teleport_start", () ->
                    SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(ReignOfNether.MOD_ID, "wretchedwraith_teleport_start")));

    public static final DeferredHolder<SoundEvent, SoundEvent> WRETCHED_WRAITH_TELEPORT_END =
            SOUND_EVENTS.register("wretchedwraith_teleport_end", () ->
                    SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(ReignOfNether.MOD_ID, "wretchedwraith_teleport_end")));

    public static final DeferredHolder<SoundEvent, SoundEvent> WRETCHED_WRAITH_BLIZZARD =
            SOUND_EVENTS.register("wretchedwraith_blizzard", () ->
                    SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(ReignOfNether.MOD_ID, "wretchedwraith_blizzard")));

    public static final DeferredHolder<SoundEvent, SoundEvent> WILDFIRE_MOLTEN_BOMB =
            SOUND_EVENTS.register("wildfire_molten_bomb", () ->
                    SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(ReignOfNether.MOD_ID, "wildfire_molten_bomb")));

    public static final DeferredHolder<SoundEvent, SoundEvent> WILDFIRE_SCORCHING_GAZE_START =
            SOUND_EVENTS.register("wildfire_scorching_gaze_start", () ->
                    SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(ReignOfNether.MOD_ID, "wildfire_scorching_gaze_start")));

    public static final DeferredHolder<SoundEvent, SoundEvent> WILDFIRE_SCORCHING_GAZE_END =
            SOUND_EVENTS.register("wildfire_scorching_gaze_end", () ->
                    SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(ReignOfNether.MOD_ID, "wildfire_scorching_gaze_end")));

    public static final DeferredHolder<SoundEvent, SoundEvent> WILDFIRE_HURT =
            SOUND_EVENTS.register("wildfire_hurt", () ->
                    SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(ReignOfNether.MOD_ID, "wildfire_hurt")));

    public static final DeferredHolder<SoundEvent, SoundEvent> WILDFIRE_DEATH =
            SOUND_EVENTS.register("wildfire_death", () ->
                    SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(ReignOfNether.MOD_ID, "wildfire_death")));

    public static final DeferredHolder<SoundEvent, SoundEvent> WILDFIRE_AMBIENT =
            SOUND_EVENTS.register("wildfire_ambient", () ->
                    SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(ReignOfNether.MOD_ID, "wildfire_ambient")));

    public static final DeferredHolder<SoundEvent, SoundEvent> WILDFIRE_SOULS_AFLAME =
            SOUND_EVENTS.register("wildfire_souls_aflame", () ->
                    SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(ReignOfNether.MOD_ID, "wildfire_souls_aflame")));

    public static final DeferredHolder<SoundEvent, SoundEvent> PIGLIN_MERCHANT_LOOT_EXPLOSION =
            SOUND_EVENTS.register("piglin_merchant_loot_explosion", () ->
                    SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(ReignOfNether.MOD_ID, "piglin_merchant_loot_explosion")));

    public static final DeferredHolder<SoundEvent, SoundEvent> WINDCALLER_HURT =
            SOUND_EVENTS.register("windcaller_hurt", () ->
                    SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(ReignOfNether.MOD_ID, "windcaller_hurt")));

    public static final DeferredHolder<SoundEvent, SoundEvent> WINDCALLER_DEATH =
            SOUND_EVENTS.register("windcaller_death", () ->
                    SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(ReignOfNether.MOD_ID, "windcaller_death")));

    public static final DeferredHolder<SoundEvent, SoundEvent> WINDCALLER_AMBIENT =
            SOUND_EVENTS.register("windcaller_ambient", () ->
                    SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(ReignOfNether.MOD_ID, "windcaller_ambient")));

    public static final DeferredHolder<SoundEvent, SoundEvent> WINDCALLER_WIND_ATTACK =
            SOUND_EVENTS.register("windcaller_wind_attack", () ->
                    SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(ReignOfNether.MOD_ID, "windcaller_wind_attack")));

    public static final DeferredHolder<SoundEvent, SoundEvent> WINDCALLER_LIFT =
            SOUND_EVENTS.register("windcaller_lift", () ->
                    SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(ReignOfNether.MOD_ID, "windcaller_lift")));

    public static final DeferredHolder<SoundEvent, SoundEvent> WINDCALLER_YELL =
            SOUND_EVENTS.register("windcaller_yell", () ->
                    SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(ReignOfNether.MOD_ID, "windcaller_yell")));

    public static final DeferredHolder<SoundEvent, SoundEvent> ITEM_DROP_COMMON =
            SOUND_EVENTS.register("item_drop_common", () ->
                    SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(ReignOfNether.MOD_ID, "item_drop_common")));

    public static final DeferredHolder<SoundEvent, SoundEvent> ITEM_DROP_RARE =
            SOUND_EVENTS.register("item_drop_rare", () ->
                    SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(ReignOfNether.MOD_ID, "item_drop_rare")));

    public static final DeferredHolder<SoundEvent, SoundEvent> ITEM_DROP_LEGENDARY =
            SOUND_EVENTS.register("item_drop_legendary", () ->
                    SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(ReignOfNether.MOD_ID, "item_drop_legendary")));

    public static final DeferredHolder<SoundEvent, SoundEvent> BUILDING_SMASH =
            SOUND_EVENTS.register("building_smash", () ->
                    SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(ReignOfNether.MOD_ID, "building_smash")));

    public static final DeferredHolder<SoundEvent, SoundEvent> BUZZY_NEST =
            SOUND_EVENTS.register("buzzy_nest", () ->
                    SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(ReignOfNether.MOD_ID, "buzzy_nest")));

    public static final DeferredHolder<SoundEvent, SoundEvent> CRITICAL_HIT =
            SOUND_EVENTS.register("critical_hit", () ->
                    SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(ReignOfNether.MOD_ID, "critical_hit")));

    public static final DeferredHolder<SoundEvent, SoundEvent> GHOST_CLOAK =
            SOUND_EVENTS.register("ghost_cloak", () ->
                    SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(ReignOfNether.MOD_ID, "ghost_cloak")));

    public static final DeferredHolder<SoundEvent, SoundEvent> GONG_OF_WEAKNING =
            SOUND_EVENTS.register("gong_of_weakning", () ->
                    SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(ReignOfNether.MOD_ID, "gong_of_weakning")));

    public static final DeferredHolder<SoundEvent, SoundEvent> ICE_WAND =
            SOUND_EVENTS.register("ice_wand", () ->
                    SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(ReignOfNether.MOD_ID, "ice_wand")));

    public static final DeferredHolder<SoundEvent, SoundEvent> SHADOW_SHIFTER =
            SOUND_EVENTS.register("shadow_shifter", () ->
                    SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(ReignOfNether.MOD_ID, "shadow_shifter")));

    public static final DeferredHolder<SoundEvent, SoundEvent> TOME_OF_DUPLICATION =
            SOUND_EVENTS.register("tome_of_duplication", () ->
                    SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(ReignOfNether.MOD_ID, "tome_of_duplication")));

    public static final DeferredHolder<SoundEvent, SoundEvent> TOTEM_PLACE =
            SOUND_EVENTS.register("totem_place", () ->
                    SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(ReignOfNether.MOD_ID, "totem_place")));

    public static final DeferredHolder<SoundEvent, SoundEvent> WAR_HORN =
            SOUND_EVENTS.register("war_horn", () ->
                    SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(ReignOfNether.MOD_ID, "war_horn")));

    public static final DeferredHolder<SoundEvent, SoundEvent> POTION_POP =
            SOUND_EVENTS.register("potion_pop", () ->
                    SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(ReignOfNether.MOD_ID, "potion_pop")));

    public static void init(IEventBus bus) {
        SOUND_EVENTS.register(bus);
    }
}
