package com.solegendary.reignofnether.util;

import com.solegendary.reignofnether.util.MobType;

/**
 * 1.21 shim: net.minecraft.world.entity.MobType was removed in 1.21.
 * Kept as a mod-local enum so existing unit logic keeps compiling.
 */
public enum MobType {
    UNDEFINED, UNDEAD, ARTHROPOD, ILLAGER, WATER
}
