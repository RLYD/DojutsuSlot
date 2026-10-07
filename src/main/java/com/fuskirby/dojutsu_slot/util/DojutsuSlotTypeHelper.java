package com.fuskirby.dojutsu_slot.util;

import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class DojutsuSlotTypeHelper {

    public enum DojutsuSlotType {
        VANILLA_HELMET(0, "dojutsu.slot.vanilla_helmet", EntityEquipmentSlot.HEAD),
        LEFT_DOJUTSU(1, "dojutsu.slot.left_dojutsu", null),
        RIGHT_DOJUTSU(2, "dojutsu.slot.right_dojutsu", null);

        private final int id;
        private final String translationKey;
        private final EntityEquipmentSlot vanillaSlot;

        DojutsuSlotType(int id, String translationKey, EntityEquipmentSlot vanillaSlot) {
            this.id = id;
            this.translationKey = translationKey;
            this.vanillaSlot = vanillaSlot;
        }

        public int getId() {
            return id;
        }

        public String getTranslationKey() {
            return translationKey;
        }

        public EntityEquipmentSlot getVanillaSlot() {
            return vanillaSlot;
        }

        public boolean isVanillaSlot() {
            return vanillaSlot != null;
        }

        public boolean isCustomSlot() {
            return vanillaSlot == null;
        }

        public static DojutsuSlotType fromId(int id) {
            for (DojutsuSlotType type : values()) {
                if (type.id == id) {
                    return type;
                }
            }
            return VANILLA_HELMET;
        }
    }

    private static final ThreadLocal<DojutsuSlotType> CURRENT_PROCESSING_SLOT = new ThreadLocal<>();

    public static void setProcessingSlot(DojutsuSlotType slotType) {
        CURRENT_PROCESSING_SLOT.set(slotType);
    }

    public static DojutsuSlotType getProcessingSlot() {
        DojutsuSlotType slot = CURRENT_PROCESSING_SLOT.get();
        return slot != null ? slot : DojutsuSlotType.VANILLA_HELMET;
    }

    public static void clearProcessingSlot() {
        CURRENT_PROCESSING_SLOT.remove();
    }

    public static boolean isHelmetRelatedSlot() {
        DojutsuSlotType current = getProcessingSlot();
        return current == DojutsuSlotType.VANILLA_HELMET ||
                current == DojutsuSlotType.LEFT_DOJUTSU ||
                current == DojutsuSlotType.RIGHT_DOJUTSU;
    }

    public static boolean isVanillaHelmetSlot() {
        return getProcessingSlot() == DojutsuSlotType.VANILLA_HELMET;
    }

    public static boolean isLeftDojutsuSlot() {
        return getProcessingSlot() == DojutsuSlotType.LEFT_DOJUTSU;
    }

    public static boolean isRightDojutsuSlot() {
        return getProcessingSlot() == DojutsuSlotType.RIGHT_DOJUTSU;
    }

    /**
     * Automatically detects and sets the slot type.
     * @param slot The vanilla slot
     * @param stack The item stack
     * @param player The player (may be null)
     */
    @SideOnly(Side.CLIENT)
    public static void autoDetectAndSetSlot(EntityEquipmentSlot slot, net.minecraft.item.ItemStack stack, net.minecraft.entity.player.EntityPlayer player) {
        if (slot == EntityEquipmentSlot.HEAD) {
            if (player != null) {
                net.minecraft.item.ItemStack helmet = player.inventory.armorInventory.get(3);
                if (net.minecraft.item.ItemStack.areItemStacksEqual(stack, helmet)) {
                    setProcessingSlot(DojutsuSlotType.VANILLA_HELMET);
                    return;
                }
            }
            setProcessingSlot(DojutsuSlotType.VANILLA_HELMET);
        }
    }

    public static Runnable withSlotContext(DojutsuSlotType slotType, Runnable task) {
        return () -> {
            try {
                setProcessingSlot(slotType);
                task.run();
            } finally {
                clearProcessingSlot();
            }
        };
    }
}