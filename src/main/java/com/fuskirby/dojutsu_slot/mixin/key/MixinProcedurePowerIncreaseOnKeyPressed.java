package com.fuskirby.dojutsu_slot.mixin.key;

import com.fuskirby.dojutsu_slot.DojutsuSlot;
import com.fuskirby.dojutsu_slot.DojutsuSlotContext;
import com.fuskirby.dojutsu_slot.data.CustomSusanooData;
import com.fuskirby.dojutsu_slot.network.packet.PacketSyncDojutsuSlot;
import com.fuskirby.dojutsu_slot.util.DojutsuSlotHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.narutomod.entity.EntitySusanooBase;
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
public abstract class MixinProcedurePowerIncreaseOnKeyPressed{

    @Unique
    private static boolean isSwitchAlready = false;

    @Inject(method = "executeProcedure", at = @At(value = "INVOKE", target = "Lnet/narutomod/item/ItemJutsu$Base;switchNextJutsu(Lnet/minecraft/item/ItemStack;Lnet/minecraft/entity/EntityLivingBase;)V"))
    private static void isSwitchAlready(Map<String, Object> dependencies, CallbackInfo ci) {
        isSwitchAlready = true;
    }

    @Inject(method = "executeProcedure", at = @At("TAIL"))
    private static void executeProcedure(Map<String, Object> dependencies, CallbackInfo ci) {
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

        if (!world.isRemote && !(isMangekyoSharingan(helmet) ||
                isMangekyoSharingan(dojutsu_left) ||
                isMangekyoSharingan(dojutsu_right))) {
            if (CustomSusanooData.hasAwakenedSusanoo(player) &&
                    player.getRidingEntity() instanceof EntitySusanooBase) {
                if (!is_pressed) {
                    ProcedureSusanoo.upgrade(player);
                }
            }
        }

        // left
        if ((!(world.isRemote))) {
            if (DojutsuSlotHelper.shouldExecuteLeftSlot(player)) {
                 if ((DojutsuSlotHelper.isDojutsuItem(dojutsu_left) && dojutsu_left.getItem() instanceof ItemDojutsu.Base
                        && ((ItemDojutsu.Base) dojutsu_left.getItem()).onSwitchJutsuKey(is_pressed, dojutsu_left, player))) {
                     DojutsuSlotContext.setCurrentSlot(1);
                }
            }
        }

        // right
        if ((!(world.isRemote))) {
            if (DojutsuSlotHelper.shouldExecuteRightSlot(player)) {
                if ((DojutsuSlotHelper.isDojutsuItem(dojutsu_right) && dojutsu_right.getItem() instanceof ItemDojutsu.Base
                        && ((ItemDojutsu.Base) dojutsu_right.getItem()).onSwitchJutsuKey(is_pressed, dojutsu_right, player))) {
                    DojutsuSlotContext.setCurrentSlot(2);
                }
            }
        }

        if (DojutsuSlotContext.getCurrentSlot() != 0 && !is_pressed) {
            DojutsuSlot.network.sendToAll(new PacketSyncDojutsuSlot(player, (DojutsuSlotContext.getCurrentSlot() - 1)));
        }
    }

    @Unique
    private static boolean isMangekyoSharingan(ItemStack stack) {
        if (stack.isEmpty()) {
            return false;
        } else if (stack.getItem() instanceof ItemSharingan.Base) {
            return ((ItemSharingan.Base) stack.getItem()).isMangekyo();
        }
        return false;
    }
}
