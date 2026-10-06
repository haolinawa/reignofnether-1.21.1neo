package com.solegendary.reignofnether.config.elements;


import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.common.ModConfigSpec;

/**
 * 1.21: vanilla {@code Checkbox}'s constructors are package-private and its Builder
 * can't be used from {@code super(...)}, so this is a lightweight checkbox widget
 * extending {@link AbstractButton} instead.
 */
public class ConfigCheckbox extends AbstractButton {

    private static final WidgetSprites SPRITES = new WidgetSprites(
            ResourceLocation.withDefaultNamespace("widget/checkbox"),
            ResourceLocation.withDefaultNamespace("widget/checkbox_selected"),
            ResourceLocation.withDefaultNamespace("widget/checkbox_disabled"),
            ResourceLocation.withDefaultNamespace("widget/checkbox_selected_disabled"));

    private static final int BOX_SIZE = 16;

    private final ModConfigSpec.ConfigValue<Boolean> configValue;
    private final String labelOn;
    private final String labelOff;
    private boolean checked;

    public ConfigCheckbox(ModConfigSpec.ConfigValue<Boolean> configValue, String label) {
        super(0, 0, 20, 20, Component.literal(label));
        this.configValue = configValue;
        this.labelOn = label;
        this.labelOff = label;
        this.checked = configValue.get();
        setMessage(Component.literal(this.checked ? labelOn : labelOff));
    }

    public ConfigCheckbox(ModConfigSpec.ConfigValue<Boolean> configValue, String labelOn, String labelOff) {
        super(0, 0, 20, 20, Component.literal(configValue.get() ? labelOn : labelOff));
        this.configValue = configValue;
        this.labelOn = labelOn;
        this.labelOff = labelOff;
        this.checked = configValue.get();
        setMessage(Component.literal(this.checked ? labelOn : labelOff));
    }

    @Override
    public void onPress() {
        this.checked = !this.checked;
        configValue.set(this.checked);
        setMessage(Component.literal(this.checked ? labelOn : labelOff));
    }

    @Override
    public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        ResourceLocation sprite = SPRITES.get(this.checked, this.isHoveredOrFocused());
        guiGraphics.blitSprite(sprite, this.getX(), this.getY(), BOX_SIZE, BOX_SIZE);
        guiGraphics.drawString(Minecraft.getInstance().font, this.getMessage(),
                this.getX() + BOX_SIZE + 4, this.getY() + (this.height - 8) / 2, 0xFFFFFF, false);
    }

    @Override
    protected void updateWidgetNarration(net.minecraft.client.gui.narration.NarrationElementOutput output) {
        this.defaultButtonNarrationText(output);
    }

    public ConfigCheckbox pos(int x, int y) {
        super.setPosition(x, y);
        return this;
    }

    public ConfigCheckbox size(int w, int h) {
        super.setWidth(w);
        super.setHeight(Math.max(h, BOX_SIZE));
        return this;
    }
}
