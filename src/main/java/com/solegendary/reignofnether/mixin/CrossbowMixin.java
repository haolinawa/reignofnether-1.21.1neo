package com.solegendary.reignofnether.mixin;

import com.solegendary.reignofnether.util.MiscUtil;

import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantments;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(CrossbowItem.class)
public class CrossbowMixin {

    @Inject(
            method = "getChargeDuration",
            at = @At("HEAD"),
            cancellable = true
    )
    private static void getChargeDuration(ItemStack pCrossbowStack, net.minecraft.world.entity.LivingEntity pEntity, CallbackInfoReturnable<Integer> cir) {
        // Read the Quick Charge level straight off the stack's enchantment holders instead of resolving
        // Enchantments.QUICK_CHARGE through the (datapack, dynamic) enchantment registry. This method runs
        // for every pillager tick on the client as well, where there is no server registry to fall back on,
        // so the registry lookup could throw and crash the client.
        cir.setReturnValue(35 - 5 * MiscUtil.getEnchantLevel(pCrossbowStack, Enchantments.QUICK_CHARGE));
    }
}