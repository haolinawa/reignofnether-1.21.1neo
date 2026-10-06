package com.solegendary.reignofnether.mixin.fogofwar;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.SheetedDecalTextureGenerator;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.datafixers.util.Pair;
import com.solegendary.reignofnether.orthoview.OrthoviewClientEvents;
import com.solegendary.reignofnether.unit.UnitClientEvents;
import com.solegendary.reignofnether.util.MiscUtil;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.ParticleStatus;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.server.level.BlockDestructionProgress;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.client.model.data.ModelData;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.solegendary.reignofnether.fogofwar.FogOfWarClientEvents.isEnabled;


@Mixin(LevelRenderer.class)
public abstract class LevelRendererMixin {

    @Final @Shadow private Minecraft minecraft;
    @Final @Shadow private RenderBuffers renderBuffers;
    @Final @Shadow private Long2ObjectMap<SortedSet<BlockDestructionProgress>> destructionProgress;

    @Shadow private ClientLevel level;

    @Unique private final AtomicBoolean needsFrustumUpdate = new AtomicBoolean(false);

    // always recheck chunks being in frustum - without this normally only checks when the camera moves
    @Inject(
            method = "setupRender(Lnet/minecraft/client/Camera;Lnet/minecraft/client/renderer/culling/Frustum;ZZ)V",
            at = @At("HEAD")
    )
    private void setupRender(Camera pCamera, Frustum pFrustum, boolean pHasCapturedFrustum, boolean pIsSpectator, CallbackInfo ci) {
        if (!isEnabled())
            return;

        if (!OrthoviewClientEvents.isEnabled())
            return;

        needsFrustumUpdate.set(true);
    }

    // rerun blockDestroyProgress overlays but with range extended to between 32-256 blocks
    @Inject(
            method = "renderLevel",
            at = @At("TAIL")
    )
    private void renderLevel(net.minecraft.client.DeltaTracker pDeltaTracker,
                             boolean pRenderBlockOutline, Camera pCamera, GameRenderer pGameRenderer,
                             LightTexture pLightTexture, Matrix4f pFrustumMatrix, Matrix4f pProjectionMatrix,
                             CallbackInfo ci) {
        // 1.21.1: no PoseStack param; vanilla renders decals with an identity PoseStack
        // (view rotation lives in RenderSystem.getModelViewStack), so we do the same
        PoseStack pPoseStack = new PoseStack();

        Vec3 vec3 = pCamera.getPosition();
        double d0 = vec3.x();
        double d1 = vec3.y();
        double d2 = vec3.z();

        ObjectIterator var42 = this.destructionProgress.long2ObjectEntrySet().iterator();

        while (var42.hasNext()) {
            Long2ObjectMap.Entry<SortedSet<BlockDestructionProgress>> entry = (Long2ObjectMap.Entry) var42.next();
            BlockPos blockpos2 = BlockPos.of(entry.getLongKey());
            double d3 = (double) blockpos2.getX() - d0;
            double d4 = (double) blockpos2.getY() - d1;
            double d5 = (double) blockpos2.getZ() - d2;
            double distSqr = d3 * d3 + d4 * d4 + d5 * d5;
            if ((distSqr > 1024.0 && distSqr < 65536)) {
                SortedSet<BlockDestructionProgress> sortedset1 = (SortedSet) entry.getValue();
                if (sortedset1 != null && !sortedset1.isEmpty()) {
                    int k1 = (sortedset1.last()).getProgress();
                    pPoseStack.pushPose();
                    pPoseStack.translate((double) blockpos2.getX() - d0, (double) blockpos2.getY() - d1, (double) blockpos2.getZ() - d2);
                    PoseStack.Pose posestack$pose = pPoseStack.last();
                    VertexConsumer vertexconsumer1 = new com.mojang.blaze3d.vertex.SheetedDecalTextureGenerator(this.renderBuffers.crumblingBufferSource().getBuffer((RenderType) ModelBakery.DESTROY_TYPES.get(k1)), posestack$pose, 1);
                    ModelData modelData = this.level.getModelDataManager().getAt(blockpos2);
                    this.minecraft.getBlockRenderer().renderBreakingTexture(this.level.getBlockState(blockpos2), blockpos2, this.level, pPoseStack, vertexconsumer1, modelData == null ? ModelData.EMPTY : modelData);
                    pPoseStack.popPose();
                }
            }
        }
    }

