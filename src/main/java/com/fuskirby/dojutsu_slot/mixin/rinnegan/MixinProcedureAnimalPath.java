package com.fuskirby.dojutsu_slot.mixin.rinnegan;

import com.fuskirby.dojutsu_slot.DojutsuSlotContext;
import com.fuskirby.dojutsu_slot.util.DojutsuSlotHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.narutomod.Chakra;
import net.narutomod.Particles;
import net.narutomod.entity.EntityGiantDog2h;
import net.narutomod.item.ItemRinnegan;
import net.narutomod.procedure.ProcedureAnimalPath;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.lang.reflect.Method;
import java.util.Map;

@Mixin(value = ProcedureAnimalPath.class, remap = false)
public abstract class MixinProcedureAnimalPath {
    @Inject(method = "executeProcedure", at = @At("HEAD"), cancellable = true)
    private static void executeProcedure(Map<String, Object> dependencies, CallbackInfo ci) {
        if (dependencies.get("entity") == null || dependencies.get("world") == null) {
            return;
        }

        Entity entity = (Entity) dependencies.get("entity");
        World world = (World) dependencies.get("world");

        if (!(entity instanceof EntityPlayer)) {
            return;
        }

        EntityPlayer player = (EntityPlayer) entity;

        ci.cancel();

        boolean hasDojutsu = checkDojutsuInAnySlot(player);

        if (!hasDojutsu) {
            return;
        }

        double summonedId = findSummonedIdFromAllSlots(player);
        if (summonedId <= 0) {
            summonAnimal(player, world);
        } else {
            recallAnimal(player, world, summonedId);
        }
    }

    @Unique
    private static boolean checkDojutsuInAnySlot(EntityPlayer player) {
        ItemStack helmetStack = player.inventory.armorInventory.get(3);
        if (isDojutsuItem(helmetStack.getItem())) {
            return true;
        }

        if (isDojutsuItem(DojutsuSlotHelper.getLeftTomoeRinnegan(player).getItem())) {
            return true;
        }
        else if (isDojutsuItem(DojutsuSlotHelper.getLeftDojutsu(player).getItem())) {
            return true;
        }

        if (isDojutsuItem(DojutsuSlotHelper.getRightTomoeRinnegan(player).getItem())) {
            return true;
        }
        else if (isDojutsuItem(DojutsuSlotHelper.getRightDojutsu(player).getItem())) {
            return true;
        }

        return false;
    }

    @Unique
    private static boolean isDojutsuItem(Item item) {
        if (item == null) return false;

        String className = item.getClass().getName();

        return className.contains("ItemRinnegan") || className.contains("ItemTenseigan") || className.contains("ItemRinneganTomoe");
    }

    @Unique
    private static double findSummonedIdFromAllSlots(EntityPlayer player) {
        double summonedId = -1;

        ItemStack helmetStack = player.inventory.armorInventory.get(3);
        if (isDojutsuItem(helmetStack.getItem()) && helmetStack.hasTagCompound()) {
            summonedId = helmetStack.getTagCompound().getDouble("SummonedAnimal_id");
        }

        if (summonedId <= 0) {
            ItemStack leftTomoeRinneganStack = DojutsuSlotHelper.getLeftTomoeRinnegan(player);
            ItemStack leftDojutsuStack = DojutsuSlotHelper.getLeftDojutsu(player);
            if (isDojutsuItem(leftTomoeRinneganStack.getItem()) && leftTomoeRinneganStack.hasTagCompound()) {
                if (leftTomoeRinneganStack.getTagCompound() != null) {
                    summonedId = leftTomoeRinneganStack.getTagCompound().getDouble("SummonedAnimal_id");
                }
            }
            if (isDojutsuItem(leftDojutsuStack.getItem()) && leftDojutsuStack.hasTagCompound()) {
                if (leftDojutsuStack.getTagCompound() != null) {
                    summonedId = leftDojutsuStack.getTagCompound().getDouble("SummonedAnimal_id");
                }
            }
        }

        if (summonedId <= 0) {
            ItemStack rightTomoeRinneganStack = DojutsuSlotHelper.getRightTomoeRinnegan(player);
            ItemStack rightDojutsuStack = DojutsuSlotHelper.getRightDojutsu(player);
            if (isDojutsuItem(rightTomoeRinneganStack.getItem()) && rightTomoeRinneganStack.hasTagCompound()) {
                if (rightTomoeRinneganStack.getTagCompound() != null) {
                    summonedId = rightTomoeRinneganStack.getTagCompound().getDouble("SummonedAnimal_id");
                }
            }
            if (isDojutsuItem(rightDojutsuStack.getItem()) && rightDojutsuStack.hasTagCompound()) {
                if (rightDojutsuStack.getTagCompound() != null) {
                    summonedId = rightDojutsuStack.getTagCompound().getDouble("SummonedAnimal_id");
                }
            }
        }

        return summonedId;
    }

