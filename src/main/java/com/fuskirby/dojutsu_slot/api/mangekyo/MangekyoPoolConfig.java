package com.fuskirby.dojutsu_slot.api.mangekyo;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraftforge.fml.common.Loader;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

class MangekyoPoolConfig {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final List<String> API_REGISTERED_IDS = new CopyOnWriteArrayList<>();
    private static final Map<String, List<String>> MOD_TO_ITEMS = new ConcurrentHashMap<>();
    private static List<String> FINAL_POOL = null;
    private static boolean locked = false;

    static void registerMangekyo(String itemId) {
        if (locked) throw new IllegalStateException("Pool locked");
        API_REGISTERED_IDS.add(itemId);
    }

    static void registerMangekyos(String... itemIds) {
        for (String id : itemIds) registerMangekyo(id);
    }

    static void registerMangekyosForMod(String modid, List<String> itemIds) {
        if (locked) throw new IllegalStateException("Pool locked");
        MOD_TO_ITEMS.computeIfAbsent(modid, k -> new CopyOnWriteArrayList<>()).addAll(itemIds);
    }

    static void loadConfig() {
        try {
            File configDir = new File(Loader.instance().getConfigDir(), "dojutsu_slot");
            if (!configDir.exists()) configDir.mkdirs();
            File configFile = new File(configDir, "mangekyo_pool.json");
            if (!configFile.exists()) {
                createDefaultConfig(configFile);
            } else {
                String json = FileUtils.readFileToString(configFile, StandardCharsets.UTF_8);
                JsonObject root = new JsonParser().parse(json).getAsJsonObject();
                if (root.has("mangekyo_items")) {
                    root.getAsJsonArray("mangekyo_items").forEach(element -> {
                        JsonObject entry = element.getAsJsonObject();
                        String modid = entry.get("modid").getAsString();
                        List<String> items = new ArrayList<>();
                        entry.getAsJsonArray("items").forEach(i -> items.add(i.getAsString()));
                        MOD_TO_ITEMS.put(modid, items);
                    });
                }
            }
        } catch (Exception e) {
            System.err.println("[DojutsuSlot] Failed to load mangekyo_pool.json: " + e.getMessage());
        }
    }

    private static void createDefaultConfig(File configFile) throws IOException {
        JsonObject root = new JsonObject();
        root.addProperty("version", "1.0.0");
        root.addProperty("description", "Mangekyo Sharingan awakening pool.");

        List<JsonObject> entries = new ArrayList<>();
        JsonObject narutoEntry = new JsonObject();
        narutoEntry.addProperty("modid", "narutomod");
        narutoEntry.add("items", GSON.toJsonTree(Arrays.asList(
                "narutomod:mangekyosharinganhelmet",
                "narutomod:mangekyosharinganobitohelmet"
        )));
        entries.add(narutoEntry);
        root.add("mangekyo_items", GSON.toJsonTree(entries));

        FileUtils.writeStringToFile(configFile, GSON.toJson(root), StandardCharsets.UTF_8);
        MOD_TO_ITEMS.put("narutomod", Arrays.asList(
                "narutomod:mangekyosharinganhelmet",
                "narutomod:mangekyosharinganobitohelmet"
        ));
    }

    static void scanAndRegisterMangekyos() {}

    static void lockAndBuildPool() {
        if (locked) return;
        locked = true;

        Set<String> poolSet = new LinkedHashSet<>();
        for (Map.Entry<String, List<String>> entry : MOD_TO_ITEMS.entrySet()) {
            if (Loader.isModLoaded(entry.getKey())) {
                poolSet.addAll(entry.getValue());
            }
        }
        poolSet.addAll(API_REGISTERED_IDS);
        if (poolSet.isEmpty()) {
            poolSet.add("narutomod:mangekyo_sharingan_helmet");
            poolSet.add("narutomod:mangekyo_sharingan_obito_helmet");
        }
        FINAL_POOL = Collections.unmodifiableList(new ArrayList<>(poolSet));
        System.out.println("[DojutsuSlot] Mangekyo pool built: " + FINAL_POOL.size() + " items");
    }

    static List<String> getPool() {
        if (FINAL_POOL == null) throw new IllegalStateException("Pool not built yet");
        return FINAL_POOL;
    }

    static String getRandomMangekyoId() {
        return getPool().get(new Random().nextInt(getPool().size()));
    }
}