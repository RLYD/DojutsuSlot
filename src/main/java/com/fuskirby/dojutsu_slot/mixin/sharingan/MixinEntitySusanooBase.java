package com.fuskirby.dojutsu_slot.mixin.sharingan;

import com.fuskirby.dojutsu_slot.data.CustomSusanooData;
import com.fuskirby.dojutsu_slot.util.DojutsuSlotHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.narutomod.entity.EntitySusanooBase;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = EntitySusanooBase.class, remap = false)
public class MixinEntitySusanooBase {

    @Shadow protected void setFlameColor(int color) {}

    @Redirect(method = "<init>(Lnet/minecraft/entity/EntityLivingBase;)V",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/entity/EntityLivingBase;func_184582_a(Lnet/minecraft/inventory/EntityEquipmentSlot;)Lnet/minecraft/item/ItemStack;"))
    private static ItemStack redirectGetHelmet(EntityLivingBase instance, EntityEquipmentSlot entityEquipmentSlot) {
        if (entityEquipmentSlot == EntityEquipmentSlot.HEAD) {
            if (instance instanceof EntityPlayer) {
                EntityPlayer player = (EntityPlayer)instance;
                return DojutsuSlotHelper.selectDojutsuForJutsu(player);
            }
        }
        return instance.getItemStackFromSlot(entityEquipmentSlot);
    }

    @Inject(method = "<init>(Lnet/minecraft/entity/EntityLivingBase;)V",
            at = @At(value = "TAIL"))
    private void onConstruct(EntityLivingBase player, CallbackInfo ci) {
        if (player instanceof EntityPlayer) {
            EntityPlayer entityPlayer = (EntityPlayer) player;
            if (CustomSusanooData.hasAwakenedSusanoo(entityPlayer)) {
                int color = CustomSusanooData.getSusanooColor(entityPlayer);
                if (color != 0) {
                    setFlameColor(color);
                }
            }
        }
    }
}
