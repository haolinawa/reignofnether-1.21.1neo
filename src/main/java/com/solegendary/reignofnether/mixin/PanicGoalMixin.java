package com.solegendary.reignofnether.mixin;

import net.minecraft.world.entity.ai.goal.PanicGoal;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PanicGoal.class)
public class PanicGoalMixin {

    // 1.21.1: speedModifier is final now, so Mixin needs @Mutable before it may write to it
    @Shadow @Mutable public double speedModifier;

    @Unique private static final double SPEED_MULTIPLIER_CAP = 1.2d;

    @Inject(
            method = "start",
            at = @At("HEAD")
    )
    public void start(CallbackInfo ci) {
        if (speedModifier > SPEED_MULTIPLIER_CAP)
            speedModifier = SPEED_MULTIPLIER_CAP;
    }
}