    @Unique
    private static void summonAnimal(EntityPlayer player, World world) {
        if (!world.isRemote) {
            double chakraCost = getAnimalPathChakraUsage(player);

            if (Chakra.pathway(player).consume(chakraCost)) {
                player.swingArm(EnumHand.MAIN_HAND);

                world.playSound(null, player.posX, player.posY, player.posZ,
                        (net.minecraft.util.SoundEvent) net.minecraft.util.SoundEvent.REGISTRY
                                .getObject(new ResourceLocation("narutomod:kuchiyosenojutsu")),
                        SoundCategory.NEUTRAL, 2.0f, 0.8f);

                EntityGiantDog2h.EntityCustom entityToSpawn = new EntityGiantDog2h.EntityCustom(player);
                double x = entityToSpawn.posX;
                double z = entityToSpawn.posZ;
                double h = player.height;

                Particles.spawnParticle(world, Particles.Types.SEAL_FORMULA, x,
                        entityToSpawn.posY + 0.015d, z, 1, 0d, 0d, 0d, 0d, 0d, 0d, 200, 0, 60);

                if (world instanceof WorldServer) {
                    ((WorldServer) world).spawnParticle(EnumParticleTypes.EXPLOSION_HUGE,
                            x, player.posY + (h / 2), z, 300, 4, h, 4, 1, new int[0]);
                }

                world.spawnEntity(entityToSpawn);
                double summonedId = entityToSpawn.getEntityId();

                storeSummonedIdToAllDojutsuSlots(player, summonedId);
            } else {
                Chakra.pathway(player).warningDisplay();
            }
        }
    }

    @Unique
    private static void recallAnimal(EntityPlayer player, World world, double summonedId) {
        if (!world.isRemote) {
            clearSummonedIdFromAllDojutsuSlots(player);

            Entity summonedEntity = world.getEntityByID((int) summonedId);
            if (summonedEntity instanceof EntityGiantDog2h.EntityCustom) {
                if (world instanceof WorldServer) {
                    ((WorldServer) world).spawnParticle(EnumParticleTypes.EXPLOSION_HUGE,
                            summonedEntity.posX, summonedEntity.posY + 15,
                            summonedEntity.posZ, 200, 4, 15, 4, 1, new int[0]);
                }
                summonedEntity.setDead();
            }
        }
    }

    @Unique
    private static double getAnimalPathChakraUsage(EntityLivingBase entity) {
        try {
            EntityPlayer player = (EntityPlayer)entity;
            Item rinnegantomoe = ForgeRegistries.ITEMS.getValue(new ResourceLocation("dojutsu_addon", "rinnegan_tomoe_helmet"));

            Class<?> targetClass = Class.forName("com.fuskirby.dojutsu_addon.item.ItemRinneganTomoe");
            Method method = targetClass.getDeclaredMethod("getAnimalPathChakraUsage");

            if (DojutsuSlotHelper.selectRinneganTomoeForJutsu(player).getItem() == rinnegantomoe) {
                return (double) method.invoke(player);
            }
            else if (DojutsuSlotContext.getDojutsuFromCurrentSlot(player).getItem() == ItemRinnegan.helmet) {
                return ItemRinnegan.getAnimalPathChakraUsage(player);
            }
        } catch (Exception e) {
            return 1000.0;
        }
        return 0;
    }

