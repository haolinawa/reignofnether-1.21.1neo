package com.solegendary.reignofnether.mixin;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Set;

/** 1.21: BlockEntityType#validBlocks is final - @Mutable accessor lets mods add custom skull blocks. */
@Mixin(BlockEntityType.class)
public interface BlockEntityTypeAccessor {
    @Mutable
    @Accessor("validBlocks")
    void ron$setValidBlocks(Set<Block> validBlocks);

    @Accessor("validBlocks")
    Set<Block> ron$getValidBlocks();
}
