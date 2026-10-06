package com.solegendary.reignofnether.util;

import net.minecraft.resources.ResourceKey;

import com.solegendary.reignofnether.util.MiscUtil;

import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.item.enchantment.ItemEnchantments;

import java.util.HashMap;

/**
 * 1.21 port: enchantments are stored in the DataComponents.ENCHANTMENTS component (ItemEnchantments),
 * keys are Holder&lt;Enchantment&gt; and EnchantmentHelper#setEnchantments(Map, ItemStack) is gone.
 */
public class EnchantmentUtil {

    private static final HashMap<ResourceKey<Enchantment>, Item> level2Enchants = new HashMap<>();

    static {
        level2Enchants.put(Enchantments.SHARPNESS, Items.IRON_AXE);
        level2Enchants.put(Enchantments.QUICK_CHARGE, Items.CROSSBOW);
    }

    public static int getRegularEnchantLevel(Holder<Enchantment> enchantment, ItemStack itemStack) {
        // static-init safe: we compare ResourceKeys, never touching the datapack registry here.
        ResourceKey<Enchantment> key = enchantment.unwrapKey().orElse(null);
        if (key != null && level2Enchants.get(key) == itemStack.getItem()) {
            return 2;
        }
        return 1;
    }

    public static void updateEnchantLevels(LivingEntity entity, boolean regularLevels) {
        scaleSlot(entity.getItemBySlot(EquipmentSlot.CHEST), regularLevels);
        scaleSlot(entity.getItemBySlot(EquipmentSlot.MAINHAND), regularLevels);
    }

    private static void scaleSlot(ItemStack stack, boolean regularLevels) {
        if (stack.isEmpty()) {
            return;
        }
        ItemEnchantments enchants = stack.getOrDefault(DataComponents.ENCHANTMENTS, ItemEnchantments.EMPTY);
        if (enchants.isEmpty()) {
            return;
        }
        ItemEnchantments.Mutable mutable = new ItemEnchantments.Mutable(enchants);
        for (Holder<Enchantment> enchant : enchants.keySet()) {
            mutable.set(enchant, getRegularEnchantLevel(enchant, stack) * (regularLevels ? 1 : 2));
        }
        stack.set(DataComponents.ENCHANTMENTS, mutable.toImmutable());
    }
}
