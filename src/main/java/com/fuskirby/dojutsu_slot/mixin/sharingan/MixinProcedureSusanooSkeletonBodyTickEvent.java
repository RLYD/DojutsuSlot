package com.fuskirby.dojutsu_slot.mixin.sharingan;

import com.fuskirby.dojutsu_slot.util.DojutsuSlotHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.narutomod.item.ItemRinnegan;
import net.narutomod.procedure.ProcedureSusanooSkeletonBodyTickEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Map;

@Mixin(value = ProcedureSusanooSkeletonBodyTickEvent.class, remap = false)
public class MixinProcedureSusanooSkeletonBodyTickEvent {
    @Redirect(
            method = "executeProcedure",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/player/InventoryPlayer;func_70431_c(Lnet/minecraft/item/ItemStack;)Z"
            )
    )
    private static boolean redirectHasItemStack(InventoryPlayer inventory, ItemStack stack, Map<String, Object> dependencies) {
        Entity entity = (Entity) dependencies.get("entity");
        EntityPlayer player = (EntityPlayer) entity;

        ItemStack leftDojutsu = DojutsuSlotHelper.getLeftDojutsu(player);
        ItemStack rightDojutsu = DojutsuSlotHelper.getRightDojutsu(player);

        return player.inventory.hasItemStack(new ItemStack(ItemRinnegan.helmet, 1)) || (leftDojutsu.getItem() == ItemRinnegan.helmet) || (rightDojutsu.getItem() == ItemRinnegan.helmet);
    }
}
