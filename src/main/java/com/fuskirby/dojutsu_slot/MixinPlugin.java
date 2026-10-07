package com.fuskirby.dojutsu_slot;

import net.minecraftforge.fml.common.Loader;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.List;
import java.util.Set;

public class MixinPlugin implements IMixinConfigPlugin {

    private static final String MIXIN_SUSANO = "com.fuskirby.dojutsu_slot.mixin.sharingan.MixinProcedureSusanooSkeletonBodyTickEvent";
    private static final String MIXIN_SHRINK = "com.fuskirby.dojutsu_slot.mixin.sharingan.MixinItemSharinganShrinkAmount";
    private static final String MIXIN_ETERNAL_SHRINK = "com.fuskirby.dojutsu_slot.mixin.sharingan.MixinItemMangekyoSharinganEternalShrinkAmount";
    private static final String ANBUBINGOBOOK = "anbubingobook";
    private static final String AHZNBCURSEMARKADDON = "ahznbcursemarkaddon";

    @Override
    public void onLoad(String mixinPackage) {}

    @Override
    public String getRefMapperConfig() {
        return null;
    }

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        if (MIXIN_SUSANO.equals(mixinClassName)) {
            if (Loader.isModLoaded("dojutsu_addon")) {
                return false;
            }
        }
        if (MIXIN_SHRINK.equals(mixinClassName)) {
            if (Loader.isModLoaded("dojutsu_addon")) {
                return false;
            }
        }
        if (MIXIN_ETERNAL_SHRINK.equals(mixinClassName)) {
            if (Loader.isModLoaded("dojutsu_addon")) {
                return false;
            }
        }

        // anbubingobook
        if (mixinClassName.equals("com.fuskirby.dojutsu_slot.mixin.anbubingobook.MixinTrackerPlayerHook")) {
            return Loader.isModLoaded(ANBUBINGOBOOK);
        }
        if (mixinClassName.equals("com.fuskirby.dojutsu_slot.mixin.anbubingobook.MixinProcedureevolve")) {
            return Loader.isModLoaded(ANBUBINGOBOOK);
        }

        // ahznbcursemarkaddon
        if (mixinClassName.equals("com.fuskirby.dojutsu_slot.mixin.ahznbcursemarkaddon.procedure.MixinProcedureSpecialJutsuOnKeyPressed")) {
            return Loader.isModLoaded(AHZNBCURSEMARKADDON);
        }
        if (mixinClassName.equals("com.fuskirby.dojutsu_slot.mixin.ahznbcursemarkaddon.procedure.MixinProcedurePowerIncreaseOnKeyPressed")) {
            return Loader.isModLoaded(AHZNBCURSEMARKADDON);
        }
        if (mixinClassName.equals("com.fuskirby.dojutsu_slot.mixin.ahznbcursemarkaddon.keybind.MixinKeyBindingSpecialJutsu")) {
            return Loader.isModLoaded(AHZNBCURSEMARKADDON);
        }
        if (mixinClassName.equals("com.fuskirby.dojutsu_slot.mixin.ahznbcursemarkaddon.MixinDojutsuAbilities")) {
            return Loader.isModLoaded(AHZNBCURSEMARKADDON);
        }
        if (mixinClassName.equals("com.fuskirby.dojutsu_slot.mixin.ahznbcursemarkaddon.procedure.MixinProcedureSusanoo")) {
            return Loader.isModLoaded(AHZNBCURSEMARKADDON);
        }
        if (mixinClassName.equals("com.fuskirby.dojutsu_slot.mixin.ahznbcursemarkaddon.procedure.MixinProcedureUrashikiRinneganSwitch")) {
            return Loader.isModLoaded(AHZNBCURSEMARKADDON);
        }
        if (mixinClassName.equals("com.fuskirby.dojutsu_slot.mixin.ahznbcursemarkaddon.procedure.MixinProcedureKetsuryuganOffActivate")) {
            return Loader.isModLoaded(AHZNBCURSEMARKADDON);
        }
        if (mixinClassName.equals("com.fuskirby.dojutsu_slot.mixin.ahznbcursemarkaddon.procedure.MixinProcedureKetsuryuganOnDeactivate")) {
            return Loader.isModLoaded(AHZNBCURSEMARKADDON);
        }
        if (mixinClassName.equals("com.fuskirby.dojutsu_slot.mixin.ahznbcursemarkaddon.procedure.MixinProcedureUtils")) {
            return Loader.isModLoaded(AHZNBCURSEMARKADDON);
        }
        if (mixinClassName.equals("com.fuskirby.dojutsu_slot.mixin.ahznbcursemarkaddon.item.MixinItemDojutsu2")) {
            return Loader.isModLoaded(AHZNBCURSEMARKADDON);
        }
        if (mixinClassName.equals("com.fuskirby.dojutsu_slot.mixin.ahznbcursemarkaddon.MixinMedicalScrollGUIOnButtonClicked")) {
            return Loader.isModLoaded(AHZNBCURSEMARKADDON);
        }
        if (mixinClassName.equals("com.fuskirby.dojutsu_slot.mixin.ahznbcursemarkaddon.item.MixinItemJougan")) {
            return Loader.isModLoaded(AHZNBCURSEMARKADDON);
        }
        if (mixinClassName.equals("com.fuskirby.dojutsu_slot.mixin.ahznbcursemarkaddon.item.MixinItemMadaraRinnegan")) {
            return Loader.isModLoaded(AHZNBCURSEMARKADDON);
        }
        if (mixinClassName.equals("com.fuskirby.dojutsu_slot.mixin.ahznbcursemarkaddon.procedure.MixinProcedureMadaraSwitch")) {
            return Loader.isModLoaded(AHZNBCURSEMARKADDON);
        }
        if (mixinClassName.equals("com.fuskirby.dojutsu_slot.mixin.ahznbcursemarkaddon.procedure.MixinProcedureMadaraSwitch$MixinProcedureMadaraSwitch_1")) {
            return Loader.isModLoaded(AHZNBCURSEMARKADDON);
        }

        return true;
    }

    @Override
    public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {}

    @Override
    public List<String> getMixins() {
        return null;
    }

    @Override
    public void preApply(String s, org.objectweb.asm.tree.ClassNode classNode, String s1, IMixinInfo iMixinInfo) {}

    @Override
    public void postApply(String s, org.objectweb.asm.tree.ClassNode classNode, String s1, IMixinInfo iMixinInfo) {}
}