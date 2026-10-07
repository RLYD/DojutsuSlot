package com.fuskirby.dojutsu_slot.mixin.ahznbcursemarkaddon;

import com.fuskirby.dojutsu_slot.util.DojutsuSlotHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.mcreator.ahznbcursemarkaddon.procedure.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

@Mixin(
    value = {
            ProcedureEMSAmaterasu.class,
            ProcedureKetsuryuganOffActivate.class,
            ProcedureKetsuryuganGenjutsu.class,
            ProcedureMadaraLimbo.class,
            ProcedureMadaraTimeRewind.class,
            ProcedureBlueRinneganTemporalRewind.class,
            ProcedureRedRinneganYomotsuHirasaka.class,
            ProcedureReflectingJutsu.class,
            ProcedureKinganHakkeKusho.class,
            ProcedureByakusharinganHakkeKusho.class,
            ProcedureJouganHakkeKusho.class,
            ProcedureJouganKarma.class,
            ProcedureCodeKarma.class,
            ProcedureKokuganKarma.class,
            ProcedureJigenKarma.class,
            ProcedureMagiciansOPGenjutsu.class,
            ProcedureKokuganKarma.class,
            ProcedureAmaterasu.class,
            ProcedureKetsuryuganBloodDragon.class,
            ProcedureBlueRinneganOrigamiReplacement.class,
            ProcedureRedRinneganSword.class,
            ProcedureKinganEightTrigrams64Palms.class,
            ProcedureByakusharinganEightTrigrams64Palms.class,
            ProcedureJouganYomotsuHirasaka.class,
            ProcedureKetsuryuganExplosive.class,
            ProcedureDuduEdotensei.class,
            ProcedureMangekyoSusanooArmor.class,
            ProcedureBlueToRedSwitch.class,
            ProcedureRedToBlueSwitch.class,
            ProcedureChakraDrain.class,
            ProcedureKinganHakkeshoKaiten.class,
            ProcedureByakusharinganHakkeshoKaiten.class,
            ProcedureJouganKarma.class,
            ProcedureMagiciansChakraDrain.class,
            ProcedureKetsuryuganOnDeactivate.class,
            ProcedureMagiciansTp.class
    },
    remap = false)
public class MixinDojutsuAbilities {

    @Unique
    private static EntityPlayer player = null;

    @Inject(method = "executeProcedure", at = @At(value = "HEAD"))
    private static void getPlayer(Map<String, Object> dependencies, CallbackInfo ci) {
        Entity entity = (Entity)dependencies.get("entity");
        if (entity instanceof EntityPlayer) {
            player = (EntityPlayer) entity;
        }
    }

    @Redirect(method = "executeProcedure",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/util/NonNullList;get(I)Ljava/lang/Object;"))
    private static Object redirectGetHelmet(NonNullList<ItemStack> instance, int index) {
        if (index == 3) {
            if (player != null) {
                return DojutsuSlotHelper.selectDojutsuForJutsu(player);
            }
        }
        return instance.get(index);
    }
}
