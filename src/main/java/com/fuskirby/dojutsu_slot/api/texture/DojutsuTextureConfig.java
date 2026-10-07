package com.fuskirby.dojutsu_slot.api.texture;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.Loader;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Predicate;

public class DojutsuTextureConfig {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Map<String, List<TextureInfo>> TEXTURE_MAP = new ConcurrentHashMap<>();

    public static class TextureInfo {
        public final String modId;
        public final String itemId;
        public final ResourceLocation defaultTexture;
        public final ResourceLocation leftEyeTexture;
        public final ResourceLocation rightEyeTexture;
        private final Predicate<ItemStack> predicate;

        public TextureInfo(String modId, String itemId,
                           String defaultTex, String leftTex, String rightTex) {
            this(modId, itemId, defaultTex, leftTex, rightTex, (stack) -> true);
        }

        public TextureInfo(String modId, String itemId,
                           String defaultTex, String leftTex, String rightTex,
                           Predicate<ItemStack> predicate) {
            this.modId = modId;
            this.itemId = itemId;
            this.defaultTexture = defaultTex != null ? new ResourceLocation(defaultTex) : null;
            this.leftEyeTexture = leftTex != null ? new ResourceLocation(leftTex) : null;
            this.rightEyeTexture = rightTex != null ? new ResourceLocation(rightTex) : null;
            this.predicate = predicate;
        }

        public boolean matches(ItemStack stack) {
            return predicate.test(stack);
        }

        public ResourceLocation getTextureForSlot(int slot) {
            switch (slot) {
                case 1: return leftEyeTexture != null ? leftEyeTexture : defaultTexture;
                case 2: return rightEyeTexture != null ? rightEyeTexture : defaultTexture;
                default: return defaultTexture;
            }
        }
    }

    public static void registerTexture(String modId, String itemId,
                                       String defaultTexture,
                                       String leftEyeTexture,
                                       String rightEyeTexture) {
        registerTexture(modId, itemId, defaultTexture, leftEyeTexture, rightEyeTexture, (stack) -> true);
    }

    public static void registerTexture(String modId, String itemId,
                                       String defaultTexture,
                                       String leftEyeTexture,
                                       String rightEyeTexture,
                                       Predicate<ItemStack> predicate) {
        String key = modId + ":" + itemId;
        List<TextureInfo> list = TEXTURE_MAP.computeIfAbsent(key, k -> new CopyOnWriteArrayList<>());
        list.add(new TextureInfo(modId, itemId, defaultTexture, leftEyeTexture, rightEyeTexture, predicate));

        saveToConfigFile();
    }

    public static TextureInfo getTextureInfo(String modId, String itemId) {
        String key = modId + ":" + itemId;
        List<TextureInfo> list = TEXTURE_MAP.get(key);
        if (list != null) {
            for (TextureInfo info : list) {
                if (info.predicate.test(ItemStack.EMPTY)) {
                    return info;
                }
            }
            if (!list.isEmpty()) {
                return list.get(0);
            }
        }
        return null;
    }

    public static TextureInfo getTextureInfo(ItemStack stack) {
        if (stack.isEmpty()) return null;
        String modId = Objects.requireNonNull(stack.getItem().getRegistryName()).getResourceDomain();
        String itemId = stack.getItem().getRegistryName().getResourcePath();
        String key = modId + ":" + itemId;
        List<TextureInfo> list = TEXTURE_MAP.get(key);
        if (list != null) {
            for (TextureInfo info : list) {
                if (info.matches(stack)) {
                    return info;
                }
            }
        }
        return null;
    }

    public static ResourceLocation getTextureForSlot(ItemStack stack, int slot) {
        TextureInfo info = getTextureInfo(stack);
        return info != null ? info.getTextureForSlot(slot) : null;
    }

    public static Map<String, List<TextureInfo>> getAllTextures() {
        return Collections.unmodifiableMap(TEXTURE_MAP);
    }

