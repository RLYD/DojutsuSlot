package com.fuskirby.dojutsu_slot.mixin.addonrbnl.item;

import com.fuskirby.dojutsu_slot.util.DojutsuSlotHelper;
import net.addonrbnl.item.ItemMangekyoSharinganSarada;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = ItemMangekyoSharinganSarada.class, remap = false)
public class MixinItemMangekyoSharinganSarada {
    @Redirect(method = "getGenjutsuFieldChakraUsage",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/entity/EntityLivingBase;func_184582_a(Lnet/minecraft/inventory/EntityEquipmentSlot;)Lnet/minecraft/item/ItemStack;"))
    private static ItemStack getGenjutsuFieldChakraUsage(EntityLivingBase instance, EntityEquipmentSlot entityEquipmentSlot) {
        if (entityEquipmentSlot == EntityEquipmentSlot.HEAD) {
            if (instance instanceof EntityPlayer) {
                EntityPlayer player = (EntityPlayer) instance;
                return DojutsuSlotHelper.selectDojutsuForJutsu(player);
            }
        }
        return instance.getItemStackFromSlot(entityEquipmentSlot);
    }
}
