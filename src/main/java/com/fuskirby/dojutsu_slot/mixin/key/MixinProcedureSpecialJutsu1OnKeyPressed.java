package com.fuskirby.dojutsu_slot.mixin.key;

import com.fuskirby.dojutsu_slot.util.DojutsuSlotHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.narutomod.item.*;
import net.narutomod.procedure.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

@Mixin(value = ProcedureSpecialJutsu1OnKeyPressed.class, remap = false)
public class MixinProcedureSpecialJutsu1OnKeyPressed {

    @Inject(method = "executeProcedure", at = @At("TAIL"))
    private static void executeProcedure(Map<String, Object> dependencies, CallbackInfo ci) {
        if (dependencies.get("is_pressed") == null || dependencies.get("entity") == null || dependencies.get("world") == null) {
            return;
        }

        boolean is_pressed = (boolean) dependencies.get("is_pressed");
        Entity entity = (Entity) dependencies.get("entity");
        World world = (World) dependencies.get("world");

        if (world.isRemote || ((EntityPlayer) entity).isSpectator()) {
            return;
        }

        EntityPlayer player = (EntityPlayer) entity;
        ItemStack selectedDojutsu = DojutsuSlotHelper.selectDojutsuForJutsu(player);

        if (selectedDojutsu.isEmpty()) {
            return;
        }
        if (selectedDojutsu.getItem() instanceof ItemDojutsu.Base) {
            ((ItemDojutsu.Base) selectedDojutsu.getItem()).onJutsuKey1(is_pressed, selectedDojutsu, (EntityPlayer) entity);
        }
    }
}