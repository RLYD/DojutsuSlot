package com.fuskirby.dojutsu_slot.client.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.client.renderer.GlStateManager;

import javax.annotation.ParametersAreNonnullByDefault;

public class ModelCompositeHelmet extends ModelBiped {
    private final ModelBiped helmetModel;
    private final ModelBiped leftModel;
    private final ModelBiped rightModel;
    private final ResourceLocation helmetTexture;
    private final ResourceLocation leftTexture;
    private final ResourceLocation rightTexture;
    private final int helmetColor;
    private final int leftColor;
    private final int rightColor;

    public ModelCompositeHelmet(ModelBiped helmetModel, ModelBiped leftModel, ModelBiped rightModel,
                                ResourceLocation helmetTex, ResourceLocation leftTex, ResourceLocation rightTex,
                                int helmetColor, int leftColor, int rightColor) {
        super(0.0F);
        this.helmetModel = helmetModel;
        this.leftModel = leftModel;
        this.rightModel = rightModel;
        this.helmetTexture = helmetTex;
        this.leftTexture = leftTex;
        this.rightTexture = rightTex;
        this.helmetColor = helmetColor;
        this.leftColor = leftColor;
        this.rightColor = rightColor;
    }

    @Override
    @ParametersAreNonnullByDefault
    public void render(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        syncModel(helmetModel);
        syncModel(leftModel);
        syncModel(rightModel);

        renderPart(helmetModel, helmetTexture, helmetColor, entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
        renderPart(leftModel, leftTexture, leftColor, entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
        renderPart(rightModel, rightTexture, rightColor, entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
    }

    private void renderPart(ModelBiped model, ResourceLocation texture, int color,
                            Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks,
                            float netHeadYaw, float headPitch, float scale) {
        if (model == null || texture == null) return;
        Minecraft.getMinecraft().getTextureManager().bindTexture(texture);
        if (color != -1) {
            float r = (color >> 16 & 0xFF) / 255.0F;
            float g = (color >> 8 & 0xFF) / 255.0F;
            float b = (color & 0xFF) / 255.0F;
            GlStateManager.color(r, g, b, 1.0F);
        }
        model.render(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
        if (color != -1) {
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        }
    }

    private void syncModel(ModelBiped model) {
        if (model != null) {
            model.isChild = this.isChild;
            model.isSneak = this.isSneak;
            model.isRiding = this.isRiding;
            model.swingProgress = this.swingProgress;
        }
    }
}