package com.fuskirby.dojutsu_slot.mixin.sharingan;

import com.fuskirby.dojutsu_slot.util.DojutsuSlotHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.narutomod.entity.EntitySusanooWinged;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = EntitySusanooWinged.EntityCustom.class, remap = false)
public class MixinEntitySusanooWingedEntityCustom {
    @Redirect(method = "<init>(Lnet/minecraft/entity/player/EntityPlayer;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/EntityPlayer;func_184582_a(Lnet/minecraft/inventory/EntityEquipmentSlot;)Lnet/minecraft/item/ItemStack;"))
    private static ItemStack redirectEntityCustom(EntityPlayer instance, EntityEquipmentSlot entityEquipmentSlot) {
        if (entityEquipmentSlot == EntityEquipmentSlot.HEAD) {
            return DojutsuSlotHelper.selectDojutsuForJutsu(instance);
        }
        return instance.getItemStackFromSlot(entityEquipmentSlot);
    }
}