    @Unique
    private static void storeSummonedIdToAllDojutsuSlots(EntityPlayer player, double summonedId) {
        // helmet slot
        ItemStack helmetStack = player.inventory.armorInventory.get(3);
        if (isDojutsuItem(helmetStack.getItem())) {
            storeSummonedIdToSlot(helmetStack, summonedId);
        }

        // left dojutsu slots
        ItemStack leftTomoeRinneganStack = DojutsuSlotHelper.getLeftTomoeRinnegan(player);
        if (isDojutsuItem(leftTomoeRinneganStack.getItem())) {
            storeSummonedIdToSlot(leftTomoeRinneganStack, summonedId);
        }
        ItemStack leftDojutsuStack = DojutsuSlotHelper.getLeftDojutsu(player);
        if (isDojutsuItem(leftDojutsuStack.getItem())) {
            storeSummonedIdToSlot(leftDojutsuStack, summonedId);
        }

        // right dojutsu slots
        ItemStack rightTomoeRinneganStack = DojutsuSlotHelper.getRightTomoeRinnegan(player);
        if (isDojutsuItem(rightTomoeRinneganStack.getItem())) {
            storeSummonedIdToSlot(rightTomoeRinneganStack, summonedId);
        }
        ItemStack rightDojutsuStack = DojutsuSlotHelper.getRightDojutsu(player);
        if (isDojutsuItem(rightDojutsuStack.getItem())) {
            storeSummonedIdToSlot(rightDojutsuStack, summonedId);
        }
    }

    @Unique
    private static void clearSummonedIdFromAllDojutsuSlots(EntityPlayer player) {
        // helmet slot
        ItemStack helmetStack = player.inventory.armorInventory.get(3);
        if (isDojutsuItem(helmetStack.getItem()) && helmetStack.hasTagCompound()) {
            helmetStack.getTagCompound().setDouble("SummonedAnimal_id", 0);
        }

        // left dojutsu slots
        ItemStack leftTomoeRinneganStack = DojutsuSlotHelper.getLeftTomoeRinnegan(player);
        if (isDojutsuItem(leftTomoeRinneganStack.getItem()) && leftTomoeRinneganStack.hasTagCompound()) {
            leftTomoeRinneganStack.getTagCompound().setDouble("SummonedAnimal_id", 0);
        }
        ItemStack leftDojutsuStack = DojutsuSlotHelper.getLeftDojutsu(player);
        if (isDojutsuItem(leftDojutsuStack.getItem()) && leftDojutsuStack.hasTagCompound()) {
            leftDojutsuStack.getTagCompound().setDouble("SummonedAnimal_id", 0);
        }

        // right dojutsu slots
        ItemStack rightTomoeRinneganStack = DojutsuSlotHelper.getRightTomoeRinnegan(player);
        if (isDojutsuItem(rightTomoeRinneganStack.getItem()) && rightTomoeRinneganStack.hasTagCompound()) {
            rightTomoeRinneganStack.getTagCompound().setDouble("SummonedAnimal_id", 0);
        }
        ItemStack rightDojutsuStack = DojutsuSlotHelper.getRightDojutsu(player);
        if (isDojutsuItem(rightDojutsuStack.getItem()) && rightDojutsuStack.hasTagCompound()) {
            rightDojutsuStack.getTagCompound().setDouble("SummonedAnimal_id", 0);
        }
    }

    @Unique
    private static void storeSummonedIdToSlot(ItemStack stack, double summonedId) {
        if (!stack.hasTagCompound()) {
            stack.setTagCompound(new NBTTagCompound());
        }
        if (stack.getTagCompound() != null) {
            stack.getTagCompound().setDouble("SummonedAnimal_id", summonedId);
        }
    }
}