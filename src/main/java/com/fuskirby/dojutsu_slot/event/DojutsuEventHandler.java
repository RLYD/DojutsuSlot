package com.fuskirby.dojutsu_slot.event;

import com.fuskirby.dojutsu_slot.api.mangekyo.MangekyoPoolAPI;
import com.fuskirby.dojutsu_slot.data.ModWorldData;
import com.fuskirby.dojutsu_slot.enums.WorldMode;
import com.fuskirby.dojutsu_slot.util.DojutsuSlotHelper;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.narutomod.item.ItemDojutsu;
import net.narutomod.item.ItemSharingan;
import org.spongepowered.asm.mixin.Unique;

import java.util.Objects;

public class DojutsuEventHandler {

    @Unique
    private final java.util.Map<net.minecraft.entity.player.EntityPlayerMP, ItemStack[]> inventoryCache = new java.util.HashMap<>();

    @Unique
    private boolean isProcessing = false;

    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END || event.side == Side.CLIENT) return;

        EntityPlayerMP player = (EntityPlayerMP) event.player;
        if (player == null) return;

        WorldMode mode = Objects.requireNonNull(ModWorldData.get(player.getEntityWorld())).getMode();
        if (mode != WorldMode.DOJUTSU) return;

        if (!isInventoryChanged(player)) return;

        processPlayerInventory(player);
    }

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public void onItemTooltip(ItemTooltipEvent event) {
        ItemStack stack = event.getItemStack();
        if (stack.isEmpty()) return;

        if (Loader.isModLoaded("ahznbcursemarkaddon")) {
            if (!(stack.getItem() instanceof net.mcreator.ahznbcursemarkaddon.item.ItemDojutsu2.Base) && !(stack.getItem() instanceof ItemDojutsu.Base)) return;
        } else if (!(stack.getItem() instanceof ItemDojutsu.Base)) return;

        NBTTagCompound tag = stack.getTagCompound();
        if (tag != null && tag.hasKey("dojutsu_state")) {
            String state = tag.getString("dojutsu_state");
            String stateName = I18n.format("dojutsu.state." + state);
            String tooltipLine = I18n.format("dojutsu.state.format", stateName);
            event.getToolTip().add(TextFormatting.GRAY + tooltipLine);
        }
    }

    @Unique
    private void processPlayerInventory(EntityPlayerMP player) {
        if (isProcessing) return;
        isProcessing = true;

        try {
            InventoryPlayer inv = player.inventory;
            for (int slot = 0; slot < inv.getSizeInventory(); slot++) {
                ItemStack stack = inv.getStackInSlot(slot);
                if (stack.isEmpty()) continue;

                if (Loader.isModLoaded("ahznbcursemarkaddon")) {
                    if (!(stack.getItem() instanceof net.mcreator.ahznbcursemarkaddon.item.ItemDojutsu2.Base) && !(stack.getItem() instanceof ItemDojutsu.Base)) continue;
                } else if (!(stack.getItem() instanceof ItemDojutsu.Base)) continue;

                Item rinnegantomoe = ForgeRegistries.ITEMS.getValue(new ResourceLocation("dojutsu_addon", "rinnegan_tomoe_helmet"));
                if (stack.getItem() == rinnegantomoe) continue;

                NBTTagCompound tag = stack.getTagCompound();
                if (tag != null && tag.hasKey("dojutsu_state")) continue;

                if (stack.getItem() instanceof ItemSharingan.Base) {
                    setMangekyoItem(stack);
                }

                ItemStack left = stack.copy();
                DojutsuSlotHelper.setDojutsuState(left, "left");
                ItemStack right = stack.copy();
                DojutsuSlotHelper.setDojutsuState(right, "right");

                inv.setInventorySlotContents(slot, ItemStack.EMPTY);

                if (!player.addItemStackToInventory(left)) {
                    player.entityDropItem(left, 0.5F);
                }
                if (!player.addItemStackToInventory(right)) {
                    player.entityDropItem(right, 0.5F);
                }

                player.inventory.markDirty();
                player.sendAllContents(player.inventoryContainer, player.inventoryContainer.getInventory());
            }
        } finally {
            isProcessing = false;
        }
    }

    @Unique
    private void setMangekyoItem(ItemStack stack) {
        NBTTagCompound tag = stack.getTagCompound();
        if (tag == null) {
            tag = new NBTTagCompound();
        }
        if (!tag.hasKey("mangekyo_item_id")) {
            tag.setString("mangekyo_item_id", MangekyoPoolAPI.getRandomMangekyoId());
        }
        stack.setTagCompound(tag);
    }

    @Unique
    private boolean isInventoryChanged(EntityPlayerMP player) {
        ItemStack[] cached = inventoryCache.get(player);

        if (cached == null || cached.length != player.inventory.getSizeInventory()) {
            inventoryCache.put(player, snapshot(player));
            return true;
        }

        for (int i = 0; i < cached.length; i++) {
            ItemStack current = player.inventory.getStackInSlot(i);
            if (!ItemStack.areItemStacksEqual(current, cached[i])) {
                inventoryCache.put(player, snapshot(player));
                return true;
            }
        }

        return false;
    }

    @Unique
    private ItemStack[] snapshot(EntityPlayerMP player) {
        int size = player.inventory.getSizeInventory();
        ItemStack[] arr = new ItemStack[size];
        for (int i = 0; i < size; i++) {
            arr[i] = player.inventory.getStackInSlot(i).copy();
        }
        return arr;
    }
}