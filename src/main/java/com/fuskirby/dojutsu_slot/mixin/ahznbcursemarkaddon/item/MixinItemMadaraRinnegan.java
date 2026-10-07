package com.fuskirby.dojutsu_slot.mixin.ahznbcursemarkaddon.item;

import com.fuskirby.dojutsu_slot.util.DojutsuSlotHelper;
import net.mcreator.ahznbcursemarkaddon.item.ItemMadaraRinnegan;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = ItemMadaraRinnegan.class, remap = false)
public class MixinItemMadaraRinnegan {
    @Redirect(method = "getChibaukutenseiChakraUsage", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/EntityLivingBase;func_184582_a(Lnet/minecraft/inventory/EntityEquipmentSlot;)Lnet/minecraft/item/ItemStack;"))
    private static ItemStack getChibaukutenseiChakraUsage(EntityLivingBase entity, EntityEquipmentSlot slot) {
        if (entity instanceof EntityPlayer) {
            return DojutsuSlotHelper.selectDojutsuForJutsu((EntityPlayer) entity);
        }
        return entity.getItemStackFromSlot(slot);
    }

    @Redirect(method = "getTengaishinseiChakraUsage", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/EntityLivingBase;func_184582_a(Lnet/minecraft/inventory/EntityEquipmentSlot;)Lnet/minecraft/item/ItemStack;"))
    private static ItemStack getTengaishinseiChakraUsage(EntityLivingBase entity, EntityEquipmentSlot slot) {
        if (entity instanceof EntityPlayer) {
            return DojutsuSlotHelper.selectDojutsuForJutsu((EntityPlayer) entity);
        }
        return entity.getItemStackFromSlot(slot);
    }
}
