package com.fuskirby.dojutsu_slot.mixin.byakugan;

import com.fuskirby.dojutsu_slot.util.DojutsuSlotHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.NonNullList;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.narutomod.gui.overlay.OverlayByakuganView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = OverlayByakuganView.GUIRenderEventClass.class, remap = false)
public class MixinOverlayByakuganView {

    @SideOnly(Side.CLIENT)
    @Redirect(method = "eventHandler",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/util/NonNullList;get(I)Ljava/lang/Object;"),
            remap = false)
    private static Object redirectGetHelmet(NonNullList instance, int index) {
        if (index == 3) {
            Minecraft mc = Minecraft.getMinecraft();
            EntityPlayer player = mc.player;
            return DojutsuSlotHelper.selectDojutsuForJutsu(player);
        }
        return instance.get(index);
    }
}
