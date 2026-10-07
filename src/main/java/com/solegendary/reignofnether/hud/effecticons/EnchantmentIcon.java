package com.solegendary.reignofnether.hud.effecticons;

import com.solegendary.reignofnether.hud.buttons.Button;
import com.solegendary.reignofnether.keybinds.Keybinding;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import org.jetbrains.annotations.Nullable;

import java.util.List;

// "Buttons" that are just to show off passive upgrades and effects that certain units have such as:
// - Villager professions
// - Enchantments
public class EnchantmentIcon extends Button {

    public static final int ICON_SIZE = 8;
    public final net.minecraft.core.Holder<Enchantment> enchantment;
    // Registry key of the enchantment. Icons are built in static initialisers at class-load time, when no
    // enchantment registry exists yet, so the key is what identifies the icon; matching on it also avoids
    // forcing a registry lookup just to compare two enchantments.
    public final net.minecraft.resources.ResourceKey<Enchantment> enchantmentKey;
    public final EquipmentSlot slot;

    public EnchantmentIcon(net.minecraft.core.Holder<Enchantment> enchantment, net.minecraft.resources.ResourceKey<Enchantment> enchantmentKey, EquipmentSlot slot, ResourceLocation iconRl, @Nullable List<FormattedCharSequence> tooltipLines) {
        super("Passive Icon", ICON_SIZE, iconRl, (Keybinding) null, () -> false, () -> true, () -> true, null, null, tooltipLines);
        this.enchantment = enchantment;
        this.enchantmentKey = enchantmentKey;
        this.slot = slot;
    }

    public EnchantmentIcon(net.minecraft.core.Holder<Enchantment> enchantment, net.minecraft.resources.ResourceKey<Enchantment> enchantmentKey, EquipmentSlot slot, ItemStack iconItem, @Nullable List<FormattedCharSequence> tooltipLines) {
        super("Passive Icon", ICON_SIZE, null, (Keybinding) null, () -> false, () -> true, () -> true, null, null, tooltipLines);
        this.iconItem = iconItem;
        this.enchantment = enchantment;
        this.enchantmentKey = enchantmentKey;
        this.slot = slot;
        this.iconItemScale = 0.75f;
    }
}
