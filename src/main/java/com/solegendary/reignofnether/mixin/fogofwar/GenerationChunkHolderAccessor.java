package com.solegendary.reignofnether.mixin.fogofwar;

import net.minecraft.server.level.GenerationChunkHolder;
import net.minecraft.world.level.ChunkPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

// 1.21.1: ChunkHolder.pos moved to the parent GenerationChunkHolder;
// expose it so ChunkHolderMixin can still read the holder's chunk position
@Mixin(GenerationChunkHolder.class)
public interface GenerationChunkHolderAccessor {

    @Accessor("pos")
    ChunkPos ron$getPos();
}
