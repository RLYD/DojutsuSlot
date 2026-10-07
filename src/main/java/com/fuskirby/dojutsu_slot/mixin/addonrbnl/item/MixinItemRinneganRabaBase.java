package com.fuskirby.dojutsu_slot.mixin.addonrbnl.item;

import com.fuskirby.dojutsu_slot.DojutsuSlotContext;
import net.addonrbnl.item.ItemRinneganRaba;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = ItemRinneganRaba.Base.class, remap = false)
public class MixinItemRinneganRabaBase {
    @Redirect(method = "onPlayerTickEventPost",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/entity/player/EntityPlayer;func_184582_a(Lnet/minecraft/inventory/EntityEquipmentSlot;)Lnet/minecraft/item/ItemStack;"))
    private ItemStack onPlayerTickEventPost(EntityPlayer instance, EntityEquipmentSlot entityEquipmentSlot) {
        if (entityEquipmentSlot == EntityEquipmentSlot.HEAD && instance instanceof EntityPlayer) {
            return DojutsuSlotContext.getDojutsuFromCurrentSlot(instance);
        }
        return instance.getItemStackFromSlot(entityEquipmentSlot);
    }
}
