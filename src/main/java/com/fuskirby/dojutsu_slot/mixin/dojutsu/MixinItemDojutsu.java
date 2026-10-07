package com.fuskirby.dojutsu_slot.mixin.dojutsu;

import com.fuskirby.dojutsu_slot.DojutsuSlot;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.narutomod.ElementsNarutomodMod;
import net.narutomod.item.ItemDojutsu;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = ItemDojutsu.class,remap = false)
public abstract class MixinItemDojutsu extends ElementsNarutomodMod.ModElement {
    public MixinItemDojutsu(ElementsNarutomodMod elements, int sortid) {
        super(elements, sortid);
    }

    @Inject(method = "wearingAnyDojutsu", at = @At("RETURN"), cancellable = true)
    private static void wearingAnyDojutsu(EntityLivingBase entity, CallbackInfoReturnable<Boolean> cir){
        if (entity.getItemStackFromSlot(EntityEquipmentSlot.HEAD).getItem() instanceof ItemDojutsu.Base
                || DojutsuSlot.invMan.getDojutsuSlotInventory(entity.getUniqueID()).getStackInSlot(0).getItem() instanceof ItemDojutsu.Base
                || DojutsuSlot.invMan.getDojutsuSlotInventory(entity.getUniqueID()).getStackInSlot(1).getItem() instanceof ItemDojutsu.Base) {
            cir.setReturnValue(true);
            cir.cancel();
        }
    }
}