    public static void loadConfigFile() {
        try {
            File configDir = new File(Loader.instance().getConfigDir(), "dojutsu_slot");
            if (!configDir.exists()) {
                configDir.mkdirs();
            }

            File textureFile = new File(configDir, "dojutsu_textures.json");
            if (!textureFile.exists()) {
                createDefaultConfig(textureFile);
                return;
            }

            String json = FileUtils.readFileToString(textureFile, StandardCharsets.UTF_8);
            JsonObject root = new JsonParser().parse(json).getAsJsonObject();

            if (root.has("textures")) {
                JsonObject textures = root.getAsJsonObject("textures");
                textures.entrySet().forEach(entry -> {
                    String key = entry.getKey();
                    JsonObject texInfo = entry.getValue().getAsJsonObject();

                    String defaultTex = texInfo.has("default") ?
                            texInfo.get("default").getAsString() : null;
                    String leftTex = texInfo.has("left_eye") ?
                            texInfo.get("left_eye").getAsString() : null;
                    String rightTex = texInfo.has("right_eye") ?
                            texInfo.get("right_eye").getAsString() : null;

                    String[] parts = key.split(":");
                    if (parts.length == 2) {
                        registerTexture(parts[0], parts[1], defaultTex, leftTex, rightTex);
                    }
                });
            }

            System.out.println("[DojutsuSlotAPI] Loaded texture configurations");

        } catch (Exception e) {
            System.err.println("[DojutsuSlotAPI] Failed to load texture config: " + e.getMessage());
        }
    }

    private static void saveToConfigFile() {
        try {
            File configDir = new File(Loader.instance().getConfigDir(), "dojutsu_slot");
            if (!configDir.exists()) {
                configDir.mkdirs();
            }

            File textureFile = new File(configDir, "dojutsu_textures.json");

            JsonObject root = new JsonObject();
            JsonObject textures = new JsonObject();

            for (Map.Entry<String, List<TextureInfo>> entry : TEXTURE_MAP.entrySet()) {
                for (TextureInfo info : entry.getValue()) {
                    if (info.predicate.test(ItemStack.EMPTY)) {
                        JsonObject texInfo = new JsonObject();
                        if (info.defaultTexture != null) {
                            texInfo.addProperty("default", info.defaultTexture.toString());
                        }
                        if (info.leftEyeTexture != null) {
                            texInfo.addProperty("left_eye", info.leftEyeTexture.toString());
                        }
                        if (info.rightEyeTexture != null) {
                            texInfo.addProperty("right_eye", info.rightEyeTexture.toString());
                        }
                        textures.add(entry.getKey(), texInfo);
                        break;
                    }
                }
            }

            root.add("textures", textures);
            root.addProperty("version", "1.0.0");
            root.addProperty("description", "Dojutsu Slot Texture Configuration");

            FileUtils.writeStringToFile(textureFile, GSON.toJson(root), StandardCharsets.UTF_8);

        } catch (IOException e) {
            System.err.println("[DojutsuSlotAPI] Failed to save texture config: " + e.getMessage());
        }
    }

