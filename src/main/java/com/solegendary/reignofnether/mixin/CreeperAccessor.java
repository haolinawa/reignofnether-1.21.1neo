package com.solegendary.reignofnether.mixin;

import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.world.entity.monster.Creeper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/** 1.21: Creeper#DATA_IS_POWERED is private static. */
@Mixin(Creeper.class)
public interface CreeperAccessor {
    @Accessor("DATA_IS_POWERED")
    static EntityDataAccessor<Boolean> ron$getDataIsPowered() {
        throw new AssertionError();
    }
}
