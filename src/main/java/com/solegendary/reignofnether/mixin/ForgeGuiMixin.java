package com.solegendary.reignofnether.mixin;

import com.mojang.blaze3d.platform.Window;
import com.solegendary.reignofnether.orthoview.OrthoviewClientEvents;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ChatComponent;
import net.minecraft.util.Mth;
import net.neoforged.neoforge.client.event.CustomizeGuiOverlayEvent;
import net.neoforged.neoforge.common.NeoForge;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * 1.21 port: NeoForge removed ForgeGui (the GUI is now assembled from layers via
 * GuiLayerManager / VanillaGuiLayers). The chat layer lives on vanilla {@link Gui#renderChat}.
 *
 * Purpose (unchanged): when the RTS orthoview is active, shift the chat history window up
 * so it does not overlap the bottom-left hotkeys.
 *
 * NOTE: a mixin class must NOT extend its own target class - use @Shadow instead.
 */
@Mixin(Gui.class)
public class ForgeGuiMixin {

    @Shadow @Final private Minecraft minecraft;
    @Shadow @Final private ChatComponent chat;
    @Shadow private int tickCount;

    @Inject(
            method = "renderChat",
            at = @At("HEAD"),
            cancellable = true
    )
    protected void renderChat(GuiGraphics guiGraphics, DeltaTracker deltaTracker, CallbackInfo ci) {
        if (!OrthoviewClientEvents.isEnabled())
            return;

        ci.cancel();

        this.minecraft.getProfiler().push("chat");
        Window window = this.minecraft.getWindow();
        int height = window.getGuiScaledHeight();
        CustomizeGuiOverlayEvent.Chat event = new CustomizeGuiOverlayEvent.Chat(
                window, guiGraphics, deltaTracker, 0, height - 40);
        NeoForge.EVENT_BUS.post(event);
        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(event.getPosX(),
                (double) (event.getPosY() - height + 40 + OrthoviewClientEvents.CHAT_Y_OFFSET) / this.chat.getScale(), 0.0);
        int mouseX = Mth.floor(this.minecraft.mouseHandler.xpos() * (double) window.getGuiScaledWidth()
                / (double) window.getScreenWidth());
        int mouseY = Mth.floor(this.minecraft.mouseHandler.ypos() * (double) window.getGuiScaledHeight()
                / (double) window.getScreenHeight());
        this.chat.render(guiGraphics, this.tickCount, mouseX, mouseY, false);
        guiGraphics.pose().popPose();
        this.minecraft.getProfiler().pop();
    }
}
