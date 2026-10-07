package com.fuskirby.dojutsu_slot.mixin.key;

import com.fuskirby.dojutsu_slot.DojutsuSlot;
import com.fuskirby.dojutsu_slot.network.packet.PacketSyncCtrlKey;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.narutomod.ElementsNarutomodMod;
import net.narutomod.keybind.KeyBindingSpecialJutsu1;
import org.lwjgl.input.Keyboard;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(
    value = {
        KeyBindingSpecialJutsu1.class
    },
    remap = false
)
public abstract class MixinKeyBindingSpecialJutsu1 extends ElementsNarutomodMod.ModElement {

    public MixinKeyBindingSpecialJutsu1(ElementsNarutomodMod elements, int sortid) {
        super(elements, sortid);
    }

    @SideOnly(Side.CLIENT)
    @Inject(method = "onClientPostTick", at = @At("HEAD"))
    private void onClientPostTick(CallbackInfo ci) {
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.player != null) {
            boolean ctrlPressed = Keyboard.isKeyDown(Keyboard.KEY_LCONTROL) ||
                    Keyboard.isKeyDown(Keyboard.KEY_RCONTROL);
            DojutsuSlot.network.sendToServer(new PacketSyncCtrlKey(ctrlPressed));
            mc.player.getEntityData().setBoolean("dojutsu_ctrl_pressed", ctrlPressed);
        }
    }
}
