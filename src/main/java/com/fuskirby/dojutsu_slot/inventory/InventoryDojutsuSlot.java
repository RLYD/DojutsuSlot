package com.fuskirby.dojutsu_slot.inventory;

import com.fuskirby.dojutsu_slot.api.inventory.DojutsuStacksBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;

public class InventoryDojutsuSlot implements IInventory
{

    public InventoryDojutsuSlot() {
        stacks.setSize(4);
    }

    public static final int MinSize = 2; // DojutsuArmorSlots = 2

    DojutsuStacksBase stacks = new DojutsuStacksBase()
    {

        @Override
        protected void onContentsChanged(int slot)
        {
            markDirty();
        }

        @Override
        protected void onLoad()
        {
            markDirty();
        }

        @Override
        public void setSize(int size)
        {
            if (size < MinSize)
                size = MinSize;
            super.setSize(size);
        }

    };
    boolean isDirty = false;

    @Override
    public void clear()
    {
        for (int i = 0; i < stacks.getSlots(); i++)
            stacks.setStackInSlot(i, ItemStack.EMPTY);
    }

    @Override
    public void closeInventory(EntityPlayer player)
    {
    }

    @Override
    public ItemStack decrStackSize(int slot, int num)
    {
        return stacks.extractItem(slot, num, false);
    }

    @Override
    public ITextComponent getDisplayName()
    {
        if (hasCustomName())
        {
            return new TextComponentString(getName());
        }
        return new TextComponentTranslation(getName());
    }

    @Override
    public int getField(int arg0)
    {
        return 0;
    }

    @Override
    public int getFieldCount()
    {
        return 0;
    }

    @Override
    public int getInventoryStackLimit()
    {
        return 64;
    }

    @Override
    public String getName()
    {
        return "";
    }

    @Override
    public int getSizeInventory()
    {
        return stacks.getSlots();
    }

    @Override
    public ItemStack getStackInSlot(int slot)
    {
        return stacks.getStackInSlot(slot);
    }

    public DojutsuStacksBase getStacks()
    {
        return stacks;
    }

    @Override
    public boolean hasCustomName()
    {
        return false;
    }

    public boolean isDirty()
    {
        return isDirty;
    }

    @Override
    public boolean isEmpty()
    {
        for (int i = 0; i < stacks.getSlots(); i++)
            if (!stacks.getStackInSlot(i).isEmpty())
                return false;
        return true;
    }

    @Override
    public boolean isItemValidForSlot(int slot, ItemStack stack)
    {
        return true;
    }

    @Override
    public boolean isUsableByPlayer(EntityPlayer player)
    {
        return true;
    }

    public void markClean()
    {
        isDirty = false;
    }

    @Override
    public void markDirty()
    {
        isDirty = true;
    }

    @Override
    public void openInventory(EntityPlayer player)
    {
    }

    public void readFromNBT(NBTTagCompound compound)
    {
        stacks.deserializeNBT(compound);
        if (stacks.getSlots() < 4) {
            stacks.expand(4);
        }
    }

    @Override
    public ItemStack removeStackFromSlot(int slot)
    {
        return stacks.extractItem(slot, Integer.MAX_VALUE, false);
    }

    @Override
    public void setField(int arg0, int arg1)
    {
    }

    @Override
    public void setInventorySlotContents(int slot, ItemStack stack)
    {
        stacks.setStackInSlot(slot, stack);
    }

    public void writeToNBT(NBTTagCompound compound)
    {
        compound.merge(stacks.serializeNBT());
    }

}
