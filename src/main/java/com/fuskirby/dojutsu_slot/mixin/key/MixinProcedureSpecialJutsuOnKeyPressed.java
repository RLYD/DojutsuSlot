package com.fuskirby.dojutsu_slot.mixin.key;

import com.fuskirby.dojutsu_slot.util.DojutsuSlotHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.narutomod.procedure.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

@Mixin(
    value = {
        ProcedureSpecialJutsu1OnKeyPressed.class,
        ProcedureSpecialJutsu2OnKeyPressed.class,
        ProcedureSpecialJutsu3OnKeyPressed.class
    },
    remap = false)
public class MixinProcedureSpecialJutsuOnKeyPressed {

    @Inject(method = "executeProcedure", at = @At("HEAD"))
    private static void executeProcedureHead(Map<String, Object> dependencies, CallbackInfo ci){
        Entity entity = (Entity) dependencies.get("entity");

        String CTRL_pressed = "CTRL_pressed";

        EntityPlayer player = (EntityPlayer) entity;

        ItemStack selected = DojutsuSlotHelper.selectDojutsuForJutsu(player);

        if (selected.isEmpty()) {
            entity.getEntityData().setBoolean((CTRL_pressed), false);
            return;
        }

        boolean isTomoeRinnegan = false;
        ResourceLocation registryName = selected.getItem().getRegistryName();
        if (registryName != null) {
            isTomoeRinnegan = registryName.toString().equals("dojutsu_addon:rinnegan_tomoe_helmet");
        }

        if (isTomoeRinnegan){
            ci.cancel();
        }
    }

    @Redirect(method = "executeProcedure",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/util/NonNullList;get(I)Ljava/lang/Object;"),
            remap = false)
    private static Object redirectGetHelmet(NonNullList instance, int index, Map<String, Object> dependencies) {
        if (index == 3) {
            Entity entity = (Entity) dependencies.get("entity");
            if (entity instanceof EntityPlayer) {
                EntityPlayer player = (EntityPlayer) entity;
                return DojutsuSlotHelper.selectDojutsuForJutsu(player);
            }
        }

        return instance.get(index);
    }

    @Inject(method = "executeProcedure", at = @At("TAIL"))
    private static void executeProcedure(Map<String, Object> dependencies, CallbackInfo ci) {
        Entity entity = (Entity) dependencies.get("entity");
        String CTRL_pressed = "CTRL_pressed";
        entity.getEntityData().setBoolean((CTRL_pressed), false);
    }
}
