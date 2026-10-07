package com.fuskirby.dojutsu_slot.mixin.ahznbcursemarkaddon.procedure;

import com.fuskirby.dojutsu_slot.DojutsuSlot;
import com.fuskirby.dojutsu_slot.DojutsuSlotContext;
import com.fuskirby.dojutsu_slot.util.DojutsuAddonHelper;
import com.fuskirby.dojutsu_slot.util.DojutsuSlotHelper;
import com.fuskirby.dojutsu_slot.util.WorldModeHelper;
import net.mcreator.ahznbcursemarkaddon.item.ItemMadaraRinnegan;
import net.mcreator.ahznbcursemarkaddon.item.ItemMangekyoSharinganMadara;
import net.mcreator.ahznbcursemarkaddon.procedure.ProcedureMadaraSwitch;
import net.mcreator.ahznbcursemarkaddon.procedure.ProcedureRinneganMadaraSwitch;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Loader;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

@Mixin(value = {
        ProcedureMadaraSwitch.class,
        ProcedureRinneganMadaraSwitch.class,
},
        remap = false)
public class MixinProcedureMadaraSwitch {
    @Unique private static EntityPlayer mixin_player = null;
    @Unique private static ItemStack item_stack = null;

    @Inject(method = "executeProcedure", at = @At(value = "HEAD"))
    private static void getPlayer(Map<String, Object> dependencies, CallbackInfo ci) {
        Entity entity = (Entity)dependencies.get("entity");
        if (entity instanceof EntityPlayer) {
            mixin_player = (EntityPlayer) entity;
        }
    }

    @Redirect(method = "executeProcedure",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/util/NonNullList;get(I)Ljava/lang/Object;"),
            remap = false)
    private static Object redirectGetHelmet(NonNullList<ItemStack> instance, int index) {
        if (index == 3) {
            if (mixin_player != null) {
                item_stack = DojutsuSlotHelper.selectDojutsuForJutsu(mixin_player);
                return item_stack;
            }
        }
        return instance.get(index);
    }

    @Mixin(targets = {
            "net.mcreator.ahznbcursemarkaddon.procedure.ProcedureMadaraSwitch$1",
            "net.mcreator.ahznbcursemarkaddon.procedure.ProcedureRinneganMadaraSwitch$1"
    },
            remap = false)
    public static class MixinProcedureMadaraSwitch_1 {
        @Inject(method = "onServerTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/NonNullList;set(ILjava/lang/Object;)Ljava/lang/Object;"))
        private void onServerTick(CallbackInfo ci) {
            if (mixin_player != null) {
                IInventory inv = DojutsuSlot.invMan.getDojutsuSlotInventory(mixin_player.getUniqueID());
                ItemStack stack;
                Item madara_rinnegan = ItemMadaraRinnegan.helmet;
                Item madara_sharingan = ItemMangekyoSharinganMadara.helmet;
                if (item_stack.getItem() == madara_rinnegan) {
                    stack = new ItemStack(madara_sharingan);
                } else if (item_stack.getItem() == madara_sharingan){
                    stack = new ItemStack(madara_rinnegan);
                } else {
                    return;
                }
                if (WorldModeHelper.isDojutsuMode(mixin_player.world)) {
                    if (DojutsuSlotContext.getCurrentSlot() == 1) {
                        DojutsuSlotHelper.setDojutsuState(stack, "left");
                    } else if (DojutsuSlotContext.getCurrentSlot() == 2) {
                        DojutsuSlotHelper.setDojutsuState(stack, "right");
                    }
                }
                if (Loader.isModLoaded("dojutsu_addon")) {
                    if (DojutsuAddonHelper.hasRegisteredTag(item_stack)) {
                        DojutsuAddonHelper.addRegisteredTag(stack);
                    }
                }
                if (DojutsuSlotContext.getCurrentSlot() != 0) {
                    inv.setInventorySlotContents((DojutsuSlotContext.getCurrentSlot() - 1), stack);
                }
                MinecraftForge.EVENT_BUS.unregister(this);
            }
        }
    }
}
