package com.fuskirby.dojutsu_slot.util;

import com.fuskirby.dojutsu_slot.DojutsuSlot;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.narutomod.item.ItemRinnegan;
import net.narutomod.item.ItemTenseigan;

import java.util.UUID;

public class RinneganAttributeHelper {
    public static final UUID RINNESHARINGAN_MODIFIER = UUID.fromString("135da083-a632-483e-85bd-2281f15ca7e0");
    private static final double HEALTH_BOOST = 380d;

    public static void updateRinneganHealthBoost(EntityPlayer player) {
        if (player.world.isRemote) return;

        IAttributeInstance maxHealthAttr = player.getAttributeMap().getAttributeInstance(SharedMonsterAttributes.MAX_HEALTH);

        AttributeModifier oldMod = maxHealthAttr.getModifier(RINNESHARINGAN_MODIFIER);
        if (oldMod != null) {
            maxHealthAttr.removeModifier(oldMod);
        }

        if (shouldApplyBoost(player)) {
            AttributeModifier newMod = new AttributeModifier(RINNESHARINGAN_MODIFIER, "rinnesharingan.maxhealth", HEALTH_BOOST, 0);
            maxHealthAttr.applyModifier(newMod);
        }
    }

    private static boolean shouldApplyBoost(EntityPlayer player) {
        IInventory dojutsuSlot = (IInventory) DojutsuSlot.invMan.getDojutsuSlotInventory(player.getUniqueID());
        ItemStack slot0 = dojutsuSlot.getStackInSlot(0);
        ItemStack slot1 = dojutsuSlot.getStackInSlot(1);
        ItemStack slot2 = dojutsuSlot.getStackInSlot(2);
        ItemStack slot3 = dojutsuSlot.getStackInSlot(3);

        return (isRinneganAndActivated(slot0) || isRinneganAndActivated(slot1)
                || isRinneganAndActivated(slot2) || isRinneganAndActivated(slot3));
    }

    private static boolean isRinneganAndActivated(ItemStack stack) {
        Item rinnegantomoe = ForgeRegistries.ITEMS.getValue(new ResourceLocation("dojutsu_addon", "rinnegan_tomoe_helmet"));
        return ((stack.getItem() == ItemRinnegan.helmet || stack.getItem() == rinnegantomoe) || stack.getItem() == ItemTenseigan.helmet) && ItemRinnegan.isRinnesharinganActivated(stack);
    }
}