package com.fuskirby.dojutsu_slot.mixin.rinnegan;

import com.fuskirby.dojutsu_slot.util.DojutsuSlotHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.narutomod.entity.EntityTenTails;
import net.narutomod.gui.GuiNinjaScroll;
import net.narutomod.item.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(targets = "net.narutomod.item.ItemRinnegan$1", remap = false)
public abstract class MixinItemRinneganBase {

    @Unique private ItemStack helmet_stack = null;
    @Unique private ItemStack dojutsu_left = null;
    @Unique private ItemStack dojutsu_right = null;
    @Unique private ItemStack tomoe_rinnegan_left = null;
    @Unique private ItemStack tomoe_rinnegan_right = null;

    @Inject(method = "func_77663_a", at = @At("HEAD"))
    private void getStack(ItemStack itemstack, World world, Entity entity, int par4, boolean par5, CallbackInfo ci) {
        if (entity instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer)entity;
            this.helmet_stack = player.getItemStackFromSlot(EntityEquipmentSlot.HEAD);
            this.dojutsu_left = DojutsuSlotHelper.getLeftDojutsu(player);
            this.dojutsu_right = DojutsuSlotHelper.getRightDojutsu(player);
            this.tomoe_rinnegan_left = DojutsuSlotHelper.getLeftTomoeRinnegan(player);
            this.tomoe_rinnegan_right = DojutsuSlotHelper.getRightTomoeRinnegan(player);
        }
    }

    @Redirect(
            method = "func_77663_a",
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

    @Inject(method = "func_77663_a", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/InventoryPlayer;func_174925_a(Lnet/minecraft/item/Item;IILnet/minecraft/nbt/NBTTagCompound;)I"), cancellable = true)
    private void clearMatchingItems(ItemStack itemstack, World world, Entity entity, int par4, boolean par5, CallbackInfo ci) {
        if (hasRinneganBase()) {
            ci.cancel();
        }
    }

    @Unique
    private boolean hasRinneganBase() {
        Item rinnegantomoe = ForgeRegistries.ITEMS.getValue(new ResourceLocation("dojutsu_addon", "rinnegan_tomoe_helmet"));

        boolean has_rinngan = helmet_stack.getItem() == ItemRinnegan.helmet
                || dojutsu_left.getItem() == ItemRinnegan.helmet
                || dojutsu_right.getItem() == ItemRinnegan.helmet;
        boolean has_tenseigan = helmet_stack.getItem() == ItemTenseigan.helmet
                || dojutsu_left.getItem() == ItemTenseigan.helmet
                || dojutsu_right.getItem() == ItemTenseigan.helmet;

        boolean has_tomoe_rinnegan = helmet_stack.getItem() == rinnegantomoe
                || dojutsu_left.getItem() == rinnegantomoe
                || tomoe_rinnegan_left.getItem() == rinnegantomoe
                || dojutsu_right.getItem() == rinnegantomoe
                || tomoe_rinnegan_right.getItem() == rinnegantomoe;

        return has_rinngan || has_tenseigan || has_tomoe_rinnegan;
    }

}
