package com.fuskirby.dojutsu_slot.mixin.addonrbnl.item;

import com.fuskirby.dojutsu_slot.util.DojutsuSlotHelper;
import net.addonrbnl.item.ItemMangekyoSharinganItachi;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = ItemMangekyoSharinganItachi.class, remap = false)
public class MixinItemMangekyoSharinganItachi {
    @Redirect(method = "getGenjutsudodgeCROWChakraUsage",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/entity/EntityLivingBase;func_184582_a(Lnet/minecraft/inventory/EntityEquipmentSlot;)Lnet/minecraft/item/ItemStack;"))
    private static ItemStack getGenjutsudodgeCROWChakraUsage(EntityLivingBase instance, EntityEquipmentSlot entityEquipmentSlot) {
        if (entityEquipmentSlot == EntityEquipmentSlot.HEAD) {
            if (instance instanceof EntityPlayer) {
                EntityPlayer player = (EntityPlayer) instance;
                return DojutsuSlotHelper.selectDojutsuForJutsu(player);
            }
        }
        return instance.getItemStackFromSlot(entityEquipmentSlot);
    }

    @Redirect(method = "getTsukoyomiChakraUsage",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/entity/EntityLivingBase;func_184582_a(Lnet/minecraft/inventory/EntityEquipmentSlot;)Lnet/minecraft/item/ItemStack;"))
    private static ItemStack getTsukoyomiChakraUsage(EntityLivingBase instance, EntityEquipmentSlot entityEquipmentSlot) {
        if (entityEquipmentSlot == EntityEquipmentSlot.HEAD) {
            if (instance instanceof EntityPlayer) {
                EntityPlayer player = (EntityPlayer) instance;
                return DojutsuSlotHelper.selectDojutsuForJutsu(player);
            }
        }
        return instance.getItemStackFromSlot(entityEquipmentSlot);
    }
}
