package com.fuskirby.dojutsu_slot.mixin.key;

import com.fuskirby.dojutsu_slot.DojutsuSlot;
import com.fuskirby.dojutsu_slot.network.packet.PacketSyncDojutsuSlot;
import com.fuskirby.dojutsu_slot.data.CustomSusanooData;
import com.fuskirby.dojutsu_slot.util.DojutsuSlotHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.narutomod.entity.EntitySusanooBase;
import net.narutomod.gui.overlay.OverlayByakuganView;
import net.narutomod.item.*;
import net.narutomod.procedure.ProcedurePowerIncreaseOnKeyPressed;
import net.narutomod.procedure.ProcedureSusanoo;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

@Mixin(value = ProcedurePowerIncreaseOnKeyPressed.class, remap = false)
public abstract class MixinProcedurePowerIncreaseOnKeyPressed {

    @Unique
    private static boolean isSwitchAlready = false;

    @Inject(method = "executeProcedure", at = @At(value = "INVOKE", target = "Lnet/narutomod/item/ItemJutsu$Base;switchNextJutsu(Lnet/minecraft/item/ItemStack;Lnet/minecraft/entity/EntityLivingBase;)V"))
    private static void isSwitchAlready(Map<String, Object> dependencies, CallbackInfo ci) {
        isSwitchAlready = true;
    }

