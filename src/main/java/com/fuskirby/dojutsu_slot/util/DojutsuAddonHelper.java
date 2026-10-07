package com.fuskirby.dojutsu_slot.util;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import java.util.UUID;

public class DojutsuAddonHelper {

    public static final String AWAKENER_UUID_TAG = "awakener_UUID";
    public static final String AWAKENER_NAME_TAG = "awakener_name";

    public static ItemStack addRegisteredTag(ItemStack stack) {
        if (!stack.isEmpty()) {
            NBTTagCompound nbt = stack.getTagCompound();
            if (nbt == null) {
                nbt = new NBTTagCompound();
            }
            nbt.setBoolean("DojutsuAddon_Registered", true);
            stack.setTagCompound(nbt);
        }
        return stack;
    }

    public static boolean hasRegisteredTag(ItemStack stack) {
        if (stack.getTagCompound() != null) {
            return !stack.isEmpty() && stack.hasTagCompound() &&
                    stack.getTagCompound().getBoolean("DojutsuAddon_Registered");
        }
        return false;
    }

    public static void setAwakener(ItemStack stack, UUID ownerId, String ownerName) {
        if (stack.isEmpty()) return;
        NBTTagCompound nbt = stack.getTagCompound();
        if (nbt == null) nbt = new NBTTagCompound();
        if (ownerId != null) nbt.setString(AWAKENER_UUID_TAG, ownerId.toString());
        if (ownerName != null) nbt.setString(AWAKENER_NAME_TAG, ownerName);
        stack.setTagCompound(nbt);
    }
}