    private static void createDefaultConfig(File textureFile) {
        JsonObject root = new JsonObject();
        JsonObject textures = new JsonObject();

        // byakugan
//        JsonObject byakugan = new JsonObject();
//        byakugan.addProperty("default", "narutomod:textures/byakuganhelmet.png");
//        byakugan.addProperty("left_eye", "dojutsu_slot:textures/models/armor/byakugan/byakuganhelmet_left.png");
//        byakugan.addProperty("right_eye", "dojutsu_slot:textures/models/armor/byakugan/byakuganhelmet_right.png");
//        textures.add("narutomod:byakuganhelmet", byakugan);

        // sharingan
        JsonObject sharingan = new JsonObject();
        sharingan.addProperty("default", "narutomod:textures/sharinganhelmet.png");
        sharingan.addProperty("left_eye", "dojutsu_slot:textures/models/armor/sharingan/sharinganhelmet_left.png");
        sharingan.addProperty("right_eye", "dojutsu_slot:textures/models/armor/sharingan/sharinganhelmet_right.png");
        textures.add("narutomod:sharinganhelmet", sharingan);

        JsonObject sharingan_amaterasu = new JsonObject();
        sharingan_amaterasu.addProperty("default", "narutomod:textures/mangekyosharinganhelmet_sasuke.png");
        sharingan_amaterasu.addProperty("left_eye", "dojutsu_slot:textures/models/armor/sharingan/mangekyosharinganhelmet_sasuke_left.png");
        sharingan_amaterasu.addProperty("right_eye", "dojutsu_slot:textures/models/armor/sharingan/mangekyosharinganhelmet_sasuke_right.png");
        textures.add("narutomod:mangekyosharinganhelmet", sharingan_amaterasu);

        JsonObject sharingan_kamui = new JsonObject();
        sharingan_kamui.addProperty("default", "narutomod:textures/mangekyosharinganhelmet_obito.png");
        sharingan_kamui.addProperty("left_eye", "dojutsu_slot:textures/models/armor/sharingan/mangekyosharinganhelmet_obito_left.png");
        sharingan_kamui.addProperty("right_eye", "dojutsu_slot:textures/models/armor/sharingan/mangekyosharinganhelmet_obito_right.png");
        textures.add("narutomod:mangekyosharinganobitohelmet", sharingan_kamui);

        JsonObject sharingan_eternal = new JsonObject();
        sharingan_eternal.addProperty("default", "narutomod:textures/mangekyosharinganhelmet_eternal.png");
        sharingan_eternal.addProperty("left_eye", "dojutsu_slot:textures/models/armor/sharingan/mangekyosharinganhelmet_eternal_left.png");
        sharingan_eternal.addProperty("right_eye", "dojutsu_slot:textures/models/armor/sharingan/mangekyosharinganhelmet_eternal_right.png");
        textures.add("narutomod:mangekyosharinganeternalhelmet", sharingan_eternal);

        // rinnegan
//        JsonObject rinnegan = new JsonObject();
//        rinnegan.addProperty("default", "narutomod:textures/rinneganhelmet.png");
//        rinnegan.addProperty("left_eye", "dojutsu_slot:textures/models/armor/rinnegan/rinneganhelmet_left.png");
//        rinnegan.addProperty("right_eye", "dojutsu_slot:textures/models/armor/rinnegan/rinneganhelmet_right.png");
//        textures.add("narutomod:rinneganhelmet", rinnegan);

        // tenseigan
        JsonObject tenseigan = new JsonObject();
        tenseigan.addProperty("default", "narutomod:textures/tenseiganhelmet.png");
        tenseigan.addProperty("left_eye", "dojutsu_slot:textures/models/armor/tenseigan/tenseiganhelmet_left.png");
        tenseigan.addProperty("right_eye", "dojutsu_slot:textures/models/armor/tenseigan/tenseiganhelmet_right.png");
        textures.add("narutomod:tenseiganhelmet", tenseigan);

        root.add("textures", textures);
        root.addProperty("version", "1.0.0");
        root.addProperty("description", "Dojutsu Slot Texture Configuration");

        try {
            FileUtils.writeStringToFile(textureFile, GSON.toJson(root), StandardCharsets.UTF_8);

//            registerTexture("narutomod", "byakuganhelmet",
//                    "narutomod:textures/byakuganhelmet.png",
//                    "dojutsu_slot:textures/models/armor/byakugan/byakuganhelmet_left.png",
//                    "dojutsu_slot:textures/models/armor/byakugan/byakuganhelmet_right.png");

            registerTexture("narutomod", "sharinganhelmet",
                    "narutomod:textures/sharinganhelmet.png",
                    "dojutsu_slot:textures/models/armor/sharingan/sharinganhelmet_left.png",
                    "dojutsu_slot:textures/models/armor/sharingan/sharinganhelmet_right.png");

            registerTexture("narutomod", "mangekyosharinganhelmet",
                    "narutomod:textures/mangekyosharinganhelmet_sasuke.png",
                    "dojutsu_slot:textures/models/armor/sharingan/mangekyosharinganhelmet_sasuke_left.png",
                    "dojutsu_slot:textures/models/armor/sharingan/mangekyosharinganhelmet_sasuke_right.png");

            registerTexture("narutomod", "mangekyosharinganobitohelmet",
                    "narutomod:textures/mangekyosharinganhelmet_obito.png",
                    "dojutsu_slot:textures/models/armor/sharingan/mangekyosharinganhelmet_obito_left.png",
                    "dojutsu_slot:textures/models/armor/sharingan/mangekyosharinganhelmet_obito_right.png");

            registerTexture("narutomod", "mangekyosharinganeternalhelmet",
                    "narutomod:textures/mangekyosharinganhelmet_eternal.png",
                    "dojutsu_slot:textures/models/armor/sharingan/mangekyosharinganhelmet_eternal_left.png",
                    "dojutsu_slot:textures/models/armor/sharingan/mangekyosharinganhelmet_eternal_right.png");

//            registerTexture("narutomod", "rinneganhelmet",
//                    "narutomod:textures/rinneganhelmet.png",
//                    "dojutsu_slot:textures/models/armor/rinnegan/rinneganhelmet_left.png",
//                    "dojutsu_slot:textures/models/armor/rinnegan/rinneganhelmet_right.png");

            registerTexture("narutomod", "tenseiganhelmet",
                    "narutomod:textures/tenseiganhelmet.png",
                    "dojutsu_slot:textures/models/armor/tenseigan/tenseiganhelmet_left.png",
                    "dojutsu_slot:textures/models/armor/tenseigan/tenseiganhelmet_right.png");

        } catch (IOException e) {
            System.err.println("[DojutsuSlotAPI] Failed to create default config: " + e.getMessage());
        }
    }

