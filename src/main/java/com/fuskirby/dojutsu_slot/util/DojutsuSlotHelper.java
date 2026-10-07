package com.fuskirby.dojutsu_slot.util;

import com.fuskirby.dojutsu_slot.DojutsuSlot;
import com.fuskirby.dojutsu_slot.DojutsuSlotContext;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.narutomod.item.ItemDojutsu;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Predicate;
import com.fuskirby.dojutsu_slot.enums.WorldMode;

public class DojutsuSlotHelper {

    public static boolean isDojutsuItem(ItemStack stack) {
        return !stack.isEmpty() && stack.getItem() instanceof ItemDojutsu.Base;
    }

    public static ItemStack getLeftDojutsu(EntityPlayer player) {
        return DojutsuSlot.invMan.getDojutsuSlotInventory(player.getUniqueID()).getStackInSlot(0);
    }

    public static ItemStack getRightDojutsu(EntityPlayer player) {
        return DojutsuSlot.invMan.getDojutsuSlotInventory(player.getUniqueID()).getStackInSlot(1);
    }

    public static ItemStack getLeftTomoeRinnegan(EntityPlayer player) {
        return DojutsuSlot.invMan.getDojutsuSlotInventory(player.getUniqueID()).getStackInSlot(2);
    }

    public static ItemStack getRightTomoeRinnegan(EntityPlayer player) {
        return DojutsuSlot.invMan.getDojutsuSlotInventory(player.getUniqueID()).getStackInSlot(3);
    }

    public static ItemStack getHelmet(EntityPlayer player) {
        return player.inventory.armorInventory.get(3);
    }

    public static ItemStack selectDojutsuForJutsu(EntityPlayer player) {
        ItemStack left = getLeftDojutsu(player);
        ItemStack right = getRightDojutsu(player);
        ItemStack helmet = getHelmet(player);

        return selectSlotForJutsu(left, right, helmet, player, 1, 2);
    }

    public static ItemStack selectRinneganTomoeForJutsu(EntityPlayer player) {
        ItemStack left = getLeftTomoeRinnegan(player);
        ItemStack right = getRightTomoeRinnegan(player);
        ItemStack other_dojutsu = selectDojutsuForJutsu(player);

        return selectSlotForJutsu(left, right, other_dojutsu, player, 3, 4);
    }

    public static ItemStack selectSlotForJutsu(ItemStack left, ItemStack right, ItemStack helmet, EntityPlayer player, int left_index, int right_index) {
        boolean hasLeft = isDojutsuItem(left);
        boolean hasRight = isDojutsuItem(right);
        boolean ctrlPressed = player.getEntityData().getBoolean("dojutsu_ctrl_pressed");

        if (hasLeft && hasRight) {
            if (ctrlPressed) {
                DojutsuSlotContext.setCurrentSlot(right_index);
                return right;
            } else {
                DojutsuSlotContext.setCurrentSlot(left_index);
                return left;
            }
        } else if (hasLeft) {
            DojutsuSlotContext.setCurrentSlot(left_index);
            return left;
        } else if (hasRight) {
            DojutsuSlotContext.setCurrentSlot(right_index);
            return right;
        } else {
            DojutsuSlotContext.setCurrentSlot(0);
            return helmet;
        }
    }

    public static boolean shouldExecuteLeftSlot(EntityPlayer player) {
        boolean hasLeft = isDojutsuItem(getLeftDojutsu(player));
        boolean hasRight = isDojutsuItem(getRightDojutsu(player));
        boolean ctrlPressed = player.getEntityData().getBoolean("dojutsu_ctrl_pressed");

        return !hasLeft || !hasRight || !ctrlPressed;
    }

    public static boolean shouldExecuteRightSlot(EntityPlayer player) {
        boolean hasLeft = isDojutsuItem(getLeftDojutsu(player));
        boolean hasRight = isDojutsuItem(getRightDojutsu(player));
        boolean ctrlPressed = player.getEntityData().getBoolean("dojutsu_ctrl_pressed");

        return !hasLeft || !hasRight || ctrlPressed;
    }

    public static boolean isExpansionEnabled(EntityPlayer player) {
        boolean hasAddon = net.minecraftforge.fml.common.Loader.isModLoaded("dojutsu_addon");
        if (!hasAddon) return false;
        WorldMode mode = WorldModeHelper.getMode(player.world);
        return mode == WorldMode.DOJUTSU;
    }

    public static void forEachDojutsuSlot(EntityPlayer player, BiConsumer<Integer, ItemStack> consumer) {
        consumer.accept(-1, player.inventory.armorInventory.get(3));

        boolean expanded = isExpansionEnabled(player);
        int[] slots = expanded ? new int[]{0, 1, 2, 3} : new int[]{0, 1};
        for (int idx : slots) {
            ItemStack stack = DojutsuSlot.invMan.getDojutsuSlotInventory(player.getUniqueID()).getStackInSlot(idx);
            consumer.accept(idx, stack);
        }
    }

    public static void forEachDojutsuSlotDo(EntityPlayer player, Consumer<ItemStack> action) {
        forEachDojutsuSlot(player, (idx, stack) -> action.accept(stack));
    }

    public static boolean anySlotMatches(EntityPlayer player, Predicate<ItemStack> predicate) {
        boolean[] found = new boolean[1];
        forEachDojutsuSlot(player, (idx, stack) -> {
            if (!found[0] && predicate.test(stack)) found[0] = true;
        });
        return found[0];
    }

    public static String getDojutsuState(ItemStack stack) {
        NBTTagCompound tag = stack.getTagCompound();
        return (tag != null && tag.hasKey("dojutsu_state")) ? tag.getString("dojutsu_state") : null;
    }

    public static void setDojutsuState(ItemStack stack, String state) {
        NBTTagCompound tag = stack.getTagCompound();
        if (tag == null) {
            tag = new NBTTagCompound();
        }
        tag.setString("dojutsu_state", state);
        stack.setTagCompound(tag);
    }
}