package com.fuskirby.dojutsu_slot.mixin.ahznbcursemarkaddon.keybind;

import com.fuskirby.dojutsu_slot.DojutsuSlot;
import com.fuskirby.dojutsu_slot.network.packet.PacketSyncCtrlKey;
import net.mcreator.ahznbcursemarkaddon.ElementsAhznbcursemarkaddonMod;
import net.mcreator.ahznbcursemarkaddon.keybind.*;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Keyboard;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(
    value = {
        KeyBindingSpecialJutsu1.class,
        KeyBindingSpecialJutsu2.class,
        KeyBindingSpecialJutsu3.class,
        KeyBindingSpecialJutsu4.class,
        KeyBindingSpecialJutsu5.class
    },
    remap = false
)
public abstract class MixinKeyBindingSpecialJutsu extends ElementsAhznbcursemarkaddonMod.ModElement {

    public MixinKeyBindingSpecialJutsu(ElementsAhznbcursemarkaddonMod elements, int sortid) {
        super(elements, sortid);
    }

    @SideOnly(Side.CLIENT)
    @Inject(method = "processKeyBind", at = @At("HEAD"))
    private void onProcessKeyBind(CallbackInfo ci) {
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.player != null) {
            boolean ctrlPressed = Keyboard.isKeyDown(Keyboard.KEY_LCONTROL) ||
                    Keyboard.isKeyDown(Keyboard.KEY_RCONTROL);
            DojutsuSlot.network.sendToServer(new PacketSyncCtrlKey(ctrlPressed));
            mc.player.getEntityData().setBoolean("dojutsu_ctrl_pressed", ctrlPressed);
        }
    }
}
