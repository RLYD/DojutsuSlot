package com.fuskirby.dojutsu_slot.mixin.byakugan;

import com.fuskirby.dojutsu_slot.util.DojutsuSlotHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.narutomod.item.ItemDojutsu;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(targets = "net.narutomod.item.ItemByakugan$1", remap = false)
public class MixinItemByakuganBase {

    @Unique private static ItemStack itemstack;

    @Inject(method = "func_77663_a", at = @At("HEAD"))
    private void getItemStack(ItemStack itemstack, World world, Entity entity, int par4, boolean par5, CallbackInfo ci) {
        MixinItemByakuganBase.itemstack = itemstack;
    }

    @Redirect(method = "func_77663_a", at = @At(value = "INVOKE", target = "Lnet/narutomod/item/ItemDojutsu$Base;setOwner(Lnet/minecraft/item/ItemStack;Lnet/minecraft/entity/EntityLivingBase;)V"))
    private void setDojutsuState(ItemDojutsu.Base instance, ItemStack stack, EntityLivingBase entityIn) {
        instance.setOwner(stack, entityIn);
        String state = DojutsuSlotHelper.getDojutsuState(itemstack);
        if (state != null) {
            DojutsuSlotHelper.setDojutsuState(stack, state);
        }
    }
}
