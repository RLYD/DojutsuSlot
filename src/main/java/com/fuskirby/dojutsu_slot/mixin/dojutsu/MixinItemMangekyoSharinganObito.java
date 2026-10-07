package com.fuskirby.dojutsu_slot.mixin.dojutsu;

import com.fuskirby.dojutsu_slot.util.DojutsuSlotHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.narutomod.item.ItemDojutsu;
import net.narutomod.item.ItemMangekyoSharinganObito;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = ItemMangekyoSharinganObito.class, remap = false)
public class MixinItemMangekyoSharinganObito {

    @Shadow public static final double INTANGIBLE_CHAKRA_USAGE = 2.0F;
    @Shadow public static final double TELEPORT_CHAKRA_USAGE = 20.0F;

    @Inject(method = "getIntangibleChakraUsage", at = @At("RETURN"), cancellable = true)
    private static void getIntangibleChakraUsage(EntityLivingBase entity, CallbackInfoReturnable<Double> cir) {
        if (entity instanceof EntityPlayer) {
            ItemStack stack = DojutsuSlotHelper.selectDojutsuForJutsu((EntityPlayer) entity);
            if (stack.getItem() instanceof ItemDojutsu.Base) {
                ItemDojutsu.Base dojutsu = (ItemDojutsu.Base) stack.getItem();
                if (dojutsu.isOwner(stack, entity)) {
                    cir.setReturnValue(INTANGIBLE_CHAKRA_USAGE);
                } else {
                    cir.setReturnValue(INTANGIBLE_CHAKRA_USAGE * 3);
                }
                return;
            }

            cir.setReturnValue(Double.MAX_VALUE);
            cir.cancel();
        }
    }

    @Inject(method = "getTeleportChakraUsage", at = @At("RETURN"), cancellable = true)
    private static void getTeleportChakraUsage(EntityLivingBase entity, CallbackInfoReturnable<Double> cir) {
        if (entity instanceof EntityPlayer) {
            ItemStack stack = DojutsuSlotHelper.selectDojutsuForJutsu((EntityPlayer) entity);
            if (stack.getItem() instanceof ItemDojutsu.Base) {
                ItemDojutsu.Base dojutsu = (ItemDojutsu.Base) stack.getItem();
                if (dojutsu.isOwner(stack, entity)) {
                    cir.setReturnValue(TELEPORT_CHAKRA_USAGE);
                } else {
                    cir.setReturnValue(TELEPORT_CHAKRA_USAGE * 3);
                }
                return;
            }
            cir.setReturnValue(Double.MAX_VALUE);
            cir.cancel();
        }
    }

//    @Redirect(method = "getIntangibleChakraUsage", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/EntityLivingBase;func_184582_a(Lnet/minecraft/inventory/EntityEquipmentSlot;)Lnet/minecraft/item/ItemStack;"))
//    private static ItemStack getIntangibleChakraUsage(EntityLivingBase instance, EntityEquipmentSlot entityEquipmentSlot) {
//        return DojutsuSlotHelper.selectDojutsuForJutsu((EntityPlayer) instance);
//    }
//
//    @Redirect(method = "getTeleportChakraUsage", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/EntityLivingBase;func_184582_a(Lnet/minecraft/inventory/EntityEquipmentSlot;)Lnet/minecraft/item/ItemStack;"))
//    private static ItemStack getTeleportChakraUsage(EntityLivingBase instance, EntityEquipmentSlot entityEquipmentSlot) {
//        return DojutsuSlotHelper.selectDojutsuForJutsu((EntityPlayer) instance);
//    }
}
