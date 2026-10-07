package com.solegendary.reignofnether.unit.modelling.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.Model;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.AbstractIllager;
import net.minecraft.world.item.*;
import net.neoforged.neoforge.client.ClientHooks;



// based on HumanoidArmorLayer
public class VillagerUnitArmorLayer<T extends LivingEntity, M extends HumanoidModel<T>, A extends HumanoidModel<T>> extends RenderLayer<T, M> {

    private final A innerModel;
    private final A outerModel;

    public VillagerUnitArmorLayer(RenderLayerParent<T, M> pRenderer, A pInnerModel, A pOuterModel) {
        super(pRenderer);
        this.innerModel = pInnerModel;
        this.outerModel = pOuterModel;
    }

    public void render(PoseStack poseStack, MultiBufferSource buffer, int packedLight, T entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        this.renderArmorPiece(poseStack, buffer, entity, EquipmentSlot.CHEST, packedLight, this.getArmorModel(EquipmentSlot.CHEST));
        this.renderArmorPiece(poseStack, buffer, entity, EquipmentSlot.LEGS, packedLight, this.getArmorModel(EquipmentSlot.LEGS));
        this.renderArmorPiece(poseStack, buffer, entity, EquipmentSlot.FEET, packedLight, this.getArmorModel(EquipmentSlot.FEET));
        this.renderArmorPiece(poseStack, buffer, entity, EquipmentSlot.HEAD, packedLight, this.getArmorModel(EquipmentSlot.HEAD));
    }

    private void renderArmorPiece(PoseStack poseStack, MultiBufferSource buffer, T entity, EquipmentSlot slot, int packedLight, A model) {
        ItemStack itemstack = entity.getItemBySlot(slot);
        Item $$9 = itemstack.getItem();
        if ($$9 instanceof ArmorItem armoritem) {
            if (armoritem.getEquipmentSlot() == slot) {
                this.getParentModel().copyPropertiesTo(model);
                this.setPartVisibility(model, slot);
                Model armorModel = this.getArmorModelHook(entity, itemstack, slot, model);
                boolean flag = this.usesInnerModel(slot);

                // 1.21: armor textures come from the ArmorMaterial, not from a name built out of the ITEM id.
                // The old 1.20.1-style path (".../armor/<item>_layer_1.png", e.g. diamond_helmet_layer_1.png)
                // no longer exists in 1.21 - the files are named after the material (diamond_layer_1.png) -
                // so every armour piece resolved to a missing texture and rendered as the magenta/black
                // checkerboard. Walk the material's layers exactly like vanilla's HumanoidArmorLayer does.
                net.minecraft.world.item.ArmorMaterial armormaterial = armoritem.getMaterial().value();
                net.neoforged.neoforge.client.extensions.common.IClientItemExtensions extensions =
                        net.neoforged.neoforge.client.extensions.common.IClientItemExtensions.of(itemstack);
                int fallbackColor = extensions.getDefaultDyeColor(itemstack);
                for (int layerIdx = 0; layerIdx < armormaterial.layers().size(); layerIdx++) {
                    net.minecraft.world.item.ArmorMaterial.Layer armorLayer = armormaterial.layers().get(layerIdx);
                    int tint = extensions.getArmorLayerTintColor(itemstack, entity, armorLayer, layerIdx, fallbackColor);
                    if (tint != 0) {
                        ResourceLocation texture = ClientHooks.getArmorTexture(entity, itemstack, armorLayer, flag, slot);
                        this.renderModel(poseStack, buffer, packedLight, armorModel, tint, texture);
                    }
                }

                if (itemstack.hasFoil()) {
                    this.renderGlint(poseStack, buffer, packedLight, armorModel);
                }
            }
        }
    }

    private void setPartVisibility(HumanoidModel<T> model, EquipmentSlot slot) {
        model.setAllVisible(false);
        switch (slot) {
            case HEAD:
                model.head.visible = true;
                model.hat.visible = true;
                break;
            case CHEST:
                model.body.visible = true;
                model.rightArm.visible = true;
                model.leftArm.visible = true;
                break;
            case LEGS:
                model.body.visible = true;
                model.rightLeg.visible = true;
                model.leftLeg.visible = true;
                break;
            case FEET:
                model.rightLeg.visible = true;
                model.leftLeg.visible = true;
                break;
        }
    }

    private void renderModel(PoseStack poseStack, MultiBufferSource buffer, int packedLight, Model model, int argbColor, ResourceLocation armorResource) {
        VertexConsumer vertexconsumer = buffer.getBuffer(RenderType.armorCutoutNoCull(armorResource));
        model.renderToBuffer(poseStack, vertexconsumer, packedLight, OverlayTexture.NO_OVERLAY, argbColor);
    }

    /*
    private void renderTrim(ArmorMaterial pArmorMaterial, PoseStack pPoseStack, MultiBufferSource pBuffer, int pPackedLight, ArmorTrim pTrim, A pModel, boolean pInnerTexture) {
        this.renderTrim(pArmorMaterial, pPoseStack, pBuffer, pPackedLight, pTrim, pModel, pInnerTexture);
    }

    private void renderTrim(ArmorMaterial p_289690_, PoseStack p_289687_, MultiBufferSource p_289643_, int p_289683_, ArmorTrim p_289692_, Model p_289663_, boolean p_289651_) {
        TextureAtlasSprite textureatlassprite = this.armorTrimAtlas.getSprite(p_289651_ ? p_289692_.innerTexture(p_289690_) : p_289692_.outerTexture(p_289690_));
        VertexConsumer vertexconsumer = textureatlassprite.wrap(p_289643_.getBuffer(Sheets.armorTrimsSheet()));
        p_289663_.renderToBuffer(p_289687_, vertexconsumer, p_289683_, OverlayTexture.NO_OVERLAY, -1);
    }
     */

    private void renderGlint(PoseStack p_289673_, MultiBufferSource p_289654_, int p_289649_, Model p_289659_) {
        p_289659_.renderToBuffer(p_289673_, p_289654_.getBuffer(RenderType.armorEntityGlint()), p_289649_, OverlayTexture.NO_OVERLAY, -1);
    }

    private A getArmorModel(EquipmentSlot pSlot) {
        return this.usesInnerModel(pSlot) ? this.innerModel : this.outerModel;
    }

    private boolean usesInnerModel(EquipmentSlot pSlot) {
        return pSlot == EquipmentSlot.LEGS;
    }

    protected Model getArmorModelHook(T entity, ItemStack itemStack, EquipmentSlot slot, A model) {
        return ClientHooks.getArmorModel(entity, itemStack, slot, model);
    }

}