    @Inject(method = "executeProcedure", at = @At("TAIL"))
    private static void executeProcedure(Map<String, Object> dependencies, CallbackInfo ci) {
        isSwitchAlready = false;
        if (dependencies.get("is_pressed") == null || dependencies.get("entity") == null ||
                dependencies.get("world") == null) {
            return;
        }
        if (isSwitchAlready) {
            isSwitchAlready = false;
            return;
        }

        boolean is_pressed = (boolean) dependencies.get("is_pressed");
        Entity entity = (Entity) dependencies.get("entity");
        World world = (World) dependencies.get("world");
        double i = 0;
        EntityPlayer player = (EntityPlayer) entity;
        ItemStack helmet = player.inventory.armorInventory.get(3);

        ItemStack dojutsu_left = DojutsuSlotHelper.getLeftDojutsu(player);
        ItemStack dojutsu_right = DojutsuSlotHelper.getRightDojutsu(player);

        if (!world.isRemote) {
            if (!((dojutsu_left.getItem()) instanceof ItemSharingan.Base || (dojutsu_right.getItem()) instanceof ItemSharingan.Base
                    || helmet.getItem() instanceof ItemSharingan.Base)) {
                if (CustomSusanooData.hasAwakenedSusanoo(player) && player.getRidingEntity() instanceof EntitySusanooBase) {
                    if (!(is_pressed)) {
                        ProcedureSusanoo.upgrade(player);
                    }
                }
            }
        }

        boolean LeftisTomoeRinnegan = false;
        boolean RightisTomoeRinnegan = false;

        ResourceLocation registryName = dojutsu_left.getItem().getRegistryName();
        if (registryName != null) {
            LeftisTomoeRinnegan = registryName.toString().equals("dojutsu_addon:rinnegan_tomoe_helmet");
        }
        if (registryName != null) {
            RightisTomoeRinnegan = registryName.toString().equals("dojutsu_addon:rinnegan_tomoe_helmet");
        }

        // left
        if (!world.isRemote) {
            if (DojutsuSlotHelper.shouldExecuteLeftSlot(player)) {
                if (DojutsuSlotHelper.isDojutsuItem(dojutsu_left) && dojutsu_left.getItem() == new ItemStack(ItemByakugan.helmet, 1).getItem() &&
                        entity.getEntityData().getBoolean("byakugan_activated")) {
                    if (is_pressed) {
                        entity.getEntityData().setDouble("byakugan_fov", entity.getEntityData().getDouble("byakugan_fov") - 1);
                        OverlayByakuganView.sendCustomData(entity, true, (float) entity.getEntityData().getDouble("byakugan_fov"));
                    }
                } else if (DojutsuSlotHelper.isDojutsuItem(dojutsu_left) && dojutsu_left.getItem() instanceof ItemSharingan.Base &&
                        entity.getRidingEntity() instanceof EntitySusanooBase) {
                    if (!is_pressed) {
                        ProcedureSusanoo.upgrade(player);
                    }
                } else if (DojutsuSlotHelper.isDojutsuItem(dojutsu_left) && !LeftisTomoeRinnegan &&
                        (dojutsu_left.getItem() == new ItemStack(ItemRinnegan.helmet, 1).getItem() ||
                                dojutsu_left.getItem() == new ItemStack(ItemTenseigan.helmet, 1).getItem())) {
                    if (!is_pressed) {
                        if (dojutsu_left.getTagCompound() != null) {
                            i = (dojutsu_left.hasTagCompound() ? dojutsu_left.getTagCompound().getDouble("which_path") : -1) + 1;
                        }
                        if (i > 5) {
                            i = 0;
                        }
                        if (!dojutsu_left.hasTagCompound()) {
                            dojutsu_left.setTagCompound(new NBTTagCompound());
                        }
                        if (dojutsu_left.getTagCompound() != null) {
                            dojutsu_left.getTagCompound().setDouble("which_path", i);
                        }
                        if (entity instanceof EntityPlayer && !entity.world.isRemote) {
                            ((EntityPlayer) entity).sendStatusMessage(new TextComponentString(
                                    net.minecraft.util.text.translation.I18n.translateToLocal(String.format("chattext.rinnegan.path%d", (int) i))), true);
                        }

                        DojutsuSlot.network.sendToAll(new PacketSyncDojutsuSlot(player, 0));
                    }
                }
            }
        }

        // right
        if (!world.isRemote) {
            if (DojutsuSlotHelper.shouldExecuteRightSlot(player)) {
                if (DojutsuSlotHelper.isDojutsuItem(dojutsu_right) && dojutsu_right.getItem() == new ItemStack(ItemByakugan.helmet, 1).getItem() &&
                        entity.getEntityData().getBoolean("byakugan_activated")) {
                    if (is_pressed) {
                        entity.getEntityData().setDouble("byakugan_fov", entity.getEntityData().getDouble("byakugan_fov") - 1);
                        OverlayByakuganView.sendCustomData(entity, true, (float) entity.getEntityData().getDouble("byakugan_fov"));
                    }
                } else if (DojutsuSlotHelper.isDojutsuItem(dojutsu_right) && !RightisTomoeRinnegan && dojutsu_right.getItem() instanceof ItemSharingan.Base &&
                        entity.getRidingEntity() instanceof EntitySusanooBase) {
                    if (!is_pressed) {
                        ProcedureSusanoo.upgrade(player);
                    }
                } else if (DojutsuSlotHelper.isDojutsuItem(dojutsu_right) &&
                        (dojutsu_right.getItem() == new ItemStack(ItemRinnegan.helmet, 1).getItem() ||
                                dojutsu_right.getItem() == new ItemStack(ItemTenseigan.helmet, 1).getItem())) {
                    if (!is_pressed) {
                        if (dojutsu_right.getTagCompound() != null) {
                            i = (dojutsu_right.hasTagCompound() ? dojutsu_right.getTagCompound().getDouble("which_path") : -1) + 1;
                        }
                        if (i > 5) {
                            i = 0;
                        }
                        if (!dojutsu_right.hasTagCompound()) {
                            dojutsu_right.setTagCompound(new NBTTagCompound());
                        }
                        if (dojutsu_right.getTagCompound() != null) {
                            dojutsu_right.getTagCompound().setDouble("which_path", i);
                        }
                        if (entity instanceof EntityPlayer && !entity.world.isRemote) {
                            ((EntityPlayer) entity).sendStatusMessage(new TextComponentString(
                                    net.minecraft.util.text.translation.I18n.translateToLocal(String.format("chattext.rinnegan.path%d", (int) i))), true);
                        }

                        DojutsuSlot.network.sendToAll(new PacketSyncDojutsuSlot(player, 1));
                    }
                }
            }
        }
    }
}
