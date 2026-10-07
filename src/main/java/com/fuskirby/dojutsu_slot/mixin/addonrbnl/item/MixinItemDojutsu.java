package com.fuskirby.dojutsu_slot.mixin.addonrbnl.item;

import com.fuskirby.dojutsu_slot.DojutsuSlotContext;
import net.addonrbnl.item.ItemDojutsu;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = ItemDojutsu.class, remap = false)
public class MixinItemDojutsu {

    @Unique
    private static EntityPlayer player = null;

    @Inject(method = "wearingAnyDojutsu", at = @At(value = "HEAD"))
    private static void getPlayer(EntityLivingBase entity, CallbackInfoReturnable<Boolean> cir) {
        if (entity instanceof EntityPlayer) {
            player = (EntityPlayer) entity;
        }
    }

    @Redirect(method = "wearingAnyDojutsu",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/entity/EntityLivingBase;func_184582_a(Lnet/minecraft/inventory/EntityEquipmentSlot;)Lnet/minecraft/item/ItemStack;"))
    private static ItemStack wearingAnyDojutsu(EntityLivingBase instance, EntityEquipmentSlot entityEquipmentSlot) {
        if (entityEquipmentSlot == EntityEquipmentSlot.HEAD) {
            if (player != null) {
                return DojutsuSlotContext.getDojutsuFromCurrentSlot(player);
            }
        }
        return instance.getItemStackFromSlot(entityEquipmentSlot);
    }
}
