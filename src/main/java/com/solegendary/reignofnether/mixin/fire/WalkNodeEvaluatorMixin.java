package com.solegendary.reignofnether.mixin.fire;

import com.solegendary.reignofnether.blocks.BlockUtils;
import com.solegendary.reignofnether.unit.interfaces.Unit;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.NodeEvaluator;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.level.pathfinder.PathfindingContext;
import net.minecraft.world.level.pathfinder.WalkNodeEvaluator;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * 1.21 port: the Forge-era target
 * getBlockPathType(BlockGetter,III) and helper getBlockPathTypeStatic no longer exist.
 * Equivalent hook is NodeEvaluator#getPathTypeOfMob(PathfindingContext,III,Mob).
 */
@Mixin(WalkNodeEvaluator.class)
public abstract class WalkNodeEvaluatorMixin extends NodeEvaluator {

    public WalkNodeEvaluatorMixin() {
    }

    @Inject(
            method = "getPathTypeOfMob",
            at = @At("HEAD"),
            cancellable = true
    )
    public void getPathTypeOfMob(PathfindingContext context, int pX, int pY, int pZ, Mob pMob,
                                 CallbackInfoReturnable<PathType> cir) {
        if (!(pMob instanceof Unit))
            return;

        BlockGetter level = context.level();
        BlockState blockStateBelow = level.getBlockState(new BlockPos(pX, pY, pZ).below());
        Block blockBelow = blockStateBelow.getBlock();
        Block block = level.getBlockState(new BlockPos(pX, pY, pZ)).getBlock();

        // allow units to walk on fire and magma but not leaves (to prevent workers getting stuck in trees)
        if (block == Blocks.FIRE || blockBelow == Blocks.FIRE ||
            block == Blocks.MAGMA_BLOCK || blockBelow == Blocks.MAGMA_BLOCK)
            cir.setReturnValue(PathType.WALKABLE);
        else if (block == Blocks.POINTED_DRIPSTONE || blockBelow == Blocks.POINTED_DRIPSTONE)
            cir.setReturnValue(PathType.UNPASSABLE_RAIL);
        else if (BlockUtils.isLeafBlock(blockStateBelow))
            cir.setReturnValue(PathType.DAMAGE_FIRE);
        else
            cir.setReturnValue(WalkNodeEvaluator.getPathTypeStatic(pMob, new BlockPos.MutableBlockPos(pX, pY, pZ)));
    }
}
