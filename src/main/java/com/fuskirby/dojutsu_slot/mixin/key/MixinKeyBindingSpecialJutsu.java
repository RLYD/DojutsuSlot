package com.fuskirby.dojutsu_slot.mixin.key;

import com.fuskirby.dojutsu_slot.DojutsuSlot;
import com.fuskirby.dojutsu_slot.network.packet.PacketSyncCtrlKey;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.narutomod.ElementsNarutomodMod;
import net.narutomod.keybind.KeyBindingPowerIncrease;
import net.narutomod.keybind.KeyBindingSpecialJutsu2;
import net.narutomod.keybind.KeyBindingSpecialJutsu3;
import org.lwjgl.input.Keyboard;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(
    value = {
        KeyBindingSpecialJutsu2.class,
        KeyBindingSpecialJutsu3.class,
        KeyBindingPowerIncrease.class
    },
    remap = false
)
public abstract class MixinKeyBindingSpecialJutsu extends ElementsNarutomodMod.ModElement {

    public MixinKeyBindingSpecialJutsu(ElementsNarutomodMod elements, int sortid) {
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
