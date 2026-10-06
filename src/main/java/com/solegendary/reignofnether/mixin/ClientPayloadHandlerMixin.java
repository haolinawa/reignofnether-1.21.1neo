package com.solegendary.reignofnether.mixin;

import com.solegendary.reignofnether.orthoview.OrthoviewClientEvents;
import com.solegendary.reignofnether.time.NightUtils;
import com.solegendary.reignofnether.time.TimeClientEvents;
import com.solegendary.reignofnether.time.TimeUtils;
import com.solegendary.reignofnether.util.MiscUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.network.payload.ClientboundCustomSetTimePayload;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * 1.21.1 NeoForge replaces ClientboundSetTimePacket with its own ClientboundCustomSetTimePayload,
 * so ClientPacketMixin#handleSetTime is never called on a NeoForge server/client and the mod never
 * learned the server time (targetClientTime stayed 0 while vanilla kept resetting the level time,
 * which made the sun jump around constantly).
 *
 * Intercept NeoForge's handler instead: record the server time for the mod's own time distortion
 * logic and cancel, so the mod keeps full control over the client's day time.
 */
@Mixin(targets = "net.neoforged.neoforge.network.handlers.ClientPayloadHandler")
public class ClientPayloadHandlerMixin {

    @Inject(
            method = "handle(Lnet/neoforged/neoforge/network/payload/ClientboundCustomSetTimePayload;Lnet/neoforged/neoforge/network/handling/IPayloadContext;)V",
            at = @At("HEAD"),
            cancellable = true
    )
    private static void ron$handleSetTime(ClientboundCustomSetTimePayload payload, IPayloadContext context, CallbackInfo ci) {
        Minecraft minecraft = Minecraft.getInstance();

        Vec3 pos;
        if (OrthoviewClientEvents.isEnabled())
            pos = MiscUtil.getOrthoviewCentreWorldPos(minecraft);
        else if (minecraft.player != null && minecraft.level != null)
            pos = minecraft.player.position();
        else
            return;

        ci.cancel();

        TimeClientEvents.serverNormDayTime = TimeUtils.normaliseTime(payload.dayTime());
        TimeClientEvents.serverGameTime = payload.gameTime();
        TimeClientEvents.ticksSinceLastUpdate = 0;

        if (NightUtils.isInRangeOfNightSource(pos, true))
            TimeClientEvents.targetClientTime = 18000; // midnight
        else
            TimeClientEvents.targetClientTime = TimeClientEvents.serverNormDayTime;
    }
}
