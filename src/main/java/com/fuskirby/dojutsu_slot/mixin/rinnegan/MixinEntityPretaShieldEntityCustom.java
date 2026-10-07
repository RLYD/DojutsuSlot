package com.fuskirby.dojutsu_slot.mixin.rinnegan;

import com.fuskirby.dojutsu_slot.util.DojutsuSlotHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.narutomod.entity.EntityPretaShield;
import net.narutomod.entity.EntityShieldBase;
import net.narutomod.item.ItemRinnegan;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = EntityPretaShield.EntityCustom.class, remap = false)
public abstract class MixinEntityPretaShieldEntityCustom extends EntityShieldBase {

    public MixinEntityPretaShieldEntityCustom(World world) {
        super(world);
    }

    @Inject(method = "func_70636_d", at = @At(value = "INVOKE", target = "Lnet/narutomod/entity/EntityPretaShield$EntityCustom;func_70106_y()V"), cancellable = true)
    private void getNarakaPathChakraUsage(CallbackInfo ci) {
        EntityLivingBase summoner = this.getSummoner();
        if (summoner != null) {
            if (summoner instanceof EntityPlayer) {
                EntityPlayer player = (EntityPlayer) summoner;
                Item rinnegan_tomoe = ForgeRegistries.ITEMS.getValue(new ResourceLocation("dojutsu_addon", "rinnegan_tomoe_helmet"));
                if (DojutsuSlotHelper.selectRinneganTomoeForJutsu(player).getItem() == rinnegan_tomoe) {
                    ci.cancel();
                }
                if (DojutsuSlotHelper.selectDojutsuForJutsu(player).getItem() == ItemRinnegan.helmet) {
                    ci.cancel();
                }
            }
        }
    }
}
