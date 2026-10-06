package com.solegendary.reignofnether.mixin;

import io.netty.channel.Channel;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.DisconnectedScreen;
import net.minecraft.client.gui.screens.GenericMessageScreen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.client.gui.screens.multiplayer.JoinMultiplayerScreen;
import net.minecraft.network.Connection;
import net.minecraft.network.DisconnectionDetails;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(Connection.class)
public class MixinNetworkManager {

    @Shadow private Channel channel;
    @Shadow private DisconnectionDetails disconnectionDetails;

    /**
     * @author
     * @reason when kicked while still on the "negotiating" screen, return to the
     *         multiplayer server list instead of an empty disconnect screen.
     *         1.21.1: disconnectedReason (Component) was replaced by disconnectionDetails
     */

    @Overwrite
    @OnlyIn(Dist.CLIENT)
    public void disconnect(Component p_150718_1_) {
        if (this.channel.isOpen()) {
            this.channel.close().awaitUninterruptibly();
            this.disconnectionDetails = new DisconnectionDetails(p_150718_1_);

            if (Minecraft.getInstance().screen instanceof GenericMessageScreen dirtMessageScreen) {
                Component title = dirtMessageScreen.getTitle();

                if (title.getContents() instanceof TranslatableContents translatable) {
                    if (translatable.getKey().equals("connect.negotiating"))
                        Minecraft.getInstance().setScreen(
                                new DisconnectedScreen(
                                        new JoinMultiplayerScreen(new TitleScreen()), Component.literal(""), disconnectionDetails));
                }
            }
        }
    }
}
