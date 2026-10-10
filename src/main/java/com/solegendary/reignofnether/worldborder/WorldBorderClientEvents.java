package com.solegendary.reignofnether.worldborder;


import net.neoforged.neoforge.client.event.ClientTickEvent;
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

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post evt) {
        if (false)
            return;

        if (MC.level == null || MC.player == null)
            return;

        WorldBorder border = MC.level.getWorldBorder();

        // Compare the rounded BLOCK bounds rather than the raw doubles. A lerping border changes
        // getSize() every single millisecond, so comparing (centerX, centerZ, size) fired this branch on
        // EVERY tick for the whole duration of the lerp. Each hit called FogOfWarClientEvents.resetFogChunks
        // -> LevelRenderer.allChanged(), which calls releaseAllBuffers() on the ViewArea and sets every
        // section's compiled state to UNCOMPILED. Entities are only drawn when their section is compiled
        // (LevelRenderer.renderLevel: "level.isOutsideBuildHeight(y) || this.isSectionCompiled(blockpos)"),
        // so they all vanished while this mod's own overlays - which do not depend on section meshes - kept
        // drawing: the ground boxes stayed, the health bars jittered as sections were rebuilt, and the game
        // stuttered until the lerp finished.
        //
        // The border only affects the client mesher through the outside-border tint (BiomeColorsMixin /
        // FogTintingBlockColor both read it live), and only chunks that STRADDLE the border line mix tinted
        // and untinted columns. So instead of a destructive full reload we mark just that one-column ring
        // dirty. setSectionDirty() only sets the section's dirty flag - it never clears its compiled state
        // and never blocks the render thread - so this cannot make entity models disappear at all.
        int minX = (int) Math.floor(border.getMinX());
        int maxX = (int) Math.ceil(border.getMaxX());
        int minZ = (int) Math.floor(border.getMinZ());
        int maxZ = (int) Math.ceil(border.getMaxZ());

        if (minX != lastMinX || maxX != lastMaxX || minZ != lastMinZ || maxZ != lastMaxZ) {
            // skip the very first sample: it is just this world's border arriving at level load
            if (lastMinX != Integer.MIN_VALUE)
                dirtyBorderRing(lastMinX, lastMinZ, lastMaxX, lastMaxZ);
            dirtyBorderRing(minX, minZ, maxX, maxZ);

            lastMinX = minX;
            lastMaxX = maxX;
            lastMinZ = minZ;
            lastMaxZ = maxZ;
        }
    }

    // Mark the columns along the border rectangle's edges for re-mesh. Only already-meshed sections need
    // this: the tint is sampled live from the border, so any chunk meshed later comes out correct on its
    // own. The ring is clamped to the player's loaded region - that also keeps a vanilla-sized (~60M block)
    // border from making this loop iterate millions of times, and sections outside the view area are not
    // compiled anyway.
    private static void dirtyBorderRing(int minX, int minZ, int maxX, int maxZ) {
        if (MC.level == null || MC.levelRenderer == null)
            return;

        // one chunk of slack per side: a chunk straddles the line when its 16-block span contains it
        int x0 = (minX >> 4) - 1;
        int x1 = (maxX >> 4) + 1;
        int z0 = (minZ >> 4) - 1;
        int z1 = (maxZ >> 4) + 1;

        // Clamp to the loaded view area. ViewArea.setDirty() does NOT bounds-check: it looks the section up
        // with floorMod, so dirtying a section outside the grid would silently mark some unrelated chunk's
        // section dirty. ViewArea is centred on the camera entity (ViewArea.repositionCamera), so clamp to
        // that, not to MC.player.
        int reach = MC.options.getEffectiveRenderDistance() + 1;
        net.minecraft.world.entity.Entity view = MC.getCameraEntity() != null ? MC.getCameraEntity() : MC.player;
        int cx = view.chunkPosition().x;
        int cz = view.chunkPosition().z;
        x0 = Math.max(x0, cx - reach);
        x1 = Math.min(x1, cx + reach);
        z0 = Math.max(z0, cz - reach);
        z1 = Math.min(z1, cz + reach);
        if (x0 > x1 || z0 > z1)
            return;

        int minSection = MC.level.getMinSection();
        int maxSection = MC.level.getMaxSection();

        for (int x = x0; x <= x1; x++) {
            dirtyColumn(x, z0, minSection, maxSection);
            if (z1 != z0)
                dirtyColumn(x, z1, minSection, maxSection);
        }
        for (int z = z0 + 1; z < z1; z++) {
            dirtyColumn(x0, z, minSection, maxSection);
            if (x1 != x0)
                dirtyColumn(x1, z, minSection, maxSection);
        }
    }

    private static void dirtyColumn(int cx, int cz, int minSection, int maxSection) {
        for (int y = minSection; y < maxSection; y++)
            MC.levelRenderer.setSectionDirty(cx, y, cz);
    }

    public static boolean isOutsideWorldBorder(BlockPos bp) {
        return MC.level != null && !MC.level.getWorldBorder().isWithinBounds(bp);
    }
}
