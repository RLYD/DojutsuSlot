package com.fuskirby.dojutsu_slot.api.mangekyo;

import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class MangekyoPoolAPI {

    public static void preInit(FMLPreInitializationEvent event) {
        MangekyoPoolConfig.loadConfig();
        MangekyoPoolConfig.scanAndRegisterMangekyos();
        MangekyoPoolConfig.lockAndBuildPool();
    }

    public static void registerMangekyo(String itemId) {
        MangekyoPoolConfig.registerMangekyo(itemId);
    }

    public static void registerMangekyos(String... itemIds) {
        MangekyoPoolConfig.registerMangekyos(itemIds);
    }

    public static java.util.List<String> getAvailableMangekyos() {
        return MangekyoPoolConfig.getPool();
    }

    public static String getRandomMangekyoId() {
        return MangekyoPoolConfig.getRandomMangekyoId();
    }

    public static void lockPool() {
        MangekyoPoolConfig.lockAndBuildPool();
    }
}