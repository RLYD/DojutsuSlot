package com.fuskirby.dojutsu_slot.mixin.ahznbcursemarkaddon.procedure;

import com.fuskirby.dojutsu_slot.DojutsuSlot;
import com.fuskirby.dojutsu_slot.DojutsuSlotContext;
import com.fuskirby.dojutsu_slot.util.DojutsuAddonHelper;
import com.fuskirby.dojutsu_slot.util.DojutsuSlotHelper;
import com.fuskirby.dojutsu_slot.util.WorldModeHelper;
import net.mcreator.ahznbcursemarkaddon.item.ItemBlueRinnegan;
import net.mcreator.ahznbcursemarkaddon.item.ItemRedRinnegan;
import net.mcreator.ahznbcursemarkaddon.procedure.ProcedureBlueToRedSwitch;
import net.mcreator.ahznbcursemarkaddon.procedure.ProcedureRedToBlueSwitch;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Loader;
import net.narutomod.NarutomodModVariables;
import net.narutomod.item.ItemDojutsu;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;
import java.util.Objects;

@Mixin(
        value = {
                ProcedureRedToBlueSwitch.class,
                ProcedureBlueToRedSwitch.class
        },
        remap = false)
public class MixinProcedureUrashikiRinneganSwitch {
    @Inject(method = "executeProcedure",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraftforge/fml/common/eventhandler/EventBus;register(Ljava/lang/Object;)V"), cancellable = true)
    private static void switchRinnegan(Map<String, Object> dependencies, CallbackInfo ci) {
        Entity entity = (Entity)dependencies.get("entity");
        World world = (World)dependencies.get("world");
        boolean is_pressed = (Boolean)dependencies.get("is_pressed");
        if (entity instanceof EntityPlayer) {
            if (is_pressed) {
                final EntityPlayer player = (EntityPlayer) entity;
                if (!player.getEntityData().getBoolean("switch_rinnegan")) {
                    ItemStack helmet = DojutsuSlotHelper.selectDojutsuForJutsu(player);
                    IInventory inv = (IInventory) DojutsuSlot.invMan.getDojutsuSlotInventory(player.getUniqueID());
                    ItemStack rinnegan = ItemStack.EMPTY;
                    if (helmet.getItem() == ItemBlueRinnegan.helmet) {
                        rinnegan = new ItemStack(ItemRedRinnegan.helmet, 1);
                    } else if (helmet.getItem() == ItemRedRinnegan.helmet) {
                        rinnegan = new ItemStack(ItemBlueRinnegan.helmet, 1);
                    }
                    if (rinnegan != ItemStack.EMPTY) {
                        ((ItemDojutsu.Base) rinnegan.getItem()).setOwner(rinnegan, player);
                        if (WorldModeHelper.isDojutsuMode(world)) {
                            if (DojutsuSlotContext.getCurrentSlot() == 1) {
                                DojutsuSlotHelper.setDojutsuState(rinnegan, "left");
                            } else if (DojutsuSlotContext.getCurrentSlot() == 2) {
                                DojutsuSlotHelper.setDojutsuState(rinnegan, "right");
                            }
                        }
                        if (Loader.isModLoaded("dojutsu_addon")) {
                            if (DojutsuAddonHelper.hasRegisteredTag(helmet)) {
                                DojutsuAddonHelper.addRegisteredTag(rinnegan);
                            }
                        }
                    }

                    inv.setInventorySlotContents((DojutsuSlotContext.getCurrentSlot() - 1), rinnegan);
                    world.playSound(null, player.getPosition(), Objects.requireNonNull(SoundEvent.REGISTRY.getObject(new ResourceLocation("ahznbcursemarkaddon:activatemangekyo"))), SoundCategory.NEUTRAL, 1.0F, 1.0F);
                    player.getEntityData().setDouble("madara_cd", NarutomodModVariables.world_tick + 200.0D);
                }
                entity.getEntityData().setBoolean("switch_rinnegan", true);
                ci.cancel();
            } else {
                entity.getEntityData().setBoolean("switch_rinnegan", false);
                ci.cancel();
            }
        }
    }
}
