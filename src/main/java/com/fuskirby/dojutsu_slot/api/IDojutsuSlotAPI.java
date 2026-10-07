package com.fuskirby.dojutsu_slot.api;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

/**
 * Dojutsu Slot API interface.
 * Allows other mods to customize slot restrictions.
 */
public interface IDojutsuSlotAPI {

    /**
     * Sets the slot validator.
     * @param validator Custom validator
     */
    void setSlotValidator(ISlotValidator validator);

    /**
     * Resets to the default validator.
     */
    void resetToDefaultValidator();

    /**
     * Gets the slot validator interface.
     */
    interface ISlotValidator {
        /**
         * Checks whether an item can be placed into the specified slot.
         * @param slot Slot index (0-1)
         * @param stack Item stack
         * @param player Player
         * @return Whether the item can be placed
         */
        boolean isItemValidForSlot(int slot, ItemStack stack, EntityPlayer player);
    }

    /**
     * Default validator - only allows items of the ItemDojutsu type.
     */
    class DefaultValidator implements ISlotValidator {
        @Override
        public boolean isItemValidForSlot(int slot, ItemStack stack, EntityPlayer player) {
            try {
                String className = stack.getItem().getClass().getName();
                if (className.contains("ItemDojutsu")) {
                    return true;
                }

                if (className.startsWith("net.narutomod.item.ItemDojutsu")) {
                    return true;
                }

                Class<?>[] interfaces = stack.getItem().getClass().getInterfaces();
                for (Class<?> iface : interfaces) {
                    if (iface.getName().contains("Dojutsu")) {
                        return true;
                    }
                }

                return false;
            } catch (Exception e) {
                return false;
            }
        }
    }

    /**
     * Extended validator - supports more checking methods.
     */
    class ExtendedValidator implements ISlotValidator {
        private final java.util.function.Predicate<ItemStack> itemChecker;

        public ExtendedValidator(java.util.function.Predicate<ItemStack> itemChecker) {
            this.itemChecker = itemChecker;
        }

        @Override
        public boolean isItemValidForSlot(int slot, ItemStack stack, EntityPlayer player) {
            return itemChecker.test(stack);
        }
    }
}