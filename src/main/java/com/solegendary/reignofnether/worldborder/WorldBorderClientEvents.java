package com.solegendary.reignofnether.worldborder;


import net.neoforged.neoforge.client.event.ClientTickEvent;
import com.solegendary.reignofnether.fogofwar.FogOfWarClientEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.border.WorldBorder;
import net.neoforged.bus.api.SubscribeEvent;

public class WorldBorderClientEvents {

    private static final Minecraft MC = Minecraft.getInstance();

    public static final int OUTSIDE_WORLD_BORDER_TINT = 0x252933;

    // Last *quantised* border, i.e. the inclusive block bounds, not the raw double size.
    private static int lastMinX = Integer.MIN_VALUE;
    private static int lastMaxX = Integer.MIN_VALUE;
    private static int lastMinZ = Integer.MIN_VALUE;
    private static int lastMaxZ = Integer.MIN_VALUE;

    // border tint is baked into the chunk meshes, so a move needs a remesh - but throttled, see onClientTick
    private static final int BORDER_REMESH_COOLDOWN_TICKS = 20;
    private static boolean borderRemeshPending = false;
    private static int borderRemeshCooldown = 0;

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post evt) {
        if (false)
            return;

        if (MC.level == null)
            return;

        WorldBorder border = MC.level.getWorldBorder();

        // Compare the rounded BLOCK bounds rather than the raw doubles. A lerping border changes
        // getSize() every single millisecond, so comparing (centerX, centerZ, size) fired this branch on
        // EVERY tick for the whole duration of the lerp. Each hit called FogOfWarClientEvents.resetFogChunks
        // -> LevelRenderer.allChanged(), which discards every compiled chunk section and blocks the render
        // thread in blockUntilClear(). Entities are only drawn when their section is compiled
        // (LevelRenderer: "level.isOutsideBuildHeight(y) || this.isSectionCompiled(blockpos)"), so they all
        // vanished while the mod's own overlays - which do not depend on section meshes - kept drawing: the
        // ground boxes stayed, health bars jittered as sections were rebuilt, and the game stuttered until
        // the lerp finished. The only thing the border actually affects in the client mesher is which blocks
        // are tinted (see the isOutsideWorldBorder mixins), so it is enough to remesh when the block bounds
        // really move, which for a smooth lerp happens at most once per block width.
        int minX = (int) Math.floor(border.getMinX());
        int maxX = (int) Math.ceil(border.getMaxX());
        int minZ = (int) Math.floor(border.getMinZ());
        int maxZ = (int) Math.ceil(border.getMaxZ());

        boolean changed = minX != lastMinX || maxX != lastMaxX || minZ != lastMinZ || maxZ != lastMaxZ;
        if (changed) {
            // skip the very first sample: it is just this world's border arriving, and allChanged() during
            // level load is pure wasted work
            if (lastMinX != Integer.MIN_VALUE)
                borderRemeshPending = true;

            lastMinX = minX;
            lastMaxX = maxX;
            lastMinZ = minZ;
            lastMaxZ = maxZ;
        }

        // Debounce the remesh. Even quantised to whole blocks a fast lerp can cross a block every few
        // ticks, and each remesh is a full allChanged() (blockUntilClear). Remesh at most once per second
        // while the border is still moving, then once more as soon as it settles so the final tint is exact.
        if (borderRemeshPending) {
            boolean settled = !changed && borderRemeshCooldown == 0;
            if (settled || borderRemeshCooldown == 0) {
                borderRemeshPending = false;
                borderRemeshCooldown = BORDER_REMESH_COOLDOWN_TICKS;
                FogOfWarClientEvents.resetFogChunks();
            }
        }
        if (borderRemeshCooldown > 0)
            borderRemeshCooldown--;
    }

    public static boolean isOutsideWorldBorder(BlockPos bp) {
        return MC.level != null && !MC.level.getWorldBorder().isWithinBounds(bp);
    }
}
