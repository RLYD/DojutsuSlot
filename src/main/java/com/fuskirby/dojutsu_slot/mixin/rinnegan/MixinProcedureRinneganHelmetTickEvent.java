package com.fuskirby.dojutsu_slot.mixin.rinnegan;

import com.fuskirby.dojutsu_slot.DojutsuSlot;
import com.fuskirby.dojutsu_slot.util.DojutsuSlotHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.narutomod.NarutomodModVariables;
import net.narutomod.entity.EntityTenTails;
import net.narutomod.item.*;
import net.narutomod.procedure.ProcedureRinneganHelmetTickEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

@Mixin(value = ProcedureRinneganHelmetTickEvent.class, remap = false)
public class MixinProcedureRinneganHelmetTickEvent {

    @Unique private static EntityPlayer player = null;
    @Unique private static ItemStack itemstack = null;

    @Inject(method = "executeProcedure", at = @At("HEAD"))
    private static void getPlayer(Map<String, Object> dependencies, CallbackInfo ci) {
        Entity entity = (Entity)dependencies.get("entity");
        ItemStack itemstack = (ItemStack)dependencies.get("itemstack");
        if (entity instanceof EntityPlayer) {
            MixinProcedureRinneganHelmetTickEvent.player = (EntityPlayer) entity;
        }
        MixinProcedureRinneganHelmetTickEvent.itemstack = itemstack;
    }

    @Redirect(
            method = "executeProcedure",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/player/InventoryPlayer;func_174925_a(Lnet/minecraft/item/Item;IILnet/minecraft/nbt/NBTTagCompound;)I"
            )
    )
    private static int redirectClearMatchingItems(InventoryPlayer inventory, Item item, int metadata, int limit, NBTTagCompound nbt) {
        if (isRinneganBase(player)) {
            if (item == ItemAsuraCanon.block || item == ItemAsuraPathArmor.body) {
                if (isAsuraPath(player)) {
                    return 0;
                }
            }
        }
        return inventory.clearMatchingItems(item, metadata, limit, nbt);
    }

    @Inject(method = "executeProcedure",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/nbt/NBTTagCompound;func_74757_a(Ljava/lang/String;Z)V",
                    ordinal = 0,
                    shift = At.Shift.AFTER),
            remap = false)
    private static void afterSetRinnesharinganActivated(Map<String, Object> dependencies, CallbackInfo ci) {
        Entity entity = (Entity) dependencies.get("entity");
        if (!(entity instanceof EntityPlayer)) return;
        EntityPlayer player = (EntityPlayer) entity;

        if (!player.equals(EntityTenTails.getBijuManager().getJinchurikiPlayer())) return;

        IInventory dojutsuInv = (IInventory) DojutsuSlot.invMan.getDojutsuSlotInventory(player.getUniqueID());
        for (int i = 0; i < dojutsuInv.getSizeInventory(); i++) {
            setActivated(dojutsuInv.getStackInSlot(i));
        }
    }

    @ModifyArg(method = "executeProcedure",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/narutomod/item/ItemNinjutsu$RangedItem;enableJutsu(Lnet/minecraft/item/ItemStack;Lnet/narutomod/item/ItemJutsu$JutsuEnum;Z)V"),
            index = 1)
    private static ItemJutsu.JutsuEnum enableJutsu(ItemJutsu.JutsuEnum par2) {
        Item rinnegantomoe = ForgeRegistries.ITEMS.getValue(new ResourceLocation("dojutsu_addon", "rinnegan_tomoe_helmet"));
        if (itemstack.getItem() == rinnegantomoe) {
            return ItemNinjutsu.AMENOTEJIKARA;
        }
        return par2;
    }

    @Unique
    private static void setActivated(ItemStack stack) {
        if (stack.isEmpty()) return;
        if (!stack.hasTagCompound()) {
            stack.setTagCompound(new NBTTagCompound());
        }
        if (stack.getTagCompound() != null) {
            stack.getTagCompound().setBoolean(NarutomodModVariables.RINNESHARINGAN_ACTIVATED, true);
        }
    }

    @Unique
    private static boolean isRinneganBase(EntityPlayer player) {
        ItemStack helmet = player.getItemStackFromSlot(EntityEquipmentSlot.HEAD);
        ItemStack dojutsuLeft  = DojutsuSlotHelper.getLeftDojutsu(player);
        ItemStack dojutsuRight = DojutsuSlotHelper.getRightDojutsu(player);

        ItemStack tomoeRinneganLeft = DojutsuSlotHelper.getLeftTomoeRinnegan(player);
        ItemStack tomoeRinneganRight = DojutsuSlotHelper.getRightTomoeRinnegan(player);

        return helmet.getItem() instanceof ItemRinnegan.Base
                || (dojutsuLeft.getItem() instanceof ItemRinnegan.Base || tomoeRinneganLeft.getItem() instanceof ItemRinnegan.Base)
                || (dojutsuRight.getItem() instanceof ItemRinnegan.Base || tomoeRinneganRight.getItem() instanceof ItemRinnegan.Base);
    }

    @Unique
    private static boolean isAsuraPath(EntityPlayer player) {
        ItemStack helmet = player.getItemStackFromSlot(EntityEquipmentSlot.HEAD);
        ItemStack dojutsuLeft = DojutsuSlotHelper.getLeftDojutsu(player);
        ItemStack dojutsuRight = DojutsuSlotHelper.getRightDojutsu(player);
        ItemStack tomoeLeft = DojutsuSlotHelper.getLeftTomoeRinnegan(player);
        ItemStack tomoeRight = DojutsuSlotHelper.getRightTomoeRinnegan(player);

        boolean helmetHas = hasWhichPath(helmet, 1.0);
        boolean leftHas = hasWhichPath(dojutsuLeft, 1.0);
        boolean rightHas = hasWhichPath(dojutsuRight, 1.0);
        boolean tomoeLeftHas = hasWhichPath(tomoeLeft, 1.0);
        boolean tomoeRightHas = hasWhichPath(tomoeRight, 1.0);

        return leftHas || rightHas || tomoeLeftHas || tomoeRightHas || helmetHas;
    }

    @Unique
    private static boolean hasWhichPath(ItemStack stack, double expectedValue) {
        if (stack.isEmpty()) return false;
        NBTTagCompound tag = stack.getTagCompound();
        if (tag == null) return false;
        if (tag.hasKey("which_path")) {
            return tag.getDouble("which_path") == expectedValue;
        }
        return false;
    }
}