    // increase render distance for particles
    @Shadow private ParticleStatus calculateParticleLevel(boolean pDecreased) { return null; }

    @Shadow @Nullable private PostChain entityEffect;

    @Inject(
            method = "addParticleInternal(Lnet/minecraft/core/particles/ParticleOptions;ZZDDDDDD)Lnet/minecraft/client/particle/Particle;",
            at = @At("HEAD"),
            cancellable = true
    )
    public void addParticleInternal(ParticleOptions pOptions, boolean pForce, boolean pDecreased, double pX, double pY, double pZ,
                                    double pXSpeed, double pYSpeed, double pZSpeed, CallbackInfoReturnable<Particle> cir) {
        if (!OrthoviewClientEvents.isEnabled())
            return;

        Camera camera = this.minecraft.gameRenderer.getMainCamera();
        if (this.minecraft != null && camera.isInitialized() && this.minecraft.particleEngine != null) {
            ParticleStatus particlestatus = this.calculateParticleLevel(pDecreased);
            if (pForce) {
                cir.setReturnValue(this.minecraft.particleEngine.createParticle(pOptions, pX, pY, pZ, pXSpeed, pYSpeed, pZSpeed));
            } else if (camera.getPosition().distanceToSqr(pX, pY, pZ) > 4096) {
                cir.setReturnValue(null);
            } else {
                cir.setReturnValue(particlestatus == ParticleStatus.MINIMAL ? null : this.minecraft.particleEngine.createParticle(pOptions, pX, pY, pZ, pXSpeed, pYSpeed, pZSpeed));
            }
        } else {
            cir.setReturnValue(null);
        }
    }

    @Final @Shadow private ObjectArrayList<net.minecraft.client.renderer.chunk.SectionRenderDispatcher.RenderSection> visibleSections;
    private List<Pair<BlockPos, Integer>> chunksToReDirty = new ArrayList<>();

    @Inject(
            method = "setupRender",
            at = @At("TAIL")
    )
    private void ron$setupRenderTail(Camera pCamera, net.minecraft.client.renderer.culling.Frustum pFrustum,
                             boolean pHasCapturedFrustum, boolean pIsSpectator, CallbackInfo ci) {

        // hiding leaves around cursor
        if (OrthoviewClientEvents.hideLeavesMethod == OrthoviewClientEvents.LeafHideMethod.AROUND_UNITS_AND_CURSOR &&
                OrthoviewClientEvents.isEnabled()) {
            UnitClientEvents.windowUpdateTicks -= 1;
            if (UnitClientEvents.windowUpdateTicks <= 0) {
                UnitClientEvents.windowUpdateTicks = UnitClientEvents.WINDOW_UPDATE_TICKS_MAX;
                for (net.minecraft.client.renderer.chunk.SectionRenderDispatcher.RenderSection chunkInfo : this.visibleSections) {
                    BlockPos chunkCentreBp = chunkInfo.getOrigin().offset((int) 8.5d, (int) 8.5d, (int) 8.5d);

                    List<Pair<BlockPos, Integer>> newChunksToReDirty = new ArrayList<>();

                    // rerender each chunk a second time so we can unhide leaves as they go out of range
                    synchronized (UnitClientEvents.windowPositions) {
                        for (Pair<BlockPos, Integer> pair : chunksToReDirty) {
                            int times = pair.getSecond();
                            if (pair.getFirst().equals(chunkInfo.getOrigin())) {
                                chunkInfo.setDirty(true);
                                times -= 1;
                            }
                            if (times > 0)
                                newChunksToReDirty.add(new Pair<>(pair.getFirst(), times));
                        }
                        chunksToReDirty.clear();
                        chunksToReDirty.addAll(newChunksToReDirty);

                        UnitClientEvents.windowPositions.forEach(bp -> {
                            if (chunkCentreBp.distSqr(bp) < 625) {
                                chunkInfo.setDirty(true);
                                chunksToReDirty.add(new Pair<>(chunkInfo.getOrigin(), 10));
                            }
                        });
                    }
                }
            }
        }
    }
}
