package com.fuskirby.dojutsu_slot.mixin.dojutsu;

import com.fuskirby.dojutsu_slot.util.DojutsuSlotHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.narutomod.item.ItemDojutsu;
import net.narutomod.item.ItemMangekyoSharingan;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = ItemMangekyoSharingan.class, remap = false)
public class MixinItemMangekyoSharingan {

    @Shadow public static final double AMATERASU_CHAKRA_USAGE = 100.0F;

    @Inject(method = "getAmaterasuChakraUsage", at = @At("RETURN"), cancellable = true)
    private static void getAmaterasuChakraUsage(EntityLivingBase entity, CallbackInfoReturnable<Double> cir) {
        if (entity instanceof EntityPlayer) {
            ItemStack stack = DojutsuSlotHelper.selectDojutsuForJutsu((EntityPlayer) entity);
            if (stack.getItem() instanceof ItemDojutsu.Base) {
                ItemDojutsu.Base dojutsu = (ItemDojutsu.Base) stack.getItem();
                if (dojutsu.isOwner(stack, entity)) {
                    cir.setReturnValue(AMATERASU_CHAKRA_USAGE);
                } else {
                    cir.setReturnValue(AMATERASU_CHAKRA_USAGE * 3);
                }
                return;
            }

            cir.setReturnValue(Double.MAX_VALUE);
            cir.cancel();
        }
    }
}
