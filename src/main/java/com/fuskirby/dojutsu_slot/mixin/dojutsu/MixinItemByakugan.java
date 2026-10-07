package com.fuskirby.dojutsu_slot.mixin.dojutsu;

import com.fuskirby.dojutsu_slot.DojutsuSlot;
import com.fuskirby.dojutsu_slot.util.DojutsuSlotHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.narutomod.ElementsNarutomodMod;
import net.narutomod.item.ItemByakugan;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static net.narutomod.item.ItemByakugan.helmet;
import static net.narutomod.item.ItemByakugan.isRinnesharinganActivated;

@Mixin(value = ItemByakugan.class, remap = false)
public abstract class MixinItemByakugan extends ElementsNarutomodMod.ModElement {

//    @Shadow
//    private static final double BYAKUGAN_CHAKRA_USAGE = 10.0F;

    public MixinItemByakugan(ElementsNarutomodMod elements, int sortid) {
        super(elements, sortid);
    }

//    @Inject(method = "getByakuganChakraUsage", at = @At("RETURN"), cancellable = true)
//    private static void getByakuganChakraUsage(EntityLivingBase entity, CallbackInfoReturnable<Double> cir) {
//        ItemStack stack = DojutsuSlotContext.getDojutsuFromCurrentSlot(entity);
//        ItemStack dojutsu_left = DojutsuSlotHelper.getLeftDojutsu((EntityPlayer) entity);
//        ItemStack dojutsu_right = DojutsuSlotHelper.getRightDojutsu((EntityPlayer) entity);
//
//        boolean isByakugan = stack.getItem() == ItemByakugan.helmet || dojutsu_left.getItem() == ItemByakugan.helmet || dojutsu_right.getItem() == ItemByakugan.helmet;
//
//        if (isByakugan) {
//            boolean isOwner = ((ItemDojutsu.Base) stack.getItem()).isOwner(stack, entity);
//            double usage = isOwner ? BYAKUGAN_CHAKRA_USAGE : BYAKUGAN_CHAKRA_USAGE * 2;
//            cir.setReturnValue(usage);
//            cir.cancel();
//        } else {
//            cir.setReturnValue(Double.MAX_VALUE * 0.001d);
//            cir.cancel();
//        }
//    }

    @Redirect(method = "getByakuganChakraUsage", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/EntityLivingBase;func_184582_a(Lnet/minecraft/inventory/EntityEquipmentSlot;)Lnet/minecraft/item/ItemStack;"))
    private static ItemStack getByakuganChakraUsage(EntityLivingBase instance, EntityEquipmentSlot entityEquipmentSlot) {
        if (instance instanceof EntityPlayer) {
            return DojutsuSlotHelper.selectDojutsuForJutsu((EntityPlayer) instance);
        }
        return instance.getItemStackFromSlot(entityEquipmentSlot);
    }

    @Redirect(method = "getRokujuyonshoChakraUsage", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/EntityLivingBase;func_184582_a(Lnet/minecraft/inventory/EntityEquipmentSlot;)Lnet/minecraft/item/ItemStack;"))
    private static ItemStack getRokujuyonshoChakraUsage(EntityLivingBase instance, EntityEquipmentSlot entityEquipmentSlot) {
        if (instance instanceof EntityPlayer) {
            return DojutsuSlotHelper.selectDojutsuForJutsu((EntityPlayer) instance);
        }
        return instance.getItemStackFromSlot(entityEquipmentSlot);
    }

    @Redirect(method = "getKaitenChakraUsage", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/EntityLivingBase;func_184582_a(Lnet/minecraft/inventory/EntityEquipmentSlot;)Lnet/minecraft/item/ItemStack;"))
    private static ItemStack getKaitenChakraUsage(EntityLivingBase instance, EntityEquipmentSlot entityEquipmentSlot) {
        if (instance instanceof EntityPlayer) {
            return DojutsuSlotHelper.selectDojutsuForJutsu((EntityPlayer) instance);
        }
        return instance.getItemStackFromSlot(entityEquipmentSlot);
    }

    @Redirect(method = "getKushoChakraUsage", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/EntityLivingBase;func_184582_a(Lnet/minecraft/inventory/EntityEquipmentSlot;)Lnet/minecraft/item/ItemStack;"))
    private static ItemStack getKushoChakraUsage(EntityLivingBase instance, EntityEquipmentSlot entityEquipmentSlot) {
        if (instance instanceof EntityPlayer) {
            return DojutsuSlotHelper.selectDojutsuForJutsu((EntityPlayer) instance);
        }
        return instance.getItemStackFromSlot(entityEquipmentSlot);
    }

    @Inject(method = "wearingRinnesharingan", at = @At("RETURN"), cancellable = true)
    private static void wearingRinnesharingan(EntityPlayer player, CallbackInfoReturnable<Boolean> cir){

        ItemStack helmetstack = player.getItemStackFromSlot(EntityEquipmentSlot.HEAD);
        ItemStack dojutsu_left = DojutsuSlot.invMan.getDojutsuSlotInventory(player.getUniqueID()).getStackInSlot(1);
        ItemStack dojutsu_right = DojutsuSlot.invMan.getDojutsuSlotInventory(player.getUniqueID()).getStackInSlot(0);

        boolean has_byakugan = helmetstack.getItem() == helmet || dojutsu_left.getItem() == helmet
                || dojutsu_right.getItem() == helmet;

        if ((has_byakugan) && (isRinnesharinganActivated(helmetstack)
                || isRinnesharinganActivated(dojutsu_left) || isRinnesharinganActivated(dojutsu_right))) {
            cir.setReturnValue(true);
        }
    }

}
