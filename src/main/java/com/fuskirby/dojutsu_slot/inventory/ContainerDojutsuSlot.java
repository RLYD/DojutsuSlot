package com.fuskirby.dojutsu_slot.inventory;

import com.fuskirby.dojutsu_slot.enums.WorldMode;
import com.fuskirby.dojutsu_slot.util.DojutsuSlotHelper;
import com.fuskirby.dojutsu_slot.util.RinneganAttributeHelper;
import com.fuskirby.dojutsu_slot.util.WorldModeHelper;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ContainerPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.narutomod.item.ItemDojutsu;

public class ContainerDojutsuSlot extends ContainerPlayer
{

    private int dojutsuSlotCount;

    public class EnhancedDojutsuSlot extends Slot {
        private final EntityPlayer player;

        public EnhancedDojutsuSlot(IInventory inventory, int index, int x, int y, EntityPlayer player) {
            super(inventory, index, x, y);
            this.player = player;
        }

        @Override
        public void onSlotChanged() {
            super.onSlotChanged();

            if (!player.world.isRemote) {
                RinneganAttributeHelper.updateRinneganHealthBoost(player);
            }

            ItemStack stack = this.getStack();
            if (!stack.isEmpty()) {
                if (stack.getItem() instanceof net.minecraft.item.ItemArmor) {
                    net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(
                            new net.minecraftforge.event.entity.living.LivingEquipmentChangeEvent(
                                    player, net.minecraft.inventory.EntityEquipmentSlot.HEAD,
                                    ItemStack.EMPTY, stack
                            )
                    );
                }

                ContainerDojutsuSlot.ensureDojutsuStateTag(this.getStack(), this.getSlotIndex(), player);
            }
        }

        @Override
        public boolean isItemValid(ItemStack stack) {
            if (!stack.getItem().isValidArmor(stack, net.minecraft.inventory.EntityEquipmentSlot.HEAD, player)) {
                return false;
            }

            return ContainerDojutsuSlot.getCurrentValidator().isItemValidForSlot(
                    this.getSlotIndex(), stack, player
            );
        }
    }

    public interface ISlotValidator {
        boolean isItemValidForSlot(int slot, ItemStack stack, EntityPlayer player);
    }

    private static ISlotValidator defaultValidator = new ISlotValidator() {
        @Override
        public boolean isItemValidForSlot(int slot, ItemStack stack, EntityPlayer player) {
            if (!(stack.getItem() instanceof ItemDojutsu.Base)) {
                if (Loader.isModLoaded("ahznbcursemarkaddon")) {
                    return stack.getItem() instanceof net.mcreator.ahznbcursemarkaddon.item.ItemDojutsu2.Base;
                }
                return false;
            }

            Item rinnegantomoe = ForgeRegistries.ITEMS.getValue(new ResourceLocation("dojutsu_addon", "rinnegan_tomoe_helmet"));

            WorldMode mode = WorldModeHelper.getMode(player.world);
            if (mode == WorldMode.CLASSIC) {
                if (Loader.isModLoaded("dojutsu_addon")) {
                    return stack.getItem() == rinnegantomoe;
                } else {
                    return true;
                }
            }

            String state = DojutsuSlotHelper.getDojutsuState(stack);

            switch (slot) {
                case 0:
                    if (stack.getItem() == rinnegantomoe) return false;
                    return "left".equals(state);
                case 1:
                    if (stack.getItem() == rinnegantomoe) return false;
                    return "right".equals(state);
                case 2:
                    if (stack.getItem() != rinnegantomoe) return false;
                    return state == null || "left".equals(state);
                case 3:
                    if (stack.getItem() != rinnegantomoe) return false;
                    return state == null || "right".equals(state);
                default:
                    return false;
            }
        }
    };

    private static ISlotValidator currentValidator = defaultValidator;

    public static void setSlotValidator(ISlotValidator validator) {
        if (validator != null) {
            currentValidator = validator;
        }
    }

    public static void resetToDefaultValidator() {
        currentValidator = defaultValidator;
    }

    public static ISlotValidator getCurrentValidator() {
        return currentValidator;
    }

    //private static final EntityEquipmentSlot[] VALID_EQUIPMENT_SLOTS = { EntityEquipmentSlot.HEAD, EntityEquipmentSlot.CHEST, EntityEquipmentSlot.LEGS, EntityEquipmentSlot.FEET };
    private static final EntityEquipmentSlot[] VALID_EQUIPMENT_SLOTS = {
            EntityEquipmentSlot.HEAD,
            EntityEquipmentSlot.HEAD
    };

    public ContainerDojutsuSlot(InventoryPlayer invPlayer, InventoryDojutsuSlot invDojutsuSlot, EntityPlayer player) {
        super(invPlayer, !player.world.isRemote, player);

        this.dojutsuSlotCount = getDojutsuSlotCount(player.world);

        for (int i = 0; i < this.dojutsuSlotCount; i++) {
            final int slotIndex = i;
            int xPos = 98 + i * 18;
            int yPos = 62;
            addSlotToContainer(new EnhancedDojutsuSlot(invDojutsuSlot, slotIndex, xPos, yPos, player) {
                @SideOnly(Side.CLIENT)
                @Override
                public String getSlotTexture() {
                    return ItemArmor.EMPTY_SLOT_NAMES[EntityEquipmentSlot.HEAD.getIndex()];
                }
            });
        }
    }

