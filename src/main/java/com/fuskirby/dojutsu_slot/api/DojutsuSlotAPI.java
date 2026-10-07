package com.fuskirby.dojutsu_slot.api;

import java.util.UUID;
import com.fuskirby.dojutsu_slot.DojutsuSlot;
import com.fuskirby.dojutsu_slot.api.inventory.DojutsuStacksBase;
import com.fuskirby.dojutsu_slot.api.render.DojutsuHelmetRenderAPI;
import com.fuskirby.dojutsu_slot.inventory.ContainerDojutsuSlot;
import com.fuskirby.dojutsu_slot.util.DojutsuSlotHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class DojutsuSlotAPI implements IDojutsuSlotAPI
{
    /**
     * @param uuid the UniqueID of a player (See also {@link net.minecraft.entity.player.EntityPlayer#getUniqueID() EntityPlayer.getUniqueID()})
     * @return associated {@link DojutsuStacksBase CAStacks} for the input uuid
     */
    public static DojutsuStacksBase getCAStacks(UUID uuid)
    {
        return DojutsuSlot.invMan.getDojutsuSlotInventory(uuid).getStacks();
    }

    /**
     * @param uuid the UniqueID of a player (See also {@link net.minecraft.entity.player.EntityPlayer#getUniqueID() EntityPlayer.getUniqueID()})
     * @return associated {@link DojutsuStacksBase CAStacks} for the input uuid on the Client
     */
    @SideOnly(Side.CLIENT)
    public static DojutsuStacksBase getCAStacksClient(UUID uuid)
    {
        return DojutsuSlot.invMan.getDojutsuSlotInventoryClient(uuid).getStacks();
    }

    @Override
    public void setSlotValidator(IDojutsuSlotAPI.ISlotValidator validator) {
        ContainerDojutsuSlot.setSlotValidator(new ContainerDojutsuSlot.ISlotValidator() {
            @Override
            public boolean isItemValidForSlot(int slot, ItemStack stack, EntityPlayer player) {
                return validator.isItemValidForSlot(slot, stack, player);
            }
        });
    }

    @Override
    public void resetToDefaultValidator() {
        ContainerDojutsuSlot.resetToDefaultValidator();
    }

    public static void registerCustomValidator(IDojutsuSlotAPI.ISlotValidator validator) {
        ContainerDojutsuSlot.setSlotValidator(new ContainerDojutsuSlot.ISlotValidator() {
            @Override
            public boolean isItemValidForSlot(int slot, ItemStack stack, EntityPlayer player) {
                return validator.isItemValidForSlot(slot, stack, player);
            }
        });
    }

    /**
     * Creates a validator based on the class name.
     * @param className Class name (partial matching supported)
     * @return Validator
     */
    public static IDojutsuSlotAPI.ISlotValidator createClassNameValidator(String className) {
        return (slot, stack, player) -> {
            return stack.getItem().getClass().getName().contains(className);
        };
    }

    /**
     * Creates a validator based on the class instance.
     * @param clazz Class
     * @return Validator
     */
    public static IDojutsuSlotAPI.ISlotValidator createClassValidator(Class<?> clazz) {
        return (slot, stack, player) -> {
            return clazz.isAssignableFrom(stack.getItem().getClass());
        };
    }

    public static DojutsuHelmetRenderAPI getHelmetRenderControl() {
        return DojutsuHelmetRenderAPI.getInstance();
    }

    /**
     * Convenience method for quickly setting the helmet slot render mode.
     * @param itemId Item ID (format: "modid:itemname")
     * @param mode Render mode: 0 = default, 1 = left eye texture, 2 = right eye texture, 3 = auto
     */
    public static void setHelmetRenderMode(String itemId, int mode) {
        getHelmetRenderControl().setHelmetRenderMode(itemId, DojutsuHelmetRenderAPI.RenderMode.fromValue(mode));
    }

    /**
     * Returns the ItemStack in the given dojutsu slot for the player.
     * <p>
     * Slot layout:
     * <ul>
     *     <li>0 = left eye</li>
     *     <li>1 = right eye</li>
     *     <li>2 = left tomoe rinnegan</li>
     *     <li>3 = right tomoe rinnegan</li>
     * </ul>
     *
     * @param player the player
     * @param slot   the dojutsu slot index
     * @return the ItemStack in the slot, or ItemStack.EMPTY if empty
     */
    public static ItemStack getEye(EntityPlayer player, int slot)
    {
        return DojutsuSlot.invMan.getDojutsuSlotInventory(player.getUniqueID())
                .getStackInSlot(slot);
    }

    /**
     * Sets the ItemStack in the given dojutsu slot for the player.
     * <p>
     * Call this on the server side only. Changes are synced to clients automatically.
     *
     * @param player the player
     * @param slot   the dojutsu slot index (0 = left, 1 = right, 2/3 = extended)
     * @param stack  the stack to place; null is treated as empty
     */
    public static void setEye(EntityPlayer player, int slot, ItemStack stack)
    {
        DojutsuSlot.invMan.getDojutsuSlotInventory(player.getUniqueID())
                .setInventorySlotContents(slot, stack == null ? ItemStack.EMPTY : stack);
    }

    /**
     * Clears the given dojutsu slot for the player.
     * <p>
     * Useful for stealing or removing eyes.
     * Call this on the server side only.
     *
     * @param player the player
     * @param slot   the dojutsu slot index
     */
    public static void removeEye(EntityPlayer player, int slot)
    {
        setEye(player, slot, ItemStack.EMPTY);
    }

    /**
     * Returns whether the given ItemStack is a dojutsu item.
     *
     * @param stack the ItemStack to check
     * @return true if the stack is a dojutsu item
     */
    public static boolean isDojutsuItem(ItemStack stack)
    {
        return DojutsuSlotHelper.isDojutsuItem(stack);
    }

    /**
     * Iterates over all dojutsu-related slots for the player.
     * <p>
     * The helmet slot is passed as index -1.
     *
     * @param player the player
     * @param action the action to run for each slot
     */
    public static void forEachEyeSlot(EntityPlayer player,
                                      java.util.function.BiConsumer<Integer, ItemStack> action)
    {
        DojutsuSlotHelper.forEachDojutsuSlot(player, action);
    }
}