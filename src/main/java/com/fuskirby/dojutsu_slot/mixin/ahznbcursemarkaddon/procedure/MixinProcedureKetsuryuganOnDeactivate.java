package com.fuskirby.dojutsu_slot.mixin.ahznbcursemarkaddon.procedure;

import com.fuskirby.dojutsu_slot.DojutsuSlot;
import com.fuskirby.dojutsu_slot.DojutsuSlotContext;
import com.fuskirby.dojutsu_slot.util.DojutsuAddonHelper;
import com.fuskirby.dojutsu_slot.util.DojutsuSlotHelper;
import com.fuskirby.dojutsu_slot.util.WorldModeHelper;
import net.mcreator.ahznbcursemarkaddon.item.ItemDojutsu2;
import net.mcreator.ahznbcursemarkaddon.item.ItemKetsuryuganOff;
import net.mcreator.ahznbcursemarkaddon.procedure.ProcedureKetsuryuganOnDeactivate;
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
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;
import java.util.Objects;

@Mixin(value = ProcedureKetsuryuganOnDeactivate.class, remap = false)
public class MixinProcedureKetsuryuganOnDeactivate {
    @Inject(method = "executeProcedure",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/util/NonNullList;set(ILjava/lang/Object;)Ljava/lang/Object;"), cancellable = true)
    private static void switchKetsuryugan(Map<String, Object> dependencies, CallbackInfo ci) {
        Entity entity = (Entity) dependencies.get("entity");
        World world = (World)dependencies.get("world");
        boolean is_pressed = (Boolean)dependencies.get("is_pressed");
        if (entity instanceof EntityPlayer) {
            if (is_pressed) {
                EntityPlayer player = (EntityPlayer) entity;
                if (!player.getEntityData().getBoolean("switch_ketsuryugan")) {
                    ItemStack helmet = DojutsuSlotHelper.selectDojutsuForJutsu(player);
                    IInventory inv = (IInventory) DojutsuSlot.invMan.getDojutsuSlotInventory(player.getUniqueID());
                    ItemStack ketsuryugan = new ItemStack(ItemKetsuryuganOff.helmet, 1);
                    ((ItemDojutsu2.Base) ketsuryugan.getItem()).setOwner(ketsuryugan, player);
                    if (WorldModeHelper.isDojutsuMode(world)) {
                        if (DojutsuSlotContext.getCurrentSlot() == 1) {
                            DojutsuSlotHelper.setDojutsuState(ketsuryugan, "left");
                        } else if (DojutsuSlotContext.getCurrentSlot() == 2) {
                            DojutsuSlotHelper.setDojutsuState(ketsuryugan, "right");
                        }
                    }
                    if (Loader.isModLoaded("dojutsu_addon")) {
                        if (DojutsuAddonHelper.hasRegisteredTag(helmet)) {
                            DojutsuAddonHelper.addRegisteredTag(ketsuryugan);
                        }
                    }

                    inv.setInventorySlotContents((DojutsuSlotContext.getCurrentSlot() - 1), ketsuryugan);
                    world.playSound(null, player.getPosition(), Objects.requireNonNull(SoundEvent.REGISTRY.getObject(new ResourceLocation("ahznbcursemarkaddon:kestudeactivate"))), SoundCategory.NEUTRAL, 1.0F, 1.0F);
                    player.getEntityData().setDouble("ketsuryugan_cd", NarutomodModVariables.world_tick + 200.0D);
                }
                entity.getEntityData().setBoolean("switch_ketsuryugan", true);
                ci.cancel();
            } else {
                entity.getEntityData().setBoolean("switch_ketsuryugan", false);
                ci.cancel();
            }
        }
    }
}
