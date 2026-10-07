package com.fuskirby.dojutsu_slot.mixin.sharingan;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.narutomod.item.ItemDojutsu;
import net.narutomod.item.ItemSharingan;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = ItemSharingan.Base.class, remap = false)
public abstract class MixinItemSharinganBase extends ItemDojutsu.Base {
    public MixinItemSharinganBase(ArmorMaterial material) {
        super(material);
    }

    @Redirect(
            method = "onArmorTick",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/item/ItemStack;func_77972_a(ILnet/minecraft/entity/EntityLivingBase;)V"
            )
    )
    private void redirectDamageItem(ItemStack stack, int amount, EntityLivingBase entity,
                                    World world, EntityPlayer player, ItemStack itemstack) {
        Item one_tomoe = ForgeRegistries.ITEMS.getValue(new ResourceLocation("dojutsu_addon", "sharingan_one_tomoe_helmet"));
        Item two_tomoe = ForgeRegistries.ITEMS.getValue(new ResourceLocation("dojutsu_addon", "sharingan_two_tomoe_helmet"));
        if (stack.getItem() != ItemSharingan.helmet && stack.getItem() != one_tomoe && stack.getItem() != two_tomoe) {
            stack.damageItem(amount, entity);
        }
    }

    @Override
    public boolean isRepairable() {
        return false;
    }
}
