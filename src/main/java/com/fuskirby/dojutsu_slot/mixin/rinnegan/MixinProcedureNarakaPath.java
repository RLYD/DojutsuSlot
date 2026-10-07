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

        if (!(entity instanceof EntityLivingBase)) {
            return;
        }

        EntityLivingBase living = (EntityLivingBase) entity;

        ci.cancel();

        living.swingArm(net.minecraft.util.EnumHand.MAIN_HAND);

        if (!world.isRemote) {
            boolean isPlayer = living instanceof EntityPlayer;
            boolean hasDojutsu = false;

            ItemStack helmetStack = living.getItemStackFromSlot(EntityEquipmentSlot.HEAD);
            if (isRinneganBase(helmetStack)) {
                hasDojutsu = true;
            }

            if (isPlayer) {
                EntityPlayer player = (EntityPlayer) living;

                if (isRinneganBase(DojutsuSlotHelper.getLeftTomoeRinnegan(player))) {
                    hasDojutsu = true;
                }
                else if (isRinneganBase(DojutsuSlotHelper.getLeftDojutsu(player))) {
                    hasDojutsu = true;
                }

                if (isRinneganBase(DojutsuSlotHelper.getRightTomoeRinnegan(player))) {
                    hasDojutsu = true;
                }
                else if (isRinneganBase(DojutsuSlotHelper.getRightDojutsu(player))) {
                    hasDojutsu = true;
                }
            }

            if (!hasDojutsu) {
                return;
            }

            UUID entityUUID = null;

            if (helmetStack.hasTagCompound()) {
                entityUUID = ProcedureUtils.getUniqueId(helmetStack, "KoH_id");
            }

            if (isPlayer && entityUUID == null) {
                EntityPlayer player = (EntityPlayer) living;

                ItemStack leftDojutsuStack = DojutsuSlotHelper.getLeftDojutsu(player);
                ItemStack rightDojutsuStack = DojutsuSlotHelper.getRightDojutsu(player);

                ItemStack leftTomoeRinnegan = DojutsuSlotHelper.getLeftTomoeRinnegan(player);
                ItemStack rightTomoeRinnegan = DojutsuSlotHelper.getRightTomoeRinnegan(player);

                if (leftTomoeRinnegan.hasTagCompound()) {
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
            }

            if (entityUUID == null) {
                double chakraburn = ItemRinnegan.getNarakaPathChakraUsage(living);
                if (Chakra.pathway(living).consume(chakraburn)) {
                    EntityKingOfHell.EntityCustom entityToSpawn = new EntityKingOfHell.EntityCustom(living, chakraburn);
                    world.spawnEntity(entityToSpawn);

                    if (isPlayer) {
                        EntityPlayer player = (EntityPlayer) living;

                        ItemStack leftDojutsuStack = DojutsuSlotHelper.getLeftDojutsu(player);
                        ItemStack rightDojutsuStack = DojutsuSlotHelper.getRightDojutsu(player);
                        ItemStack leftTomoeRinnegan = DojutsuSlotHelper.getLeftTomoeRinnegan(player);
                        ItemStack rightTomoeRinnegan = DojutsuSlotHelper.getRightTomoeRinnegan(player);

                        storeEntityUUIDToAllDojutsuSlots(helmetStack, leftDojutsuStack, rightDojutsuStack, leftTomoeRinnegan, rightTomoeRinnegan, entityToSpawn.getUniqueID());
                    } else {
                        storeEntityUUIDToAllDojutsuSlots(helmetStack, ItemStack.EMPTY, ItemStack.EMPTY, ItemStack.EMPTY, ItemStack.EMPTY, entityToSpawn.getUniqueID());
                    }
                }
            } else {
                Entity entitySpawned = ((WorldServer) world).getEntityFromUuid(entityUUID);
                if (entitySpawned instanceof EntityKingOfHell.EntityCustom) {
                    ((EntityLivingBase) entitySpawned).setHealth(0.0F);
                }

                if (isPlayer) {
                    EntityPlayer player = (EntityPlayer) living;

                    ItemStack leftDojutsuStack = DojutsuSlotHelper.getLeftDojutsu(player);
                    ItemStack rightDojutsuStack = DojutsuSlotHelper.getRightDojutsu(player);
                    ItemStack leftTomoeRinnegan = DojutsuSlotHelper.getLeftTomoeRinnegan(player);
                    ItemStack rightTomoeRinnegan = DojutsuSlotHelper.getRightTomoeRinnegan(player);

                    clearEntityUUIDFromAllDojutsuSlots(helmetStack, leftDojutsuStack, rightDojutsuStack, leftTomoeRinnegan, rightTomoeRinnegan);
                } else {
                    clearEntityUUIDFromAllDojutsuSlots(helmetStack, ItemStack.EMPTY, ItemStack.EMPTY, ItemStack.EMPTY, ItemStack.EMPTY);
                }
            }
        }
    }

    @Unique
    private static boolean isRinneganBase(ItemStack stack) {
        if (stack.isEmpty()) {
            return false;
        }

        return (stack.getItem() instanceof net.narutomod.item.ItemRinnegan.Base);
    }

    @Unique
    private static void storeEntityUUIDToAllDojutsuSlots(ItemStack helmetStack, ItemStack leftDojutsuStack,
                                                         ItemStack rightDojutsuStack, ItemStack leftTomoeRinnegan,
                                                         ItemStack rightTomoeRinnegan, UUID entityUUID) {
        // store in the helmet slot
        if (isRinneganBase(helmetStack)) {
            if (!helmetStack.hasTagCompound()) {
                helmetStack.setTagCompound(new net.minecraft.nbt.NBTTagCompound());
                helmetStack.getTagCompound().setUniqueId("KoH_id", entityUUID);
            }
        }

        // store in the left dojutsu slots
        if (isRinneganBase(leftTomoeRinnegan)) {
            if (!leftTomoeRinnegan.hasTagCompound()) {
                leftTomoeRinnegan.setTagCompound(new net.minecraft.nbt.NBTTagCompound());
                leftTomoeRinnegan.getTagCompound().setUniqueId("KoH_id", entityUUID);
            }
        }
        if (isRinneganBase(leftDojutsuStack)) {
            if (!leftDojutsuStack.hasTagCompound()) {
                leftDojutsuStack.setTagCompound(new net.minecraft.nbt.NBTTagCompound());
                leftDojutsuStack.getTagCompound().setUniqueId("KoH_id", entityUUID);
            }
        }

        // store in the right dojutsu slots
        if (isRinneganBase(rightTomoeRinnegan)) {
            if (!rightTomoeRinnegan.hasTagCompound()) {
                rightTomoeRinnegan.setTagCompound(new net.minecraft.nbt.NBTTagCompound());
                rightTomoeRinnegan.getTagCompound().setUniqueId("KoH_id", entityUUID);
            }
        }
        if (isRinneganBase(rightDojutsuStack)) {
            if (!rightDojutsuStack.hasTagCompound()) {
                rightDojutsuStack.setTagCompound(new net.minecraft.nbt.NBTTagCompound());
                rightDojutsuStack.getTagCompound().setUniqueId("KoH_id", entityUUID);
            }
        }
    }

    @Unique
    private static void clearEntityUUIDFromAllDojutsuSlots(ItemStack helmetStack,
                                                           ItemStack leftDojutsuStack, ItemStack rightDojutsuStack,
                                                           ItemStack leftTomoeRinnegan, ItemStack rightTomoeRinnegan) {
        // helmet
        if (helmetStack.hasTagCompound()) {
            ProcedureUtils.removeUniqueIdTag(helmetStack, "KoH_id");
        }

        // left dojutsu
        if (leftTomoeRinnegan.hasTagCompound()) {
            ProcedureUtils.removeUniqueIdTag(leftTomoeRinnegan, "KoH_id");
        }
        if (leftDojutsuStack.hasTagCompound()) {
            ProcedureUtils.removeUniqueIdTag(leftDojutsuStack, "KoH_id");
        }

        // right dojutsu
        if (rightTomoeRinnegan.hasTagCompound()) {
            ProcedureUtils.removeUniqueIdTag(rightTomoeRinnegan, "KoH_id");
        }
        if (rightDojutsuStack.hasTagCompound()) {
            ProcedureUtils.removeUniqueIdTag(rightDojutsuStack, "KoH_id");
        }
    }
}