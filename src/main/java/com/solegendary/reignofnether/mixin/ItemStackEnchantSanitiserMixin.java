package com.solegendary.reignofnether.mixin;

import com.solegendary.reignofnether.util.MiscUtil;
import net.minecraft.core.Holder;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Repairs items whose ENCHANTMENTS component holds an unusable Holder.
 *
 * <p>Earlier builds wrote the mod's own enchantments as NeoForge DeferredHolders. For a datapack registry
 * such a Holder has no value until the registry exists, and value() throws "Registry not present for ...".
 * Vanilla iterates equipment enchantments every tick (EnchantmentHelper#tickEffects ->
 * runIterationOnEquipment -> holder.value()), so any already-saved item kept crashing its wearer's tick
 * even after the write path was fixed. Sanitising on first access heals those existing saves.
 *
 * <p>Only the broken entries are dropped; everything else on the stack is left alone.
 */
@Mixin(ItemStack.class)
public class ItemStackEnchantSanitiserMixin {

    @Inject(method = "getEnchantments", at = @At("HEAD"))
    private void reignofnether$stripBrokenEnchantments(CallbackInfoReturnable<ItemEnchantments> cir) {
        ItemStack self = (ItemStack) (Object) this;
        if (self.isEmpty())
            return;
        ItemEnchantments enchants = self.get(net.minecraft.core.component.DataComponents.ENCHANTMENTS);
        if (enchants == null || enchants.isEmpty())
            return;

        ItemEnchantments.Mutable mutable = null;
        for (Holder<Enchantment> holder : enchants.keySet()) {
            if (MiscUtil.isUsableEnchantmentHolder(holder))
                continue;
            if (mutable == null)
                mutable = new ItemEnchantments.Mutable(enchants);
            mutable.removeIf(h -> h == holder);
        }
        if (mutable != null)
            self.set(net.minecraft.core.component.DataComponents.ENCHANTMENTS, mutable.toImmutable());
    }
}
