package com.fuskirby.dojutsu_slot.mixin.sharingan;

import com.fuskirby.dojutsu_slot.util.DojutsuSlotHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.narutomod.item.ItemSharingan;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = ItemSharingan.PlayerHook.class, remap = false)
public class MixinItemSharinganPlayerHook {
    @Redirect(method = "onAttacked",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/entity/EntityLivingBase;func_184582_a(Lnet/minecraft/inventory/EntityEquipmentSlot;)Lnet/minecraft/item/ItemStack;"),
            remap = false)
    private static ItemStack redirectGetHelmet(EntityLivingBase instance, EntityEquipmentSlot entityEquipmentSlot) {
        if (entityEquipmentSlot == EntityEquipmentSlot.HEAD) {
            if (instance instanceof EntityPlayer) {
                EntityPlayer player = (EntityPlayer)instance;

                ItemStack helmet = instance.getItemStackFromSlot(entityEquipmentSlot);
                ItemStack left = DojutsuSlotHelper.getLeftDojutsu(player);
                ItemStack right = DojutsuSlotHelper.getRightDojutsu(player);

                if (helmet.getItem() instanceof ItemSharingan.Base) {
                    return helmet;
                }
                if (left.getItem() instanceof ItemSharingan.Base) {
                    return left;
                }
                if (right.getItem() instanceof ItemSharingan.Base) {
                    return right;
                }
            }
        }
        return instance.getItemStackFromSlot(entityEquipmentSlot);
    }
}
