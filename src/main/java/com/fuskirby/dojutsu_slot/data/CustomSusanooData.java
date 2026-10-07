package com.fuskirby.dojutsu_slot.data;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;

public class CustomSusanooData {
    private static final String HAS_SUSANOO_AWAKENED = "has_awakened_susanoo";
    private static final String SUSANOO_COLOR = "susanoo_color";

    public static boolean hasAwakenedSusanoo(EntityPlayer player) {
        return player.getEntityData().getBoolean(HAS_SUSANOO_AWAKENED);
    }

    public static void setAwakenedSusanoo(EntityPlayer player, int color) {
        NBTTagCompound persistent = player.getEntityData();
        persistent.setBoolean(HAS_SUSANOO_AWAKENED, true);
        persistent.setInteger(SUSANOO_COLOR, color);
    }

    public static int getSusanooColor(EntityPlayer player) {
        return player.getEntityData().getInteger(SUSANOO_COLOR);
    }
}
