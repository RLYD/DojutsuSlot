package com.fuskirby.dojutsu_slot;

import com.fuskirby.dojutsu_slot.inventory.InventoryDojutsuSlot;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public class DojutsuSlotContext {
    private static final ThreadLocal<Integer> CURRENT_SLOT_CONTEXT = new ThreadLocal<>();

    public static final int SLOT_HELMET = 0;
    public static final int SLOT_LEFT_ORIGINAL = 1;
    public static final int SLOT_RIGHT_ORIGINAL = 2;
    public static final int SLOT_LEFT_EXTENDED = 3;
    public static final int SLOT_RIGHT_EXTENDED = 4;

    public static void setCurrentSlot(int slotType) {
        CURRENT_SLOT_CONTEXT.set(slotType);
    }

    public static int getCurrentSlot() {
        Integer slot = CURRENT_SLOT_CONTEXT.get();
        return slot != null ? slot : 0;
    }

    public static void clearCurrentSlot() {
        CURRENT_SLOT_CONTEXT.remove();
    }

    public static ItemStack getDojutsuFromCurrentSlot(EntityLivingBase entity) {
        if (!(entity instanceof EntityPlayer)) {
            return ItemStack.EMPTY;
        }

        EntityPlayer player = (EntityPlayer) entity;
        int slotType = getCurrentSlot();

        InventoryDojutsuSlot inv = DojutsuSlot.invMan.getDojutsuSlotInventory(player.getUniqueID());

        switch (slotType) {
            case SLOT_HELMET:
                return player.inventory.armorInventory.get(3);
            case SLOT_LEFT_ORIGINAL:
                return inv.getStackInSlot(0);
            case SLOT_RIGHT_ORIGINAL:
                return inv.getStackInSlot(1);
            case SLOT_LEFT_EXTENDED:
                return inv.getStackInSlot(2);
            case SLOT_RIGHT_EXTENDED:
                return inv.getStackInSlot(3);
            default:
                return ItemStack.EMPTY;
        }
    }
}