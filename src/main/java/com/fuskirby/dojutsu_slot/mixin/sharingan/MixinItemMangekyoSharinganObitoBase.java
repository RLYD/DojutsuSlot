package com.fuskirby.dojutsu_slot.mixin.sharingan;

import com.fuskirby.dojutsu_slot.util.UtilKamuiHelper;
import com.fuskirby.dojutsu_slot.util.WorldModeHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(targets = "net.narutomod.item.ItemMangekyoSharinganObito$1", remap = false)
public class MixinItemMangekyoSharinganObitoBase {
    @Inject(method = "onJutsuKey1", at = @At(value = "INVOKE", target = "Lnet/narutomod/procedure/ProcedureKamuiJikukanIdo;executeProcedure(Ljava/util/Map;)V"), cancellable = true)
    private void onJutsuKey1(boolean is_pressed, ItemStack stack, EntityPlayer entity, CallbackInfoReturnable<Boolean> cir) {
        if (!WorldModeHelper.isDojutsuMode(entity.world)) return;

        boolean isLeft = UtilKamuiHelper.isLeftEye();
        boolean isRight = UtilKamuiHelper.isRightEye();

        if (!isLeft && !isRight) return;
        if (UtilKamuiHelper.hasLeftEye(entity) && UtilKamuiHelper.hasRightEye(entity)) return;

        if (entity.isSneaking()) {
            if (!is_pressed) {
                if (isRight) {
                    if (!UtilKamuiHelper.tryRightEyeTeleport(entity)) {
                        UtilKamuiHelper.teleportSelf(entity);
                    }
                    cir.setReturnValue(true);
                    cir.cancel();
                }
            }
        } else {
            if (isLeft) {
                cir.setReturnValue(true);
                cir.cancel();
            }
        }
    }
}
