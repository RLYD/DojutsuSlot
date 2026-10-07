package com.fuskirby.dojutsu_slot.api.texture;

import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Objects;
import java.util.function.Predicate;

public class DojutsuTextureAPI {
    public static void preInit(FMLPreInitializationEvent event) {
        DojutsuTextureConfig.loadConfigFile();
        DojutsuTextureConfig.scanAndRegisterTextures();
    }

    public static void registerDojutsuTexture(String modId, String itemId,
                                              String defaultTexture,
                                              String leftEyeTexture,
                                              String rightEyeTexture) {
        DojutsuTextureConfig.registerTexture(modId, itemId,
                defaultTexture, leftEyeTexture, rightEyeTexture);
    }

    public static void registerDojutsuTexture(String modId, String itemId,
                                              String defaultTexture,
                                              String leftEyeTexture,
                                              String rightEyeTexture,
                                              Predicate<ItemStack> predicate) {
        DojutsuTextureConfig.registerTexture(modId, itemId,
                defaultTexture, leftEyeTexture, rightEyeTexture, predicate);
    }

    public static void registerDojutsuTexture(String itemId,
                                              String defaultTexture,
                                              String leftEyeTexture,
                                              String rightEyeTexture) {
        String modId = Objects.requireNonNull(Loader.instance()
                .activeModContainer()).getModId();
        registerDojutsuTexture(modId, itemId, defaultTexture, leftEyeTexture, rightEyeTexture);
    }

    public static boolean hasCustomTexture(ItemStack stack) {
        return DojutsuTextureConfig.getTextureInfo(stack) != null;
    }

    public static ResourceLocation getTextureForSlot(ItemStack stack, int slot) {
        return DojutsuTextureConfig.getTextureForSlot(stack, slot);
    }

    @SideOnly(Side.CLIENT)
    public static void bindTextureForSlot(ItemStack stack, int slot) {
        ResourceLocation texture = getTextureForSlot(stack, slot);
        if (texture != null) {
            net.minecraft.client.Minecraft.getMinecraft()
                    .getTextureManager().bindTexture(texture);
        }
    }

    public static String createExampleConfig() {
        return "{\n" +
                "  \"textures\": {\n" +
                "    \"yourmodid:your_dojutsu_item\": {\n" +
                "      \"default\": \"yourmodid:textures/models/armor/your_dojutsu.png\",\n" +
                "      \"left_eye\": \"yourmodid:textures/armor/your_dojutsu_left.png\",\n" +
                "      \"right_eye\": \"yourmodid:textures/armor/your_dojutsu_right.png\"\n" +
                "    }\n" +
                "  }\n" +
                "}";
    }
}