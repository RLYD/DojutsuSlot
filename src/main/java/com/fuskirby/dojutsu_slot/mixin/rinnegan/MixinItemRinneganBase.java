package com.fuskirby.dojutsu_slot.mixin.rinnegan;

import com.fuskirby.dojutsu_slot.util.DojutsuSlotHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.narutomod.entity.EntityTenTails;
import net.narutomod.gui.GuiNinjaScroll;
import net.narutomod.item.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = ItemRinnegan.Base.class, remap = false)
public abstract class MixinItemRinneganBase {

    @Unique private ItemStack helmet_stack = null;
    @Unique private ItemStack dojutsu_left = null;
    @Unique private ItemStack dojutsu_right = null;
    @Unique private ItemStack tomoe_rinnegan_left = null;
    @Unique private ItemStack tomoe_rinnegan_right = null;

    @Inject(method = "onUpdatePost", at = @At("HEAD"))
    private void getStack(EntityPlayer player, CallbackInfo ci) {
        this.helmet_stack = player.getItemStackFromSlot(EntityEquipmentSlot.HEAD);
        this.dojutsu_left = DojutsuSlotHelper.getLeftDojutsu(player);
        this.dojutsu_right = DojutsuSlotHelper.getRightDojutsu(player);
        this.tomoe_rinnegan_left = DojutsuSlotHelper.getLeftTomoeRinnegan(player);
        this.tomoe_rinnegan_right = DojutsuSlotHelper.getRightTomoeRinnegan(player);
    }

    @Redirect(
            method = "onUpdatePost",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/narutomod/gui/GuiNinjaScroll;enableJutsu(Lnet/minecraft/entity/player/EntityPlayer;Lnet/narutomod/item/ItemJutsu$Base;Lnet/narutomod/item/ItemJutsu$JutsuEnum;Z)Lnet/minecraft/item/ItemStack;"
            ),
            remap = false
    )
    private ItemStack redirectEnableJutsu(EntityPlayer player, ItemJutsu.Base base, ItemJutsu.JutsuEnum jutsu, boolean enable) {
        boolean newFlag;

        if (jutsu == ItemYoton.SEALING9D) {
            newFlag = hasRinneganBase();
        } else if (jutsu == ItemYoton.SEALING10) {
            boolean tenTailsAdded = EntityTenTails.getBijuManager().isAddedToWorld(player.world);
            newFlag = hasRinneganBase() && tenTailsAdded;
        } else {
            newFlag = enable;
        }

        return GuiNinjaScroll.enableJutsu(player, base, jutsu, newFlag);
    }

    @Inject(method = "onUpdatePost", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/InventoryPlayer;func_174925_a(Lnet/minecraft/item/Item;IILnet/minecraft/nbt/NBTTagCompound;)I"), cancellable = true)
    private void clearMatchingItems(EntityPlayer player, CallbackInfo ci) {
        if (hasRinneganBase()) {
            ci.cancel();
        }
    }

//    @Inject(method = "onSwitchJutsuKey", at = @At("RETURN"))
//    private void PacketSyncDojutsuSlot(boolean is_pressed, ItemStack stack, EntityPlayer entity, CallbackInfoReturnable<Boolean> cir) {
//        DojutsuSlot.network.sendToAll(new PacketSyncDojutsuSlot(entity, DojutsuSlotContext.getCurrentSlot()));
//    }

    @Unique
    private boolean hasRinneganBase() {
        return helmet_stack.getItem() instanceof ItemRinnegan.Base
                || dojutsu_left.getItem() instanceof ItemRinnegan.Base
                || dojutsu_right.getItem() instanceof ItemRinnegan.Base
                || tomoe_rinnegan_left.getItem() instanceof ItemRinnegan.Base
                || tomoe_rinnegan_right.getItem() instanceof ItemRinnegan.Base;
    }
}
