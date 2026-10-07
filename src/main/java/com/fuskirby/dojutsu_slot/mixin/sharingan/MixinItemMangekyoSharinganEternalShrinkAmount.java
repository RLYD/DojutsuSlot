package com.fuskirby.dojutsu_slot.mixin.sharingan;

import com.fuskirby.dojutsu_slot.util.WorldModeHelper;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(targets = "net.narutomod.item.ItemMangekyoSharinganEternal$1", remap = false)
public class MixinItemMangekyoSharinganEternalShrinkAmount {
    @Unique
    private World _world = null;

    @Inject(method = "func_77663_a", at = @At("HEAD"))
    private void getWorld(ItemStack itemstack, World world, Entity entity, int par4, boolean par5, CallbackInfo ci) {
        _world = world;
    }

    @ModifyArg(method = "func_77663_a",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/item/ItemStack;func_190918_g(I)V"),
            index = 0)
    private int modifyShrinkAmount(int originalAmount) {
        if (_world != null) {
            if (WorldModeHelper.isDojutsuMode(_world)) {
                return 0;
            }
        }
        return originalAmount;
    }
}
