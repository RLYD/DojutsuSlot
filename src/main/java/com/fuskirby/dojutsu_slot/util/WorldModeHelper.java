package com.fuskirby.dojutsu_slot.util;

import com.fuskirby.dojutsu_slot.client.ClientModeCache;
import com.fuskirby.dojutsu_slot.data.ModWorldData;
import com.fuskirby.dojutsu_slot.enums.WorldMode;
import net.minecraft.world.World;

public class WorldModeHelper {

    public static WorldMode getMode(World world) {
        if (world == null) return WorldMode.CLASSIC;
        if (!world.isRemote) {
            ModWorldData data = ModWorldData.get(world);
            return data != null ? data.getMode() : WorldMode.CLASSIC;
        } else {
            return ClientModeCache.getCurrentMode();
        }
    }

    public static boolean isDojutsuMode(World world) {
        return getMode(world) == WorldMode.DOJUTSU;
    }
}