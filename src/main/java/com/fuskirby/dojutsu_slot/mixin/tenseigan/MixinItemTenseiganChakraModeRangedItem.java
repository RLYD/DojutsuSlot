package com.fuskirby.dojutsu_slot.mixin.tenseigan;

import com.fuskirby.dojutsu_slot.util.DojutsuSlotHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.*;
import net.minecraft.world.World;
import net.narutomod.Particles;
import net.narutomod.item.ItemJutsu;
import net.narutomod.item.ItemTenseigan;
import net.narutomod.item.ItemTenseiganChakraMode;
import net.narutomod.potion.PotionFlight;
import net.narutomod.procedure.ProcedureUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Objects;

@Mixin(value = ItemTenseiganChakraMode.RangedItem.class, remap = false)
public abstract class MixinItemTenseiganChakraModeRangedItem extends ItemJutsu.Base{
    public MixinItemTenseiganChakraModeRangedItem(ItemJutsu.JutsuEnum.Type typeIn, ItemJutsu.JutsuEnum... jutsuListIn) {
        super(typeIn, jutsuListIn);
    }

    @Inject(method = "func_77659_a", at = @At("RETURN"), cancellable = true)
    public void onItemRightClick(World world, EntityPlayer entity, EnumHand hand, CallbackInfoReturnable<ActionResult<ItemStack>> cir) {
        ItemStack headStack = entity.getItemStackFromSlot(EntityEquipmentSlot.HEAD);
        boolean hasHelmet = headStack.getItem() == ItemTenseigan.helmet;

        ItemStack dojutsu_left = DojutsuSlotHelper.getLeftDojutsu(entity);
        ItemStack dojutsu_right = DojutsuSlotHelper.getRightDojutsu(entity);
        boolean hasLeft = dojutsu_left.getItem() == ItemTenseigan.helmet;
        boolean hasRight = dojutsu_right.getItem() == ItemTenseigan.helmet;

        if (!(hasHelmet || (hasLeft && hasRight))) {
            cir.setReturnValue(new ActionResult<>(EnumActionResult.FAIL, entity.getHeldItem(hand)));
        } else {
            cir.setReturnValue(super.onItemRightClick(world, entity, hand));
        }

        cir.cancel();
    }

    /**
     * @author fuskirby
     * @reason add_dojutsu_slots
     */
    @Overwrite
    public void func_77663_a(ItemStack itemstack, World world, Entity entity, int par4, boolean par5) {
        super.onUpdate(itemstack, world, entity, par4, par5);
        if (!world.isRemote && entity instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) entity;
            if (!player.isCreative() && !player.getCooldownTracker().hasCooldown(ItemTenseiganChakraMode.block)) {

                ItemStack headSlot = player.getItemStackFromSlot(EntityEquipmentSlot.HEAD);
                boolean hasHelmet = headSlot.getItem() == ItemTenseigan.helmet;

                boolean hasLeftDojutsu = DojutsuSlotHelper.getLeftDojutsu(player).getItem() == ItemTenseigan.helmet;   // 自定义方法
                boolean hasRightDojutsu = DojutsuSlotHelper.getRightDojutsu(player).getItem() == ItemTenseigan.helmet; // 自定义方法

                if (!(hasHelmet || (hasLeftDojutsu && hasRightDojutsu))) {
                    return;
                }

                if (player.getHeldItemMainhand().equals(itemstack)) {
                    player.addPotionEffect(new PotionEffect(PotionFlight.potion, 2, 0, false, false));

                    ItemStack chestStack = player.getItemStackFromSlot(EntityEquipmentSlot.CHEST);
                    ItemStack legsStack = player.getItemStackFromSlot(EntityEquipmentSlot.LEGS);

                    if (chestStack.getItem() != ItemTenseigan.body) {
                        ItemStack newChest = ProcedureUtils.getMatchingItemStack(player, ItemTenseigan.body);
                        if (newChest == null) {
                            for (int i = 0; i < 1000; i++) {
                                Particles.spawnParticle(world, Particles.Types.SMOKE, entity.posX, entity.posY + 0.8d, entity.posZ,
                                        1, 0.0d, 0.0d, 0.0d,
                                        (itemRand.nextDouble() - 0.5d),
                                        (itemRand.nextDouble() - 0.5d),
                                        (itemRand.nextDouble() - 0.5d),
                                        0x20b5fff5, 30, 0, 0xF0, entity.getEntityId());
                            }
                            entity.world.playSound(null, entity.posX, entity.posY, entity.posZ,
                                    Objects.requireNonNull(SoundEvent.REGISTRY.getObject(new ResourceLocation("narutomod:charging_chakra"))),
                                    net.minecraft.util.SoundCategory.PLAYERS, 1.0F, 1.0F);
                            newChest = new ItemStack(ItemTenseigan.body);
                            if (itemstack.getTagCompound() != null) {
                                newChest.setItemDamage(itemstack.getTagCompound().getInteger("ChestArmorDamage"));
                            }
                        }
                        ProcedureUtils.swapItemToSlot(player, EntityEquipmentSlot.CHEST, newChest);
                    } else if (itemstack.getTagCompound() != null && chestStack.getItemDamage() != itemstack.getTagCompound().getInteger("ChestArmorDamage")) {
                        itemstack.getTagCompound().setInteger("ChestArmorDamage", chestStack.getItemDamage());
                    }

                    if (legsStack.getItem() != ItemTenseigan.legs) {
                        ItemStack newLegs = ProcedureUtils.getMatchingItemStack(player, ItemTenseigan.legs);
                        if (newLegs == null) {
                            newLegs = new ItemStack(ItemTenseigan.legs);
                            if (itemstack.getTagCompound() != null) {
                                newLegs.setItemDamage(itemstack.getTagCompound().getInteger("LegArmorDamage"));
                            }
                        }
                        ProcedureUtils.swapItemToSlot(player, EntityEquipmentSlot.LEGS, newLegs);
                    } else if (itemstack.getTagCompound() != null && legsStack.getItemDamage() != itemstack.getTagCompound().getInteger("LegArmorDamage")) {
                        itemstack.getTagCompound().setInteger("LegArmorDamage", legsStack.getItemDamage());
                    }

                    if (chestStack.getItem() == ItemTenseigan.body && legsStack.getItem() == ItemTenseigan.legs
                            && (chestStack.getItemDamage() >= chestStack.getMaxDamage()
                            || legsStack.getItemDamage() >= legsStack.getMaxDamage())) {
                        player.getCooldownTracker().setCooldown(ItemTenseiganChakraMode.block, 3600);
                        chestStack.shrink(1);
                        legsStack.shrink(1);
                        if (itemstack.getTagCompound() != null) {
                            itemstack.getTagCompound().setInteger("ChestArmorDamage", 0);
                            itemstack.getTagCompound().setInteger("LegArmorDamage", 0);
                        }
                    }
                }
            }
        }
    }
}
