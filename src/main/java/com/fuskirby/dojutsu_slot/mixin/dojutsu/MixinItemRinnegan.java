package com.fuskirby.dojutsu_slot.mixin.dojutsu;

import com.fuskirby.dojutsu_slot.DojutsuSlot;
import com.fuskirby.dojutsu_slot.util.DojutsuSlotHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.narutomod.ElementsNarutomodMod;
import net.narutomod.item.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static net.narutomod.item.ItemRinnegan.helmet;
import static net.narutomod.item.ItemRinnegan.isRinnesharinganActivated;

@Mixin(value = ItemRinnegan.class, remap = false)
public abstract class MixinItemRinnegan extends ElementsNarutomodMod.ModElement {

    @Shadow public static final double SHINRATENSEI_CHAKRA_USAGE = 10d;
    @Shadow public static final double BANSHOTENIN_CHAKRA_USAGE = 0.5F;
    @Shadow public static final double CHIBAKUTENSEI_CHAKRA_USAGE = 5000d;
    @Shadow public static final double NARAKAPATH_CHAKRA_USAGE = 100d;
    @Shadow public static final double PRETAPATH_CHAKRA_USAGE = 10d;
    @Shadow public static final double ANIMALPATH_CHAKRA_USAGE = 200d;
    @Shadow public static final double OUTERPATH_CHAKRA_USAGE = 2000d;
    @Shadow public static final double TENGAISHINSEI_CHAKRA_USAGE = 5000d;

    public MixinItemRinnegan(ElementsNarutomodMod elements, int sortid) {
        super(elements, sortid);
    }

    @Inject(method = "getShinratenseiChakraUsage", at = @At("RETURN"), cancellable = true)
    private static void getShinratenseiChakraUsage(EntityLivingBase entity, CallbackInfoReturnable<Double> cir) {
        if (entity instanceof EntityPlayer) {
            ItemStack stack = DojutsuSlotHelper.selectRinneganTomoeForJutsu((EntityPlayer) entity);
            if (stack.getItem() instanceof ItemRinnegan.Base) {
                boolean isOwner = ((ItemDojutsu.Base) stack.getItem()).isOwner(stack, entity);
                double usage = isOwner ? SHINRATENSEI_CHAKRA_USAGE : SHINRATENSEI_CHAKRA_USAGE * 2;
                cir.setReturnValue(usage);
            } else {
                cir.setReturnValue(Double.MAX_VALUE * 0.001d);
            }
        }
    }

    @Inject(method = "getBanshoteninChakraUsage", at = @At("RETURN"), cancellable = true)
    private static void getBanshoteninChakraUsage(EntityLivingBase entity, CallbackInfoReturnable<Double> cir) {
        if (entity instanceof EntityPlayer) {
            ItemStack stack = DojutsuSlotHelper.selectRinneganTomoeForJutsu((EntityPlayer) entity);
            if (stack.getItem() instanceof ItemRinnegan.Base) {
                boolean isOwner = ((ItemDojutsu.Base) stack.getItem()).isOwner(stack, entity);
                double usage = isOwner ? BANSHOTENIN_CHAKRA_USAGE : BANSHOTENIN_CHAKRA_USAGE * 2;
                cir.setReturnValue(usage);
            } else {
                cir.setReturnValue(Double.MAX_VALUE * 0.001d);
            }
        }
    }

    @Inject(method = "getChibaukutenseiChakraUsage", at = @At("RETURN"), cancellable = true)
    private static void getChibaukutenseiChakraUsage(EntityLivingBase entity, CallbackInfoReturnable<Double> cir) {
        if (entity instanceof EntityPlayer) {
            ItemStack stack = DojutsuSlotHelper.selectRinneganTomoeForJutsu((EntityPlayer) entity);
            if (stack.getItem() instanceof ItemRinnegan.Base) {
                boolean isOwner = ((ItemDojutsu.Base) helmet).isOwner(stack, entity);
                double usage = isOwner ? CHIBAKUTENSEI_CHAKRA_USAGE : CHIBAKUTENSEI_CHAKRA_USAGE * 2;
                cir.setReturnValue(usage);
            } else {
                cir.setReturnValue(Double.MAX_VALUE * 0.001d);
            }
        }
    }

