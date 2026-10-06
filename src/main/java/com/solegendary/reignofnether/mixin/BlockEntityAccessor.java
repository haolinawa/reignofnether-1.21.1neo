package com.solegendary.reignofnether.mixin;

import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

/** 1.21: BlockEntity#type is final; custom block entities need to replace it. */
@Mixin(BlockEntity.class)
public interface BlockEntityAccessor {
    @Mutable
    @Accessor("type")
    void ron$setType(BlockEntityType<?> type);
}
