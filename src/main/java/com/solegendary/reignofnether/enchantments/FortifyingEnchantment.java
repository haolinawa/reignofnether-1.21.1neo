package com.solegendary.reignofnether.enchantments;

import net.minecraft.core.HolderSet;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.item.enchantment.Enchantment;

/**
 * Ported to the 1.21 data-driven enchantment model.
 * (In 1.20.1 this was `extends Enchantment` with Rarity.RARE + a category;
 *  Enchantment is final and a record in 1.21, so we supply an equivalent definition.)
 */
public class FortifyingEnchantment {

    public static Enchantment create() {
        return new Enchantment(
                Component.translatable("enchantment.reignofnether.fortifying"),
                Enchantment.definition(
                        HolderSet.empty(),               // supported items (applied programmatically by the mod)
                        2,                               // weight ~= Rarity.RARE
                        1,                               // max level
                        Enchantment.constantCost(1),     // min cost
                        Enchantment.constantCost(21),    // max cost
                        4,                               // anvil cost
                        EquipmentSlotGroup.ARMOR
                ),
                HolderSet.empty(),                       // exclusive set
                DataComponentMap.EMPTY                   // effects
        );
    }
}
