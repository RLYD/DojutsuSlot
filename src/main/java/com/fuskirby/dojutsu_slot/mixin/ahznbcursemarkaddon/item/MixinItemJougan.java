package com.fuskirby.dojutsu_slot.mixin.ahznbcursemarkaddon.item;

import com.fuskirby.dojutsu_slot.util.DojutsuSlotHelper;
import net.mcreator.ahznbcursemarkaddon.item.ItemJougan;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = ItemJougan.class, remap = false)
public class MixinItemJougan {

    @Redirect(method = "getChibaukutenseiChakraUsage", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/EntityLivingBase;func_184582_a(Lnet/minecraft/inventory/EntityEquipmentSlot;)Lnet/minecraft/item/ItemStack;"))
    private static ItemStack getChibaukutenseiChakraUsage(EntityLivingBase entity, EntityEquipmentSlot entityEquipmentSlot) {
        if (entity instanceof EntityPlayer) {
             return DojutsuSlotHelper.selectDojutsuForJutsu((EntityPlayer) entity);
        }
        return entity.getItemStackFromSlot(EntityEquipmentSlot.HEAD);
    }

    @Redirect(method = "getTengaishinseiChakraUsage", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/EntityLivingBase;func_184582_a(Lnet/minecraft/inventory/EntityEquipmentSlot;)Lnet/minecraft/item/ItemStack;"))
    private static ItemStack getTengaishinseiChakraUsage(EntityLivingBase entity, EntityEquipmentSlot entityEquipmentSlot) {
        if (entity instanceof EntityPlayer) {
            return DojutsuSlotHelper.selectDojutsuForJutsu((EntityPlayer) entity);
        }
        return entity.getItemStackFromSlot(EntityEquipmentSlot.HEAD);
    }

    @Redirect(method = "getByakuganChakraUsage", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/EntityLivingBase;func_184582_a(Lnet/minecraft/inventory/EntityEquipmentSlot;)Lnet/minecraft/item/ItemStack;"))
    private static ItemStack getByakuganChakraUsage(EntityLivingBase entity, EntityEquipmentSlot entityEquipmentSlot) {
        if (entity instanceof EntityPlayer) {
            return DojutsuSlotHelper.selectDojutsuForJutsu((EntityPlayer) entity);
        }
        return entity.getItemStackFromSlot(EntityEquipmentSlot.HEAD);
    }

    @Redirect(method = "getKushoChakraUsage", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/EntityLivingBase;func_184582_a(Lnet/minecraft/inventory/EntityEquipmentSlot;)Lnet/minecraft/item/ItemStack;"))
    private static ItemStack getKushoChakraUsage(EntityLivingBase entity, EntityEquipmentSlot entityEquipmentSlot) {
        if (entity instanceof EntityPlayer) {
            return DojutsuSlotHelper.selectDojutsuForJutsu((EntityPlayer) entity);
        }
        return entity.getItemStackFromSlot(EntityEquipmentSlot.HEAD);
    }
}
