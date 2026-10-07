package com.fuskirby.dojutsu_slot.mixin;

import com.fuskirby.dojutsu_slot.util.DojutsuSlotHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.narutomod.procedure.ProcedureWhiteZetsuFleshFoodEaten;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

@Mixin(value = ProcedureWhiteZetsuFleshFoodEaten.class, remap = false)
public class MixinProcedureWhiteZetsuFleshFoodEaten {

    @Unique
    private static EntityPlayer player = null;

    @Inject(method = "executeProcedure", at = @At(value = "HEAD"))
    private static void getPlayer(Map<String, Object> dependencies, CallbackInfo ci) {
        Entity entity = (Entity)dependencies.get("entity");
        if (entity instanceof EntityPlayer) {
            player = (EntityPlayer) entity;
        }
    }

    @Redirect(method = "executeProcedure",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/util/NonNullList;get(I)Ljava/lang/Object;"),
            remap = false)
    private static Object redirectGetHelmet(NonNullList<ItemStack> instance, int index) {
        if (index == 3) {
            if (player != null) {
                return DojutsuSlotHelper.selectDojutsuForJutsu(player);
            }
        }
        return instance.get(index);
    }
}
