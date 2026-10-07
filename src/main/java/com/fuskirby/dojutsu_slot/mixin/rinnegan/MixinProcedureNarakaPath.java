package com.fuskirby.dojutsu_slot.mixin.rinnegan;

import com.fuskirby.dojutsu_slot.util.DojutsuSlotHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.narutomod.Chakra;
import net.narutomod.ElementsNarutomodMod;
import net.narutomod.entity.EntityKingOfHell;
import net.narutomod.item.ItemRinnegan;
import net.narutomod.procedure.ProcedureNarakaPath;
import net.narutomod.procedure.ProcedureUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;
import java.util.UUID;

@Mixin(value = ProcedureNarakaPath.class, remap = false)
public abstract class MixinProcedureNarakaPath extends ElementsNarutomodMod.ModElement {
    public MixinProcedureNarakaPath(ElementsNarutomodMod elements, int sortid) {
        super(elements, sortid);
    }

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

        ((EntityLivingBase) player).swingArm(net.minecraft.util.EnumHand.MAIN_HAND);

        if (!world.isRemote) {
            boolean hasDojutsu = false;

            ItemStack helmetStack = player.getItemStackFromSlot(EntityEquipmentSlot.HEAD);
            if (isRinneTenseiganHelmet(helmetStack)) {
                hasDojutsu = true;
            }

            ItemStack leftDojutsuStack = DojutsuSlotHelper.getLeftDojutsu(player);
            ItemStack leftTomoeRinnegan = DojutsuSlotHelper.getLeftTomoeRinnegan(player);

            if (isRinneTenseiganHelmet(leftTomoeRinnegan)) {
                hasDojutsu = true;
            }
            else if (isRinneTenseiganHelmet(leftDojutsuStack)) {
                hasDojutsu = true;
            }

            ItemStack rightDojutsuStack = DojutsuSlotHelper.getRightDojutsu(player);
            ItemStack rightTomoeRinnegan = DojutsuSlotHelper.getRightTomoeRinnegan(player);

            if (isRinneTenseiganHelmet(rightTomoeRinnegan)) {
                hasDojutsu = true;
            }
            else if (isRinneTenseiganHelmet(rightDojutsuStack)) {
                hasDojutsu = true;
            }

            if (!hasDojutsu) {
                return;
            }

            UUID entityUUID = null;

            if (helmetStack.hasTagCompound()) {
                entityUUID = ProcedureUtils.getUniqueId(helmetStack, "KoH_id");
            }

            if (entityUUID == null && leftTomoeRinnegan.hasTagCompound()) {
                entityUUID = ProcedureUtils.getUniqueId(leftTomoeRinnegan, "KoH_id");
            }
            if (entityUUID == null && leftDojutsuStack.hasTagCompound()) {
                entityUUID = ProcedureUtils.getUniqueId(leftDojutsuStack, "KoH_id");
            }

            if (entityUUID == null && rightTomoeRinnegan.hasTagCompound()) {
                entityUUID = ProcedureUtils.getUniqueId(rightTomoeRinnegan, "KoH_id");
            }
            if (entityUUID == null && rightDojutsuStack.hasTagCompound()) {
                entityUUID = ProcedureUtils.getUniqueId(rightDojutsuStack, "KoH_id");
            }

            if (entityUUID == null) {
                if (Chakra.pathway(player).consume(ItemRinnegan.getNarakaPathChakraUsage(player))) {
                    EntityKingOfHell.EntityCustom entityToSpawn = new EntityKingOfHell.EntityCustom(player);
                    world.spawnEntity(entityToSpawn);

                    storeEntityUUIDToAllDojutsuSlots(player, entityToSpawn.getUniqueID(),
                            helmetStack, leftDojutsuStack, rightDojutsuStack, leftTomoeRinnegan, rightTomoeRinnegan);
                }
            } else {
                Entity entitySpawned = ((WorldServer) world).getEntityFromUuid(entityUUID);
                if (entitySpawned instanceof EntityKingOfHell.EntityCustom) {
                    ((EntityLivingBase) entitySpawned).setHealth(0.0F);
                }

                clearEntityUUIDFromAllDojutsuSlots(player, helmetStack, leftDojutsuStack, rightDojutsuStack, leftTomoeRinnegan, rightTomoeRinnegan);
            }
        }
    }

    @Unique
    private static boolean isRinneTenseiganHelmet(ItemStack stack) {
        if (stack.isEmpty()) {
            return false;
        }

        String className = stack.getItem().getClass().getName();
        return className.contains("ItemRinnegan") || className.contains("ItemTenseigan");
    }

    @Unique
    private static void storeEntityUUIDToAllDojutsuSlots(EntityPlayer player, UUID entityUUID,
                                                         ItemStack helmetStack, ItemStack leftDojutsuStack, ItemStack rightDojutsuStack,
                                                         ItemStack leftRinneganTomoe, ItemStack rightRinneganTomoe) {
        // store in the helmet slot
        if (isRinneTenseiganHelmet(helmetStack)) {
            if (!helmetStack.hasTagCompound()) {
                helmetStack.setTagCompound(new net.minecraft.nbt.NBTTagCompound());
                helmetStack.getTagCompound().setUniqueId("KoH_id", entityUUID);
            }
        }

        // store in the left dojutsu slots
        if (isRinneTenseiganHelmet(leftRinneganTomoe)) {
            if (!leftRinneganTomoe.hasTagCompound()) {
                leftRinneganTomoe.setTagCompound(new net.minecraft.nbt.NBTTagCompound());
                leftRinneganTomoe.getTagCompound().setUniqueId("KoH_id", entityUUID);
            }
        }
        if (isRinneTenseiganHelmet(leftDojutsuStack)) {
            if (!leftDojutsuStack.hasTagCompound()) {
                leftDojutsuStack.setTagCompound(new net.minecraft.nbt.NBTTagCompound());
                leftDojutsuStack.getTagCompound().setUniqueId("KoH_id", entityUUID);
            }
        }

        // store in the right dojutsu slots
        if (isRinneTenseiganHelmet(rightRinneganTomoe)) {
            if (!rightRinneganTomoe.hasTagCompound()) {
                rightRinneganTomoe.setTagCompound(new net.minecraft.nbt.NBTTagCompound());
                rightRinneganTomoe.getTagCompound().setUniqueId("KoH_id", entityUUID);
            }
        }
        if (isRinneTenseiganHelmet(rightDojutsuStack)) {
            if (!rightDojutsuStack.hasTagCompound()) {
                rightDojutsuStack.setTagCompound(new net.minecraft.nbt.NBTTagCompound());
                rightDojutsuStack.getTagCompound().setUniqueId("KoH_id", entityUUID);
            }
        }
    }

    @Unique
    private static void clearEntityUUIDFromAllDojutsuSlots(EntityPlayer player,
                                                           ItemStack helmetStack, ItemStack leftDojutsuStack, ItemStack rightDojutsuStack,
                                                           ItemStack leftRinneganTomoe, ItemStack rightRinneganTomoe) {
        // helmet
        if (helmetStack.hasTagCompound()) {
            ProcedureUtils.removeUniqueIdTag(helmetStack, "KoH_id");
        }

        // left dojutsu
        if (leftRinneganTomoe.hasTagCompound()) {
            ProcedureUtils.removeUniqueIdTag(leftRinneganTomoe, "KoH_id");
        }
        if (leftDojutsuStack.hasTagCompound()) {
            ProcedureUtils.removeUniqueIdTag(leftDojutsuStack, "KoH_id");
        }

        // right dojutsu
        if (rightRinneganTomoe.hasTagCompound()) {
            ProcedureUtils.removeUniqueIdTag(rightRinneganTomoe, "KoH_id");
        }
        if (rightDojutsuStack.hasTagCompound()) {
            ProcedureUtils.removeUniqueIdTag(rightDojutsuStack, "KoH_id");
        }
    }
}