    private static int getDojutsuSlotCount(World world) {
        boolean hasAddon = Loader.isModLoaded("dojutsu_addon");
        if (!hasAddon) return 2;

        WorldMode mode = WorldModeHelper.getMode(world);
        return mode == WorldMode.DOJUTSU ? 4 : 2;
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer player, int slotNumber)
    {
        ItemStack stack = ItemStack.EMPTY;
        Slot slot = (Slot) inventorySlots.get(slotNumber);

        if ((slot != null) && (slot.getHasStack()))
        {
            ItemStack stack1 = slot.getStack();
            stack = stack1.copy();
            EntityEquipmentSlot desiredSlot = EntityLiving.getSlotForItemStack(stack);

            int firstDojutsuSlot = 46;
            int lastDojutsuSlot = 45 + this.dojutsuSlotCount;

            if (slotNumber == 0) // CraftingResult
            {
                if (!mergeItemStack(stack1, 9, 45, true))
                    return ItemStack.EMPTY;

                slot.onSlotChange(stack1, stack);
            }
            else if ((slotNumber >= 1) && (slotNumber < 5)) // CraftingGrid
            {
                if (!mergeItemStack(stack1, 9, 45, false))
                    return ItemStack.EMPTY;
            }
            else if ((slotNumber >= 5) && (slotNumber < 9)) // NormalArmor
            {
                if (!mergeItemStack(stack1, 9, 45, false))
                    return ItemStack.EMPTY;
            }
            else if ((slotNumber >= firstDojutsuSlot) && (slotNumber <= lastDojutsuSlot)) // DojutsuSlot (dynamic quantity)
            {
                if (!mergeItemStack(stack1, 9, 45, false))
                    return ItemStack.EMPTY;
            }
            else if (desiredSlot.getSlotType() == EntityEquipmentSlot.Type.ARMOR && !inventorySlots.get(8 - desiredSlot.getIndex()).getHasStack()) // ItemArmor - check NormalArmor slots
            {
                int j = 8 - desiredSlot.getIndex();

                if (!mergeItemStack(stack1, j, j + 1, false))
                    return ItemStack.EMPTY;
            }
            else if (desiredSlot == EntityEquipmentSlot.HEAD)
            {
                for (int i = lastDojutsuSlot; i >= firstDojutsuSlot; i--) {
                    if (!inventorySlots.get(i).getHasStack()) {
                        if (mergeItemStack(stack1, i, i + 1, false)) {
                            break;
                        }
                    }
                }
            }
            else if (slotNumber < 36) // PlayerInventory
            {
                if (!mergeItemStack(stack1, 36, 45, false))
                    return ItemStack.EMPTY;
            }
            else if (slotNumber < 45) // PlayerHotBar
            {
                if (!mergeItemStack(stack1, 9, 36, false))
                    return ItemStack.EMPTY;
            }
            else if (!mergeItemStack(stack1, 9, 45, false))
            {
                return ItemStack.EMPTY;
            }

            if (stack1.isEmpty())
                slot.putStack(ItemStack.EMPTY);
            else
                slot.onSlotChanged();

            if (stack1.getCount() == stack.getCount())
                return ItemStack.EMPTY;

            ItemStack stack2 = slot.onTake(player, stack1);

            if (slotNumber == 0)
                player.dropItem(stack2, false);
        }

        return stack;
    }

    public int getDojutsuSlotCount() {
        return dojutsuSlotCount;
    }

    private static void ensureDojutsuStateTag(ItemStack stack, int slotIndex, EntityPlayer player) {
        if (stack.isEmpty() || !(stack.getItem() instanceof ItemDojutsu.Base)) {
            if (Loader.isModLoaded("ahznbcursemarkaddon")) {
                if (!(stack.getItem() instanceof net.mcreator.ahznbcursemarkaddon.item.ItemDojutsu2.Base)) return;
            } else {
                return;
            }
        }
        if (slotIndex < 2 || slotIndex > 3) return;
        Item rinnegantomoe = ForgeRegistries.ITEMS.getValue(new ResourceLocation("dojutsu_addon", "rinnegan_tomoe_helmet"));
        if (stack.getItem() != rinnegantomoe) return;

        boolean hasTag = stack.hasTagCompound() && stack.getTagCompound().hasKey("dojutsu_state");
        if (!hasTag) {
            String state = (slotIndex == 2) ? "left" : "right";
            NBTTagCompound nbt = stack.getTagCompound();
            if (nbt == null) {
                nbt = new NBTTagCompound();
                stack.setTagCompound(nbt);
            }
            nbt.setString("dojutsu_state", state);
        }
    }
}
