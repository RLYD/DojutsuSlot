package com.fuskirby.dojutsu_slot.mixin;

import com.fuskirby.dojutsu_slot.DojutsuSlot;
import com.fuskirby.dojutsu_slot.inventory.InventoryDojutsuSlot;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.narutomod.ElementsNarutomodMod;
import net.narutomod.procedure.ProcedureUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = ProcedureUtils.class,remap = false)
public abstract class MixinProcedureUtils extends ElementsNarutomodMod.ModElement {
    public MixinProcedureUtils(ElementsNarutomodMod elements, int sortid) {
        super(elements, sortid);
    }

    @Inject(method = "hasAnyItemOfSubtype", at = @At("RETURN"), cancellable = true)
    private static void hasAnyItemOfSubtype(EntityPlayer player, Class<? extends Item> itemType, CallbackInfoReturnable<Boolean> cir) {
        InventoryDojutsuSlot dojutsuInv = DojutsuSlot.invMan.getDojutsuSlotInventory(player.getUniqueID());
        if (dojutsuInv != null) {
            for (int i = 0; i < dojutsuInv.getSizeInventory(); i++) {
                ItemStack stack = dojutsuInv.getStackInSlot(i);
                if (!stack.isEmpty() && itemType.isAssignableFrom(stack.getItem().getClass())) {
                    cir.setReturnValue(true);
                    return;
                }
            }
        }
    }

    @Inject(method = "hasItemInInventory*", at = @At("RETURN"), cancellable = true)
    private static void hasItemInInventory(EntityPlayer player, Item item, CallbackInfoReturnable<Boolean> cir) {
        InventoryDojutsuSlot dojutsuInv = DojutsuSlot.invMan.getDojutsuSlotInventory(player.getUniqueID());
        if (dojutsuInv != null) {
            for (int i = 0; i < dojutsuInv.getSizeInventory(); i++) {
                ItemStack stack = dojutsuInv.getStackInSlot(i);
                if (!stack.isEmpty() && stack.getItem() == item) {
                    cir.setReturnValue(true);
                    return;
                }
            }
        }
    }
}