    public static void scanAndRegisterTextures() {
        registerAddonrbnlModTextures();
        registerAhznbcursemarkaddonModTextures();
    }

    private static void registerAddonrbnlModTextures() {
        // sharingan
        registerTexture("addonrbnl", "mangekyosharinganazazaelhelmet",
                "addonrbnl:textures/mangekyosharinganhelmet_azazael.png",
                "dojutsu_slot:textures/compat/addonrbnl/sharingan/mangekyosharinganhelmet_azazael_left.png",
                "dojutsu_slot:textures/compat/addonrbnl/sharingan/mangekyosharinganhelmet_azazael_right.png");

        registerTexture("addonrbnl", "mangekyosharinganazazaeleternalhelmet",
                "addonrbnl:textures/mangekyosharinganeternalhelmet_azazael.png",
                "dojutsu_slot:textures/compat/addonrbnl/sharingan/mangekyosharinganeternalhelmet_azazael_left.png",
                "dojutsu_slot:textures/compat/addonrbnl/sharingan/mangekyosharinganeternalhelmet_azazael_right.png");

        registerTexture("addonrbnl", "mangekyosharinganfugakuhelmet",
                "addonrbnl:textures/mangekyosharinganhelmet_fugaku.png",
                "dojutsu_slot:textures/compat/addonrbnl/sharingan/mangekyosharinganhelmet_fugaku_left.png",
                "dojutsu_slot:textures/compat/addonrbnl/sharingan/mangekyosharinganhelmet_fugaku_right.png");

        registerTexture("addonrbnl", "mangekyosharinganindrahelmet",
                "addonrbnl:textures/mangekyosharinganhelmet_indra.png",
                "dojutsu_slot:textures/compat/addonrbnl/sharingan/mangekyosharinganhelmet_indra_left.png",
                "dojutsu_slot:textures/compat/addonrbnl/sharingan/mangekyosharinganhelmet_indra_right.png");

        registerTexture("addonrbnl", "mangekyosharinganindraeternalhelmet",
                "addonrbnl:textures/mangekyosharinganeternalhelmet_indra.png",
                "dojutsu_slot:textures/compat/addonrbnl/sharingan/mangekyosharinganeternalhelmet_indra_left.png",
                "dojutsu_slot:textures/compat/addonrbnl/sharingan/mangekyosharinganeternalhelmet_indra_right.png");

        registerTexture("addonrbnl", "mangekyosharinganitachihelmet",
                "addonrbnl:textures/mangekyosharinganhelmet_itachi.png",
                "dojutsu_slot:textures/compat/addonrbnl/sharingan/mangekyosharinganhelmet_itachi_left.png",
                "dojutsu_slot:textures/compat/addonrbnl/sharingan/mangekyosharinganhelmet_itachi_right.png");

        registerTexture("addonrbnl", "mangekyosharinganitachieternalhelmet",
                "addonrbnl:textures/mangekyosharinganeternalhelmet_itachil.png",
                "dojutsu_slot:textures/compat/addonrbnl/sharingan/mangekyosharinganeternalhelmet_itachi_left.png",
                "dojutsu_slot:textures/compat/addonrbnl/sharingan/mangekyosharinganeternalhelmet_itachi_right.png");

        registerTexture("addonrbnl", "mangekyosharinganizunahelmet",
                "addonrbnl:textures/mangekyosharinganhelmet_izuna.png",
                "dojutsu_slot:textures/compat/addonrbnl/sharingan/mangekyosharinganhelmet_izuna_left.png",
                "dojutsu_slot:textures/compat/addonrbnl/sharingan/mangekyosharinganhelmet_izuna_right.png");

        registerTexture("addonrbnl", "mangekyosharinganjikanhelmet",
                "addonrbnl:textures/mangekyosharinganhelmet_jikan.png",
                "dojutsu_slot:textures/compat/addonrbnl/sharingan/mangekyosharinganhelmet_jikan_left.png",
                "dojutsu_slot:textures/compat/addonrbnl/sharingan/mangekyosharinganhelmet_jikan_right.png");

        registerTexture("addonrbnl", "mangekyosharinganjikaneternalhelmet",
                "addonrbnl:textures/mangekyosharinganeternalhelmet_jikan.png",
                "dojutsu_slot:textures/compat/addonrbnl/sharingan/mangekyosharinganeternalhelmet_jikan_left.png",
                "dojutsu_slot:textures/compat/addonrbnl/sharingan/mangekyosharinganeternalhelmet_jikan_right.png");

        registerTexture("addonrbnl", "mangekyosharinganmadarahelmet",
                "addonrbnl:textures/mangekyosharinganhelmet_madara.png",
                "dojutsu_slot:textures/compat/addonrbnl/sharingan/mangekyosharinganhelmet_madara_left.png",
                "dojutsu_slot:textures/compat/addonrbnl/sharingan/mangekyosharinganhelmet_madara_right.png");

        registerTexture("addonrbnl", "mangekyosharinganmadaraeternalhelmet",
                "addonrbnl:textures/mangekyosharinganeternalhelmet_madara.png",
                "dojutsu_slot:textures/compat/addonrbnl/sharingan/mangekyosharinganeternalhelmet_madara_left.png",
                "dojutsu_slot:textures/compat/addonrbnl/sharingan/mangekyosharinganeternalhelmet_madara_right.png");

        registerTexture("addonrbnl", "mangekyosharingannakahelmet",
                "addonrbnl:textures/mangekyosharinganhelmet_naka.png",
                "dojutsu_slot:textures/compat/addonrbnl/sharingan/mangekyosharinganhelmet_naka_left.png",
                "dojutsu_slot:textures/compat/addonrbnl/sharingan/mangekyosharinganhelmet_naka_right.png");

        registerTexture("addonrbnl", "mangekyosharingannaorihelmet",
                "addonrbnl:textures/mangekyosharinganhelmet_naori.png",
                "dojutsu_slot:textures/compat/addonrbnl/sharingan/mangekyosharinganhelmet_naori_left.png",
                "dojutsu_slot:textures/compat/addonrbnl/sharingan/mangekyosharinganhelmet_naori_right.png");

        registerTexture("addonrbnl", "mangekyosharingannaorieternalhelmet",
                "addonrbnl:textures/mangekyosharinganeternalhelmet_naori.png",
                "dojutsu_slot:textures/compat/addonrbnl/sharingan/mangekyosharinganeternalhelmet_naori_left.png",
                "dojutsu_slot:textures/compat/addonrbnl/sharingan/mangekyosharinganeternalhelmet_naori_right.png");

        registerTexture("addonrbnl", "mangekyosharingansaradahelmet",
                "addonrbnl:textures/mangekyosharinganhelmet_sarada.png",
                "dojutsu_slot:textures/compat/addonrbnl/sharingan/mangekyosharinganhelmet_sarada_left.png",
                "dojutsu_slot:textures/compat/addonrbnl/sharingan/mangekyosharinganhelmet_sarada_right.png");

        registerTexture("addonrbnl", "mangekyosharinganshinhelmet",
                "addonrbnl:textures/mangekyosharinganhelmet_shin.png",
                "dojutsu_slot:textures/compat/addonrbnl/sharingan/mangekyosharinganhelmet_shin_left.png",
                "dojutsu_slot:textures/compat/addonrbnl/sharingan/mangekyosharinganhelmet_shin_right.png");

        registerTexture("addonrbnl", "mangekyosharinganshisuihelmet",
                "addonrbnl:textures/mangekyosharinganhelmet_shisui.png",
                "dojutsu_slot:textures/compat/addonrbnl/sharingan/mangekyosharinganhelmet_shisui_left.png",
                "dojutsu_slot:textures/compat/addonrbnl/sharingan/mangekyosharinganhelmet_shisui_right.png");

        // scarleteyes
        registerTexture("addonrbnl", "scarlet_eyeshelmet",
                "addonrbnl:textures/scarleteyeshelmet.png",
                "dojutsu_slot:textures/compat/addonrbnl/scarleteyes/scarleteyeshelmet_left.png",
                "dojutsu_slot:textures/compat/addonrbnl/scarleteyes/scarleteyeshelmet_right.png");
    }