    @Inject(method = "getNarakaPathChakraUsage", at = @At("RETURN"), cancellable = true)
    private static void getNarakaPathChakraUsage(EntityLivingBase entity, CallbackInfoReturnable<Double> cir) {
        if (entity instanceof EntityPlayer) {
            ItemStack stack = DojutsuSlotHelper.selectRinneganTomoeForJutsu((EntityPlayer) entity);
            if (stack.getItem() instanceof ItemRinnegan.Base) {
                boolean isOwner = ((ItemDojutsu.Base) helmet).isOwner(stack, entity);
                double usage = isOwner ? NARAKAPATH_CHAKRA_USAGE : NARAKAPATH_CHAKRA_USAGE * 2;
                cir.setReturnValue(usage);
            } else {
                cir.setReturnValue(Double.MAX_VALUE * 0.001d);
            }
        }
    }

    @Inject(method = "getPretaPathChakraUsage", at = @At("RETURN"), cancellable = true)
    private static void getPretaPathChakraUsage(EntityLivingBase entity, CallbackInfoReturnable<Double> cir) {
        if (entity instanceof EntityPlayer) {
            ItemStack stack = DojutsuSlotHelper.selectRinneganTomoeForJutsu((EntityPlayer) entity);
            if (stack.getItem() instanceof ItemRinnegan.Base) {
                boolean isOwner = ((ItemDojutsu.Base) helmet).isOwner(stack, entity);
                double usage = isOwner ? PRETAPATH_CHAKRA_USAGE : PRETAPATH_CHAKRA_USAGE * 2;
                cir.setReturnValue(usage);
            } else {
                cir.setReturnValue(Double.MAX_VALUE * 0.001d);
            }
        }
    }

    @Inject(method = "getAnimalPathChakraUsage", at = @At("RETURN"), cancellable = true)
    private static void getAnimalPathChakraUsage(EntityLivingBase entity, CallbackInfoReturnable<Double> cir) {
        if (entity instanceof EntityPlayer) {
            ItemStack stack = DojutsuSlotHelper.selectRinneganTomoeForJutsu((EntityPlayer) entity);
            if (stack.getItem() instanceof ItemRinnegan.Base) {
                boolean isOwner = ((ItemDojutsu.Base) helmet).isOwner(stack, entity);
                double usage = isOwner ? ANIMALPATH_CHAKRA_USAGE : ANIMALPATH_CHAKRA_USAGE * 2;
                cir.setReturnValue(usage);
            } else {
                cir.setReturnValue(Double.MAX_VALUE * 0.001d);
            }
        }
    }

    @Inject(method = "getOuterPathChakraUsage", at = @At("RETURN"), cancellable = true)
    private static void getOuterPathChakraUsage(EntityLivingBase entity, CallbackInfoReturnable<Double> cir) {
        if (entity instanceof EntityPlayer) {
            ItemStack stack = DojutsuSlotHelper.selectRinneganTomoeForJutsu((EntityPlayer) entity);
            if (stack.getItem() instanceof ItemRinnegan.Base) {
                boolean isOwner = ((ItemDojutsu.Base) helmet).isOwner(stack, entity);
                double usage = isOwner ? OUTERPATH_CHAKRA_USAGE : OUTERPATH_CHAKRA_USAGE * 2;
                cir.setReturnValue(usage);
            } else {
                cir.setReturnValue(Double.MAX_VALUE * 0.001d);
            }
        }
    }

    @Inject(method = "getTengaishinseiChakraUsage", at = @At("RETURN"), cancellable = true)
    private static void getTengaishinseiChakraUsage(EntityLivingBase entity, CallbackInfoReturnable<Double> cir) {
        if (entity instanceof EntityPlayer) {
            ItemStack stack = DojutsuSlotHelper.selectDojutsuForJutsu((EntityPlayer) entity);
            if (stack.getItem() instanceof ItemRinnegan.Base) {
                boolean isOwner = ((ItemDojutsu.Base) helmet).isOwner(stack, entity);
                double usage = isOwner ? TENGAISHINSEI_CHAKRA_USAGE : TENGAISHINSEI_CHAKRA_USAGE * 2;
                cir.setReturnValue(usage);
            } else {
                cir.setReturnValue(Double.MAX_VALUE * 0.001d);
            }
        }
    }

