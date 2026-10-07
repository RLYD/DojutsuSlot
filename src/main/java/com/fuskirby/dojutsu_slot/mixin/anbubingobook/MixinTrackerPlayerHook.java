package com.fuskirby.dojutsu_slot.mixin.anbubingobook;

import com.fuskirby.dojutsu_slot.util.DojutsuSlotHelper;
import net.mcreator.anbubingobook.Tracker;
import net.mcreator.anbubingobook.procedure.procedureevolve;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.narutomod.item.ItemSharingan;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

@Mixin(value = Tracker.PlayerHook.class, remap = false)
public class MixinTrackerPlayerHook {

    @Unique
    private EntityPlayer player = null;
    @Unique
    private ItemStack dojutsu = ItemStack.EMPTY;

    @Inject(method = "LivingDeathEvent", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/NonNullList;get(I)Ljava/lang/Object;"))
    private void getPlayer(LivingDeathEvent event, CallbackInfo ci) {
        player = (EntityPlayer)event.getSource().getTrueSource();
    }

    @Redirect(method = "LivingDeathEvent", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/NonNullList;get(I)Ljava/lang/Object;"))
    private Object onLivingDeathEvent(NonNullList<ItemStack> instance, int p_get_1_) {
        if (p_get_1_ == 3) {
            if (player != null) {
                ItemStack dojutsu_left = DojutsuSlotHelper.getLeftDojutsu(player);
                ItemStack dojutsu_right = DojutsuSlotHelper.getRightDojutsu(player);
                if (instance.get(3) != ItemStack.EMPTY && instance.get(3).getItem() == ItemSharingan.helmet
                        && instance.get(3).getTagCompound() != null && !instance.get(3).getTagCompound().hasKey("awakened")) {
                    dojutsu = instance.get(p_get_1_);
                } else if (dojutsu_left != ItemStack.EMPTY && dojutsu_left.getItem() == ItemSharingan.helmet
                        && dojutsu_left.getTagCompound() != null && !dojutsu_left.getTagCompound().hasKey("awakened")) {
                    dojutsu = dojutsu_left;
                } else if (dojutsu_right != ItemStack.EMPTY && dojutsu_right.getItem() == ItemSharingan.helmet
                        && dojutsu_right.getTagCompound() != null && !dojutsu_right.getTagCompound().hasKey("awakened")) {
                    dojutsu = dojutsu_right;
                }
                return dojutsu;
            }
        }
        return instance.get(p_get_1_);
    }

    @Redirect(method = "LivingDeathEvent", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;func_190918_g(I)V"))
    private void donotShrink(ItemStack instance, int i) {
        instance.shrink(0);
    }

    @Redirect(method = "LivingDeathEvent", at = @At(value = "INVOKE", target = "Lnet/mcreator/anbubingobook/procedure/procedureevolve;executeProcedure(Ljava/util/Map;)V"))
    private void donotShrink(Map<String, Object> dependencies) {
        dependencies.put("itemstack", dojutsu);
        procedureevolve.executeProcedure(dependencies);
    }

}
