package com.fuskirby.dojutsu_slot.mixin.sharingan;

import com.fuskirby.dojutsu_slot.util.UtilKamuiHelper;
import com.fuskirby.dojutsu_slot.util.WorldModeHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.narutomod.procedure.ProcedureSpecialJutsu1OnKeyPressed;
import net.narutomod.procedure.ProcedureSpecialJutsu3OnKeyPressed;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

@Mixin(
    value = {
        ProcedureSpecialJutsu1OnKeyPressed.class,
        ProcedureSpecialJutsu3OnKeyPressed.class
    },
    remap = false
)
public class MixinProcedureKamuiOnKeyPressed {
    @Inject(method = "executeProcedure", at = @At(value = "INVOKE", target = "Lnet/narutomod/procedure/ProcedureKamuiJikukanIdo;executeProcedure(Ljava/util/Map;)V"), cancellable = true)
    private static void onExecute(Map<String, Object> dependencies, CallbackInfo cir) {
        Entity entity = (Entity)dependencies.get("entity");
        if (entity instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) entity;

            boolean is_pressed = (boolean) dependencies.get("is_pressed");

            if (!WorldModeHelper.isDojutsuMode(entity.world)) return;

            boolean isLeft = UtilKamuiHelper.isLeftEye();
            boolean isRight = UtilKamuiHelper.isRightEye();

            if (!isLeft && !isRight) return;
            if (UtilKamuiHelper.hasLeftEye(player) && UtilKamuiHelper.hasRightEye(player)) return;

            if (entity.isSneaking()) {
                if (!is_pressed) {
                    if (isRight) {
                        if (!UtilKamuiHelper.tryRightEyeTeleport(player)) {
                            UtilKamuiHelper.teleportSelf(player);
                        }
                        cir.cancel();
                    }
                }
            } else {
                if (isLeft) {
                    cir.cancel();
                }
            }
        }
    }
}
