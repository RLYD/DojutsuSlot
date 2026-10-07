package com.fuskirby.dojutsu_slot.mixin.dojutsu;

import com.fuskirby.dojutsu_slot.util.DojutsuSlotHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.narutomod.item.ItemSharingan;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = ItemSharingan.class, remap = false)
public class MixinItemSharingan {
    @Inject(method = "wearingAny", at = @At("RETURN"), cancellable = true)
    private static void wearingAny(EntityLivingBase entity, CallbackInfoReturnable<Boolean> cir) {
        if (entity instanceof EntityPlayer) {
            if (entity instanceof EntityPlayer) {
                ItemStack dojutsu_left = DojutsuSlotHelper.getLeftDojutsu((EntityPlayer) entity);
                ItemStack dojutsu_right = DojutsuSlotHelper.getRightDojutsu((EntityPlayer) entity);

                boolean isSharingan = dojutsu_left.getItem() instanceof ItemSharingan.Base || dojutsu_right.getItem() instanceof ItemSharingan.Base;

                cir.setReturnValue(isSharingan);
            }
        }
    }

    @Inject(method = "isWearingMangekyo", at = @At("RETURN"), cancellable = true)
    private static void isWearingMangekyo(EntityLivingBase entity, CallbackInfoReturnable<Boolean> cir) {
        if (entity instanceof EntityPlayer) {
            ItemStack helmetstack = entity.getItemStackFromSlot(EntityEquipmentSlot.HEAD);
            ItemStack dojutsu_left = DojutsuSlotHelper.getLeftDojutsu((EntityPlayer) entity);
            ItemStack dojutsu_right = DojutsuSlotHelper.getRightDojutsu((EntityPlayer) entity);

            boolean isWearingMangekyo = (helmetstack.getItem() instanceof ItemSharingan.Base && helmetstack.getItem() != ItemSharingan.helmet)
                    || (dojutsu_left.getItem() instanceof ItemSharingan.Base && helmetstack.getItem() != ItemSharingan.helmet)
                    || (dojutsu_right.getItem() instanceof ItemSharingan.Base && helmetstack.getItem() != ItemSharingan.helmet);
            cir.setReturnValue(isWearingMangekyo);
        }
    }
}
