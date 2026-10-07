package com.fuskirby.dojutsu_slot.mixin.rinnegan;

import com.fuskirby.dojutsu_slot.util.DojutsuSlotHelper;
import net.narutomod.procedure.ProcedureAsuraPathArmorBodyTickEvent;
import net.narutomod.item.ItemRinnegan;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.world.World;
import net.minecraft.potion.PotionEffect;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.item.ItemStack;
import net.minecraft.init.MobEffects;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import java.util.Map;

@Mixin(value = ProcedureAsuraPathArmorBodyTickEvent.class, remap = false)
public class MixinProcedureAsuraPathArmorBodyTickEvent {
    /**
     * @author RLYDFuskirby
     * @reason Modified the helmet check and added checks for the left and right dojutsu slots
     */
    @Overwrite
    public static void executeProcedure(Map<String, Object> dependencies) {
        if (dependencies.get("entity") == null) {
            System.err.println("Failed to load dependency entity for procedure AsuraPathArmorBodyTickEvent!");
            return;
        }
        if (dependencies.get("itemstack") == null) {
            System.err.println("Failed to load dependency itemstack for procedure AsuraPathArmorBodyTickEvent!");
            return;
        }
        if (dependencies.get("world") == null) {
            System.err.println("Failed to load dependency world for procedure AsuraPathArmorBodyTickEvent!");
            return;
        }

        Entity entity = (Entity) dependencies.get("entity");
        ItemStack itemstack = (ItemStack) dependencies.get("itemstack");
        World world = (World) dependencies.get("world");

        if (entity instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) entity;

            Item helmetItem = player.getItemStackFromSlot(EntityEquipmentSlot.HEAD).getItem();
            Item dojutsuLeft  = DojutsuSlotHelper.getLeftDojutsu(player).getItem();
            Item dojutsuRight = DojutsuSlotHelper.getRightDojutsu(player).getItem();

            Item tomoeRinneganLeft = DojutsuSlotHelper.getLeftTomoeRinnegan(player).getItem();
            Item tomoeRinneganRight = DojutsuSlotHelper.getRightTomoeRinnegan(player).getItem();

            if (!(helmetItem instanceof ItemRinnegan.Base
                    || (dojutsuLeft instanceof ItemRinnegan.Base || tomoeRinneganLeft instanceof ItemRinnegan.Base)
                    || (dojutsuRight instanceof ItemRinnegan.Base || tomoeRinneganRight instanceof ItemRinnegan.Base)
            )) {
                itemstack.shrink(1);
                return;
            }
        } else {
            itemstack.shrink(1);
            return;
        }
        double ticks_used = 0;
        if ((itemstack).getTagCompound() != null) {
            ticks_used = (((itemstack).hasTagCompound() ? (itemstack).getTagCompound().getDouble("ticks_used") : -1) + 1);
        }
        {
            if (!(itemstack).hasTagCompound())
                (itemstack).setTagCompound(new NBTTagCompound());
            if ((itemstack).getTagCompound() != null) {
                (itemstack).getTagCompound().setDouble("ticks_used", (ticks_used));
            }
        }

        if ((!(world.isRemote)) && (((ticks_used) % 40) == 1)) {
            ((EntityLivingBase) entity).addPotionEffect(new PotionEffect(MobEffects.STRENGTH, 41, 24, false, false));
            ((EntityLivingBase) entity).addPotionEffect(new PotionEffect(MobEffects.SPEED, 41, 16, false, false));
            ((EntityLivingBase) entity).addPotionEffect(new PotionEffect(MobEffects.HASTE, 41, 5, false, false));
            ((EntityLivingBase) entity).addPotionEffect(new PotionEffect(MobEffects.JUMP_BOOST, 41, 5, false, false));
            ((EntityLivingBase) entity).addPotionEffect(new PotionEffect(MobEffects.SATURATION, 41, 0, false, false));
        }
    }
}