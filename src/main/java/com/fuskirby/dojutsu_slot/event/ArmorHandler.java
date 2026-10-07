package com.fuskirby.dojutsu_slot.event;

import com.fuskirby.dojutsu_slot.DojutsuSlot;
import com.fuskirby.dojutsu_slot.enums.WorldMode;
import com.fuskirby.dojutsu_slot.inventory.InventoryDojutsuSlot;
import com.fuskirby.dojutsu_slot.util.WorldModeHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.entity.living.LivingEquipmentChangeEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.narutomod.item.ItemDojutsu;

public class ArmorHandler {

    @SubscribeEvent
    public void onEquipmentChange(LivingEquipmentChangeEvent event) {
        if (!(event.getEntityLiving() instanceof EntityPlayer)) return;
        if (event.getSlot() != EntityEquipmentSlot.HEAD) return;

        EntityPlayer player = (EntityPlayer) event.getEntityLiving();
        if (player.world.isRemote) return;

        InventoryDojutsuSlot inv = DojutsuSlot.invMan.getDojutsuSlotInventory(player.getUniqueID());

        if ((event.getTo() == inv.getStackInSlot(2)) ||(event.getTo() == inv.getStackInSlot(3))) return;

        WorldMode mode = WorldModeHelper.getMode(player.world);
        if (mode != WorldMode.DOJUTSU) return;

        if ((event.getTo() == inv.getStackInSlot(0)) || (event.getTo() == inv.getStackInSlot(1))) {
            Item rinnegan_tomoe = ForgeRegistries.ITEMS.getValue(new ResourceLocation("dojutsu_addon", "rinnegan_tomoe_helmet"));
            if (!(event.getTo().getItem() == rinnegan_tomoe)) {
                return;
            }
        }

        ItemStack newStack = event.getTo();
        if (newStack.isEmpty()) return;

        if (newStack.getItem() instanceof ItemDojutsu.Base) {
//            ItemStack originalStack = event.getFrom();
//            player.setItemStackToSlot(EntityEquipmentSlot.HEAD, originalStack);
            if (!newStack.isEmpty()) {
                if (!player.inventory.addItemStackToInventory(newStack)) {
                    player.dropItem(newStack, false);
                }
            }
        }
    }
}