package com.solegendary.reignofnether.mixin;

import com.solegendary.reignofnether.unit.interfaces.Unit;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.animal.PolarBear;
import net.minecraft.world.entity.animal.Wolf;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Random;

// 1.21.1: Wolf/PolarBear no longer override doHurtTarget (only Mob does),
// so we hook Mob.doHurtTarget and gate on the attacker being a wolf/polar bear
// to keep the original 1.20.1 behaviour (make wolves/bears respect evasion chance)
@Mixin(Mob.class)
public abstract class MobDoHurtTargetMixin {

    private static final Random RANDOM = new Random();

    @Inject(
            method = "doHurtTarget",
            at = @At("HEAD"),
            cancellable = true
    )
    public void doHurtTarget(Entity pEntity, CallbackInfoReturnable<Boolean> cir) {
        Object self = this;
        if (!(self instanceof Wolf) && !(self instanceof PolarBear))
            return;
        if (pEntity instanceof Unit unit && unit.getEvasionChance() > 0)
            if (RANDOM.nextFloat() < unit.getEvasionChance())
                cir.setReturnValue(false);
    }
}
