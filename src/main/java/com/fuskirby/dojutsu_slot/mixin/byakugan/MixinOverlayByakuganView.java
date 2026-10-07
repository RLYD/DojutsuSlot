package com.fuskirby.dojutsu_slot.mixin.byakugan;

import com.fuskirby.dojutsu_slot.util.DojutsuSlotHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.narutomod.gui.overlay.OverlayByakuganView;
import net.narutomod.item.ItemDojutsu;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Mixin(value = OverlayByakuganView.GUIRenderEventClass.class, remap = false)
public class MixinOverlayByakuganView {

    // @Unique private static final Map<Class<?>, Boolean> METHOD_CACHE = new ConcurrentHashMap<>();

    @SideOnly(Side.CLIENT)
    @Redirect(method = "eventHandler",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/util/NonNullList;get(I)Ljava/lang/Object;"),
            remap = false)
    private Object redirectGetHelmet(NonNullList<ItemStack> instance, int index) {
        if (index == 3) {
            Minecraft mc = Minecraft.getMinecraft();
            EntityPlayer player = mc.player;
            return DojutsuSlotHelper.selectDojutsuForJutsu(player);
        }
        return instance.get(index);
    }

//    @SideOnly(Side.CLIENT)
//    @Inject(method = "eventHandler", at = @At(value = "INVOKE", target = "Lnet/narutomod/item/ItemDojutsu$Base;getType()Lnet/narutomod/item/ItemDojutsu$Type;"), cancellable = true)
//    private void eventHandler(RenderGameOverlayEvent event, CallbackInfo ci) {
//        if (isDojutsuMissingGetType(getDojutsuItemStack())) {
//            ci.cancel();
//        }
//    }
//
//    @Unique
//    private ItemStack getDojutsuItemStack() {
//        Minecraft mc = Minecraft.getMinecraft();
//        EntityPlayer player = mc.player;
//        return DojutsuSlotHelper.selectDojutsuForJutsu(player);
//    }
//
//    @Unique
//    private boolean isDojutsuMissingGetType(ItemStack stack) {
//        if (stack == null || stack.isEmpty()) return false;
//        if (!(stack.getItem() instanceof ItemDojutsu.Base)) {
//            return false;
//        }
//        Class<?> clazz = stack.getItem().getClass();
//        return METHOD_CACHE.computeIfAbsent(clazz, (c) -> {
//            try {
//                c.getDeclaredMethod("getType");
//                return false;
//            } catch (NoSuchMethodException e) {
//                return true;
//            }
//        });
//    }
}
