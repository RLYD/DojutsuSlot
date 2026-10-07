package com.fuskirby.dojutsu_slot.mixin;

import com.fuskirby.dojutsu_slot.data.CustomSusanooData;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.narutomod.procedure.ProcedureOnPlayerPostTick;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

@Mixin(value = ProcedureOnPlayerPostTick.class, remap = false)
public class MixinProcedureOnPlayerPostTick {
    @Inject(method = "executeProcedure", at = @At(value = "INVOKE", target = "Lnet/narutomod/procedure/ProcedureSusanoo;executeProcedure(Ljava/util/Map;)V"), cancellable = true)
    private static void onProcecureSusanoo(Map<String, Object> dependencies, CallbackInfo ci) {
        Entity entity = (Entity) dependencies.get("entity");
        if (entity instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) entity;
            if (player.getEntityData().getBoolean("susanoo_activated")) {
                if (CustomSusanooData.hasAwakenedSusanoo(player)) {
                    ci.cancel();
                }
            }
        }
    }
}
