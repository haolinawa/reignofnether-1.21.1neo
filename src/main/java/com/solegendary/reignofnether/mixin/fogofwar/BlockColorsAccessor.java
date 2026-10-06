package com.solegendary.reignofnether.mixin.fogofwar;

import net.minecraft.client.color.block.BlockColor;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.world.level.block.Block;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

// Expose BlockColors' map so we can wrap existing providers with FogTintingBlockColor.
// NeoForge 1.21.1 keys this map by Block itself (IdentityHashMap<Block, BlockColor>), not by Holder.Reference
// like 1.20.1 Forge did - looking up with a Holder made every delegate null, which turned every biome-tinted
// block (grass, leaves, water, ...) white.
@Mixin(BlockColors.class)
public interface BlockColorsAccessor {
    @Accessor
    Map<Block, BlockColor> getBlockColors();
}