    private static void registerAhznbcursemarkaddonModTextures() {
        registerTexture("ahznbcursemarkaddon", "blue_rinneganhelmet",
                "ahznbcursemarkaddon:textures/dojutsu/rinnegan/blue/helmet",
                "dojutsu_slot:textures/compat/ahznbcursemarkaddon/rinnegan/blue/helmet_left.png",
                "dojutsu_slot:textures/compat/ahznbcursemarkaddon/rinnegan/blue/helmet_right.png");

        registerTexture("ahznbcursemarkaddon", "byakusharinganhelmet",
                "ahznbcursemarkaddon:textures/dojutsu/sharingan/byakusharinganhelmet/helmet",
                "dojutsu_slot:textures/compat/ahznbcursemarkaddon/sharingan/byakusharinganhelmet/helmet_left.png",
                "dojutsu_slot:textures/compat/ahznbcursemarkaddon/sharingan/byakusharinganhelmet/helmet_right.png");

        registerTexture("ahznbcursemarkaddon", "dudu_rinneganhelmet",
                "ahznbcursemarkaddon:textures/dojutsu/rinnegan/dudu/helmet",
                "dojutsu_slot:textures/compat/ahznbcursemarkaddon/rinnegan/dudu/helmet_left.png",
                "dojutsu_slot:textures/compat/ahznbcursemarkaddon/rinnegan/dudu/helmet_right.png");

        registerTexture("ahznbcursemarkaddon", "e_mangekyo_sharingan_infernohelmet",
                "ahznbcursemarkaddon:textures/dojutsu/ms/robberto/ems/helmet.png",
                "dojutsu_slot:textures/compat/ahznbcursemarkaddon/ms/robberto/ems/helmet_left.png",
                "dojutsu_slot:textures/compat/ahznbcursemarkaddon/ms/robberto/ems/helmet_right.png");

        registerTexture("ahznbcursemarkaddon", "fake_kokuganhelmet",
                "ahznbcursemarkaddon:textures/dojutsu/boruto/kawaki/helmet.png",
                "dojutsu_slot:textures/compat/ahznbcursemarkaddon/boruto/kawaki/helmet_left.png",
                "dojutsu_slot:textures/compat/ahznbcursemarkaddon/boruto/kawaki/helmet_right.png");

        registerTexture("ahznbcursemarkaddon", "glass_eyehelmet",
                "ahznbcursemarkaddon:textures/dojutsu/boruto/glass/helmet.png",
                "dojutsu_slot:textures/compat/ahznbcursemarkaddon/boruto/glass/helmet_left.png",
                "dojutsu_slot:textures/compat/ahznbcursemarkaddon/boruto/glass/helmet_right.png");

        registerTexture("ahznbcursemarkaddon", "jouganhelmet",
                "ahznbcursemarkaddon:textures/dojutsu/boruto/jougan/helmet.png",
                "dojutsu_slot:textures/compat/ahznbcursemarkaddon/boruto/jougan/helmet_left.png",
                "dojutsu_slot:textures/compat/ahznbcursemarkaddon/boruto/jougan/helmet_right.png");

        registerTexture("ahznbcursemarkaddon", "ketsuryuganoffhelmet",
                "ahznbcursemarkaddon:textures/dojutsu/ketsuryugan/helmet.png",
                "dojutsu_slot:textures/compat/ahznbcursemarkaddon/ketsuryugan/helmet_left.png",
                "dojutsu_slot:textures/compat/ahznbcursemarkaddon/ketsuryugan/helmet_right.png");

        registerTexture("ahznbcursemarkaddon", "ketsuryugan_onhelmet",
                "ahznbcursemarkaddon:textures/dojutsu/ketsuryugan/helmet2.png",
                "dojutsu_slot:textures/compat/ahznbcursemarkaddon/ketsuryugan/helmet2_left.png",
                "dojutsu_slot:textures/compat/ahznbcursemarkaddon/ketsuryugan/helmet2_right.png");

        registerTexture("ahznbcursemarkaddon", "mangekyo_sharingan_infernohelmet",
                "ahznbcursemarkaddon:textures/dojutsu/ms/robberto/one/helmet.png",
                "dojutsu_slot:textures/compat/ahznbcursemarkaddon/ms/robberto/one/helmet_left.png",
                "dojutsu_slot:textures/compat/ahznbcursemarkaddon/ms/robberto/one/helmet_right.png");

        registerTexture("ahznbcursemarkaddon", "mangekyo_sharingan_madarahelmet",
                "ahznbcursemarkaddon:textures/dojutsu/ms/madara/helmet.png",
                "dojutsu_slot:textures/compat/ahznbcursemarkaddon/ms/madara/helmet_left.png",
                "dojutsu_slot:textures/compat/ahznbcursemarkaddon/ms/madara/helmet_right.png");

        registerTexture("ahznbcursemarkaddon", "mangekyo_sharingan_magicianshelmet",
                "ahznbcursemarkaddon:textures/dojutsu/ms/whyhellothere/helmet.png",
                "dojutsu_slot:textures/compat/ahznbcursemarkaddon/ms/whyhellothere/helmet_left.png",
                "dojutsu_slot:textures/compat/ahznbcursemarkaddon/ms/whyhellothere/helmet_right.png");

        registerTexture("ahznbcursemarkaddon", "mangekyo_sharingan_pretahelmet",
                "ahznbcursemarkaddon:textures/dojutsu/ms/whyhellothere/helmet.png",
                "dojutsu_slot:textures/compat/ahznbcursemarkaddon/ms/robberto/2/helmet_left.png",
                "dojutsu_slot:textures/compat/ahznbcursemarkaddon/ms/robberto/2/helmet_right.png");

        registerTexture("ahznbcursemarkaddon", "ratja_eyehelmet",
                "ahznbcursemarkaddon:textures/dojutsu/ms/tyler/helmet2.png",
                "dojutsu_slot:textures/compat/ahznbcursemarkaddon/ms/tyler/helmet2_left.png",
                "dojutsu_slot:textures/compat/ahznbcursemarkaddon/ms/tyler/helmet2_right.png");

        registerTexture("ahznbcursemarkaddon", "red_rinneganhelmet",
                "ahznbcursemarkaddon:textures/dojutsu/rinnegan/red/helmet.png",
                "dojutsu_slot:textures/compat/ahznbcursemarkaddon/rinnegan/red/helmet_left.png",
                "dojutsu_slot:textures/compat/ahznbcursemarkaddon/rinnegan/red/helmet_right.png");

        registerTexture("ahznbcursemarkaddon", "yashi_eyehelmet",
                "ahznbcursemarkaddon:textures/dojutsu/random/yashi/helmet.png",
                "dojutsu_slot:textures/compat/ahznbcursemarkaddon/random/yashi/helmet_left.png",
                "dojutsu_slot:textures/compat/ahznbcursemarkaddon/random/yashi/helmet_right.png");
    }
}