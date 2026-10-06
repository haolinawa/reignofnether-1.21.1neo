package com.solegendary.reignofnether.mixin;

import com.solegendary.reignofnether.minimap.MinimapClientEvents;
import com.solegendary.reignofnether.orthoview.OrthoviewClientEvents;
import com.solegendary.reignofnether.registrars.MobEffectRegistrar;
import com.solegendary.reignofnether.resources.ResourceSources;
import com.solegendary.reignofnether.resources.ResourcesClientEvents;
import com.solegendary.reignofnether.unit.UnitServerEvents;
import com.solegendary.reignofnether.util.MiscUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageSources;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public abstract class EntityMixin {

    @Shadow private static double viewScale;
    @Shadow private AABB bb;
    @Shadow public abstract EntityType<?> getType();

    @Inject(
        method = "shouldRenderAtSqrDistance(D)Z",
        at = @At("HEAD"),
        cancellable=true
    )
    private void shouldRenderAtSqrDistance(
            double pDistance, CallbackInfoReturnable<Boolean> cir
    ) {
        if (!OrthoviewClientEvents.isEnabled())
            return;

        double d0 = this.bb.getSize();
        if (Double.isNaN(d0)) {
            d0 = 1.0D;
        }
        d0 *= 64.0D * viewScale;

        // make item entities render at 4x normal distance
        if (this.getType() == EntityType.ITEM)
            d0 *= 4.0D;

        // The orthographic camera sits up to ~100 blocks above the terrain, so vanilla's perspective-style
        // distance cut (radius = bounding box size * 64 * viewScale, i.e. only ~67-168 blocks) removed the
        // models of units on the far side of an RTS view, while their selection boxes kept being drawn -
        // that is the "blank entity" look (an outlined box with nothing inside it). An orthographic view
        // has no distance falloff, so scale the cut to the view extent instead; the frustum and the fog
        // checks still do the real culling.
        d0 = Math.max(d0, OrthoviewClientEvents.getZoom() * 2.0D + 160.0D);

        cir.setReturnValue(pDistance < d0 * d0);
    }

    // use this mixin if you want a mob to avoid damage and not even register a damage animation
    @Inject(
            method = "isInvulnerableTo",
            at = @At("HEAD"),
            cancellable=true
    )
    private void isInvulnerableTo(DamageSource pSource, CallbackInfoReturnable<Boolean> cir) {
        if (pSource == damageSources().inWall())
            cir.setReturnValue(true);
    }

    @Shadow public int getTicksRequiredToFreeze() { return 140; }
    @Shadow public int getTicksFrozen() { return 0; }

    @Shadow public abstract DamageSources damageSources();
    @Shadow public abstract Component getName();
    @Shadow public abstract void remove(Entity.RemovalReason pReason);
    @Shadow public abstract AABB getBoundingBox();
    @Shadow public abstract BlockPos getOnPos();
    @Shadow public abstract Level level();


    @Shadow private Level level;

    @Inject(
            method = "getPercentFrozen",
            at = @At("HEAD"),
            cancellable = true
    )
    protected void getPercentFrozen(CallbackInfoReturnable<Float> cir) {
        int i = this.getTicksRequiredToFreeze();
        float percent = (float)Math.min(this.getTicksFrozen(), 140) / (float)i;
        cir.setReturnValue(Math.min(percent, 0.5f));
    }

    @Inject(
            method = "extinguishFire()V",
            at = @At("HEAD"),
            cancellable = true
    )
    public void extinguishFire(CallbackInfo ci) {
        if ((Object)this instanceof LivingEntity le && le.hasEffect(MobEffectRegistrar.SOULS_AFLAME)) {
            ci.cancel();
        }
    }

    @Inject(
            method = "playEntityOnFireExtinguishedSound",
            at = @At("HEAD"),
            cancellable = true
    )
    public void playEntityOnFireExtinguishedSound(CallbackInfo ci) {
        if ((Object)this instanceof LivingEntity le && le.hasEffect(MobEffectRegistrar.SOULS_AFLAME)) {
            ci.cancel();
        }
    }

    @Inject(
            method = "setRemainingFireTicks",
            at = @At("HEAD"),
            cancellable = true
    )
    public void setRemainingFireTicks(int pRemainingFireTicks, CallbackInfo ci) {
        if (pRemainingFireTicks <= 0 && (Object)this instanceof LivingEntity le &&
            le.hasEffect(MobEffectRegistrar.SOULS_AFLAME)) {
            ci.cancel();
        }
    }

    // use this mixin if you want a mob to avoid damage and not even register a damage animation
    @Inject(
            method = "isCurrentlyGlowing",
            at = @At("HEAD"),
            cancellable=true
    )
    private void isCurrentlyGlowing(CallbackInfoReturnable<Boolean> cir) {
        if (level.isClientSide() && MinimapClientEvents.shouldHighlightAnimals()) {
            if ((Object) this instanceof LivingEntity le && ResourceSources.isHuntableAnimal(le)) {
                cir.setReturnValue(true);
            }
        }
    }

}
