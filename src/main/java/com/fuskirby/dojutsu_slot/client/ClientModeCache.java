package com.fuskirby.dojutsu_slot.client;

import com.fuskirby.dojutsu_slot.enums.WorldMode;

public class ClientModeCache {
    private static WorldMode currentMode = WorldMode.CLASSIC;

    public static void setCurrentMode(WorldMode mode) {
        currentMode = mode;
    }

    public static WorldMode getCurrentMode() {
        return currentMode;
    }
}