    @Inject(method = "wearingRinnegan", at = @At("RETURN"), cancellable = true)
    private static void wearingRinnegan(EntityLivingBase player, CallbackInfoReturnable<Boolean> cir){
        if (player instanceof EntityPlayer) {
            IInventory dojutsuSlot = (IInventory) DojutsuSlot.invMan.getDojutsuSlotInventory(player.getUniqueID());

            ItemStack slot0 = dojutsuSlot.getStackInSlot(0);
            ItemStack slot1 = dojutsuSlot.getStackInSlot(1);
            ItemStack slot2 = dojutsuSlot.getStackInSlot(2);
            ItemStack slot3 = dojutsuSlot.getStackInSlot(3);

            Item rinnegantomoe = ForgeRegistries.ITEMS.getValue(new ResourceLocation("dojutsu_addon", "rinnegan_tomoe_helmet"));

            if (player.getItemStackFromSlot(EntityEquipmentSlot.HEAD).getItem() == helmet
                    || slot0.getItem() == helmet || slot1.getItem() == helmet
                    || slot2.getItem() == helmet || slot3.getItem() == helmet
                    || slot2.getItem() == rinnegantomoe || slot3.getItem() == rinnegantomoe) {
                cir.setReturnValue(true);
            }
        }
    }

    @Inject(method = "wearingRinnesharingan", at = @At("RETURN"), cancellable = true)
    private static void wearingRinnesharingan(EntityLivingBase player, CallbackInfoReturnable<Boolean> cir){
        if (player instanceof EntityPlayer) {
            IInventory dojutsuSlot = (IInventory) DojutsuSlot.invMan.getDojutsuSlotInventory(player.getUniqueID());

            ItemStack helmetstack = player.getItemStackFromSlot(EntityEquipmentSlot.HEAD);
            ItemStack dojutsu_left = dojutsuSlot.getStackInSlot(0);
            ItemStack dojutsu_right = dojutsuSlot.getStackInSlot(1);

            ItemStack rinnegantomoe_left = dojutsuSlot.getStackInSlot(2);
            ItemStack rinnegantomoe_right = dojutsuSlot.getStackInSlot(3);

            boolean has_rinnegan = helmetstack.getItem() instanceof ItemRinnegan.Base
                    || (dojutsu_left.getItem() instanceof ItemRinnegan.Base || rinnegantomoe_left.getItem() instanceof ItemRinnegan.Base)
                    || (dojutsu_right.getItem() instanceof ItemRinnegan.Base || rinnegantomoe_right.getItem() instanceof ItemRinnegan.Base);

            if (has_rinnegan && (isRinnesharinganActivated(helmetstack)
                    || isRinnesharinganActivated(dojutsu_left) || isRinnesharinganActivated(dojutsu_right)
                    || isRinnesharinganActivated(rinnegantomoe_left) || isRinnesharinganActivated(rinnegantomoe_right))) {
                cir.setReturnValue(true);
            }
        }
    }

//    @Inject(method = "wearingRinnesharingan", at = @At("RETURN"), cancellable = true)
//    private static void wearingRinnesharingan(EntityLivingBase player, CallbackInfoReturnable<Boolean> cir){
//
//        ItemStack helmetstack = player.getItemStackFromSlot(EntityEquipmentSlot.HEAD);
//        ItemStack dojutsu_left  = DojutsuSlot.invMan.getDojutsuSlotInventory(player.getUniqueID()).getStackInSlot(1);
//        ItemStack dojutsu_right = DojutsuSlot.invMan.getDojutsuSlotInventory(player.getUniqueID()).getStackInSlot(0);
//
//        ResourceLocation helmetReg = helmetstack.getItem().getRegistryName();
//        ResourceLocation leftReg   = dojutsu_left.getItem().getRegistryName();
//        ResourceLocation rightReg  = dojutsu_right.getItem().getRegistryName();
//
//        ResourceLocation rinneganReg = new ResourceLocation("narutomod", "rinneganhelmet");
//        ResourceLocation tenseiganReg = new ResourceLocation("narutomod", "tenseiganhelmet");
//        ResourceLocation tomoeRinneganReg = new ResourceLocation("dojutsu_addon", "rinnegan_tomoe_helmet");
//
//        boolean has_rinnegan = rinneganReg.equals(helmetReg) || rinneganReg.equals(leftReg) || rinneganReg.equals(rightReg);
//        boolean has_tenseigan = tenseiganReg.equals(helmetReg) || tenseiganReg.equals(leftReg) || tenseiganReg.equals(rightReg);
//        boolean has_tomoe_rinnegan = tomoeRinneganReg.equals(helmetReg) || tomoeRinneganReg.equals(leftReg) || tomoeRinneganReg.equals(rightReg);
//
//        if (((has_rinnegan || has_tomoe_rinnegan) || has_tenseigan) && (isRinnesharinganActivated(helmetstack)
//                || isRinnesharinganActivated(dojutsu_left) || isRinnesharinganActivated(dojutsu_right))) {
//            cir.setReturnValue(true);
//        }
//    }
}
