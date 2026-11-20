package com.fuskirby.dojutsu_slot.compat;

import net.addonraba.util.KekkeiGenkai;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public final class AddonRbnlCompat {
    private AddonRbnlCompat() {}

    public static ItemStack getMangekyoItem() {
        Item item = KekkeiGenkai.MANGEKYO.getItem();
        return new ItemStack(item, 1);
    }
}