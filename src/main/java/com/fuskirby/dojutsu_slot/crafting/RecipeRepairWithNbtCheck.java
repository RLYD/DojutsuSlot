package com.fuskirby.dojutsu_slot.crafting;

import com.google.common.collect.Lists;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import net.minecraftforge.registries.IForgeRegistryEntry;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

public class RecipeRepairWithNbtCheck extends IForgeRegistryEntry.Impl<IRecipe> implements IRecipe {

    private final Item targetItem;
    private final String forbiddenNbtKey;

    public RecipeRepairWithNbtCheck(Item targetItem, String forbiddenNbtKey) {
        this.targetItem = targetItem;
        this.forbiddenNbtKey = forbiddenNbtKey;
    }

    @Override
    @ParametersAreNonnullByDefault
    public boolean matches(InventoryCrafting inv, World worldIn) {
        List<ItemStack> list = Lists.newArrayList();

        for (int i = 0; i < inv.getSizeInventory(); ++i) {
            ItemStack stack = inv.getStackInSlot(i);
            if (!stack.isEmpty()) {
                if (stack.getItem() != targetItem) {
                    return false;
                }
                if (stack.getCount() != 1) {
                    return false;
                }
                NBTTagCompound tag = stack.getTagCompound();
                if (tag != null && tag.hasKey(forbiddenNbtKey)) {
                    return false;
                }
                list.add(stack);
            }
        }

        return list.size() == 2;
    }

    @Override
    @MethodsReturnNonnullByDefault
    public ItemStack getCraftingResult(InventoryCrafting inv) {
        List<ItemStack> list = Lists.newArrayList();

        for (int i = 0; i < inv.getSizeInventory(); ++i) {
            ItemStack stack = inv.getStackInSlot(i);
            if (!stack.isEmpty()) {
                if (stack.getItem() != targetItem || stack.getCount() != 1) {
                    return ItemStack.EMPTY;
                }
                NBTTagCompound tag = stack.getTagCompound();
                if (tag != null && tag.hasKey(forbiddenNbtKey)) {
                    return ItemStack.EMPTY;
                }
                list.add(stack);
            }
        }

        if (list.size() == 2) {
            ItemStack stack1 = list.get(0);
            ItemStack stack2 = list.get(1);
            if (stack1.getItem() == stack2.getItem()) {
                int maxDamage = stack1.getMaxDamage();
                int remaining1 = maxDamage - stack1.getItemDamage();
                int remaining2 = maxDamage - stack2.getItemDamage();
                int totalRemaining = remaining1 + remaining2 + maxDamage * 5 / 100;
                int newDamage = maxDamage - totalRemaining;
                if (newDamage < 0) {
                    newDamage = 0;
                }
                return new ItemStack(targetItem, 1, newDamage);
            }
        }
        return ItemStack.EMPTY;
    }

    @Override
    @MethodsReturnNonnullByDefault
    public ItemStack getRecipeOutput() {
        return ItemStack.EMPTY;
    }

    @Override
    @MethodsReturnNonnullByDefault
    public NonNullList<ItemStack> getRemainingItems(InventoryCrafting inv) {
        NonNullList<ItemStack> remaining = NonNullList.withSize(inv.getSizeInventory(), ItemStack.EMPTY);
        for (int i = 0; i < remaining.size(); ++i) {
            ItemStack stack = inv.getStackInSlot(i);
            remaining.set(i, net.minecraftforge.common.ForgeHooks.getContainerItem(stack));
        }
        return remaining;
    }

    @Override
    public boolean isDynamic() {
        return true;
    }

    @Override
    public boolean canFit(int width, int height) {
        return width * height >= 2;
    }
}