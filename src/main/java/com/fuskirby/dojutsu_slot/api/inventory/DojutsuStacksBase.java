package com.fuskirby.dojutsu_slot.api.inventory;

import javax.annotation.Nonnull;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.NonNullList;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemHandlerHelper;

/**
 * This is the actual inventory associated with the player. <br>
 * Changes made to server side CAStacks will be sync to the clients. <br>
 * Do not make changes to client side CAStacks, it is not expected, and can cause problems. <br>
 * <br>
 * This class implements {@link IItemHandler}, {@link IItemHandlerModifiable}, {@link INBTSerializable}. <br>
 * <br>
 * DojutsuSlot uses 2 slots. <br>
 * Slot 0-3 are {@link net.minecraft.inventory.EntityEquipmentSlot#FEET FEET}, {@link net.minecraft.inventory.EntityEquipmentSlot#LEGS LEGS}, {@link net.minecraft.inventory.EntityEquipmentSlot#CHEST CHEST}, {@link net.minecraft.inventory.EntityEquipmentSlot#HEAD HEAD}. <br>
 */
public class DojutsuStacksBase implements IItemHandler, IItemHandlerModifiable, INBTSerializable<NBTTagCompound>
{

    protected NonNullList<ItemStack> stacks;

    public DojutsuStacksBase()
    {
        this(2);
    }

    public DojutsuStacksBase(int size)
    {
        setSize(size);
    }

    @Override
    public void deserializeNBT(NBTTagCompound nbt)
    {
        setSize(nbt.hasKey("DojutsuSlot.Inventory.Size", Constants.NBT.TAG_INT) ? nbt.getInteger("DojutsuSlot.Inventory.Size") : stacks.size());
        NBTTagList tagList = nbt.getTagList("DojutsuSlot.Inventory", Constants.NBT.TAG_COMPOUND);
        for (int i = 0; i < tagList.tagCount(); i++)
        {
            NBTTagCompound itemTags = tagList.getCompoundTagAt(i);
            int slot = itemTags.getByte("Slot") & 255;

            if (slot >= 0 && slot < stacks.size())
            {
                stacks.set(slot, new ItemStack(itemTags));
            }
        }
        onLoad();
    }

    @Nonnull
    @Override
    public ItemStack extractItem(int slot, int amount, boolean simulate)
    {
        if (amount == 0)
            return ItemStack.EMPTY;

        validateSlotIndex(slot);

        ItemStack existing = this.stacks.get(slot);

        if (existing.isEmpty())
            return ItemStack.EMPTY;

        int toExtract = Math.min(amount, existing.getMaxStackSize());

        if (existing.getCount() <= toExtract)
        {
            if (!simulate)
            {
                this.stacks.set(slot, ItemStack.EMPTY);
                onContentsChanged(slot);
            }
            return existing;
        }
        else
        {
            if (!simulate)
            {
                this.stacks.set(slot, ItemHandlerHelper.copyStackWithSize(existing, existing.getCount() - toExtract));
                onContentsChanged(slot);
            }

            return ItemHandlerHelper.copyStackWithSize(existing, toExtract);
        }
    }

    @Override
    public int getSlotLimit(int slot)
    {
        return 64;
    }

    @Override
    public int getSlots()
    {
        return stacks.size();
    }

    @Nonnull
    @Override
    public ItemStack getStackInSlot(int slot)
    {
        validateSlotIndex(slot);
        return this.stacks.get(slot);
    }

    protected int getStackLimit(int slot, @Nonnull ItemStack stack)
    {
        return Math.min(getSlotLimit(slot), stack.getMaxStackSize());
    }

    @Nonnull
    @Override
    public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate)
    {
        if (stack.isEmpty())
            return ItemStack.EMPTY;

        validateSlotIndex(slot);

        ItemStack existing = this.stacks.get(slot);

        int limit = getStackLimit(slot, stack);

        if (!existing.isEmpty())
        {
            if (!ItemHandlerHelper.canItemStacksStack(stack, existing))
                return stack;

            limit -= existing.getCount();
        }

        if (limit <= 0)
            return stack;

        boolean reachedLimit = stack.getCount() > limit;

        if (!simulate)
        {
            if (existing.isEmpty())
            {
                this.stacks.set(slot, reachedLimit ? ItemHandlerHelper.copyStackWithSize(stack, limit) : stack);
            }
            else
            {
                existing.grow(reachedLimit ? limit : stack.getCount());
            }
            onContentsChanged(slot);
        }

        return reachedLimit ? ItemHandlerHelper.copyStackWithSize(stack, stack.getCount() - limit) : ItemStack.EMPTY;
    }

    protected void onContentsChanged(int slot)
    {
    }

    protected void onLoad()
    {
    }

    @Override
    public NBTTagCompound serializeNBT()
    {
        NBTTagList nbtTagList = new NBTTagList();
        for (int i = 0; i < stacks.size(); i++)
        {
            NBTTagCompound itemTag = new NBTTagCompound();
            itemTag.setByte("Slot", (byte) i);
            if (!stacks.get(i).isEmpty())
                stacks.get(i).writeToNBT(itemTag);
            nbtTagList.appendTag(itemTag);
        }
        NBTTagCompound nbt = new NBTTagCompound();
        nbt.setTag("DojutsuSlot.Inventory", nbtTagList);
        nbt.setInteger("DojutsuSlot.Inventory.Size", stacks.size());
        return nbt;
    }

    public void setSize(int size)
    {
        stacks = NonNullList.withSize(size, ItemStack.EMPTY);
    }

    @Override
    public void setStackInSlot(int slot, @Nonnull ItemStack stack)
    {
        validateSlotIndex(slot);
        if (ItemStack.areItemStacksEqual(stacks.get(slot), stack))
            return;
        stacks.set(slot, stack);
        onContentsChanged(slot);
    }

    protected void validateSlotIndex(int slot)
    {
        if (slot < 0 || slot >= stacks.size())
            throw new RuntimeException("Slot " + slot + " not in valid range - [0," + stacks.size() + ")");
    }

    public void expand(int newSize) {
        if (newSize <= stacks.size()) return;
        NonNullList<ItemStack> newStacks = NonNullList.withSize(newSize, ItemStack.EMPTY);
        for (int i = 0; i < stacks.size(); i++) {
            newStacks.set(i, stacks.get(i));
        }
        stacks = newStacks;
        onContentsChanged(-1);
    }

}
