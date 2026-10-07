package com.fuskirby.dojutsu_slot.util;

import com.fuskirby.dojutsu_slot.client.render.ModelCompositeHelmet;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.HashMap;
import java.util.Map;

public class ItemCompositeHelmet extends ItemArmor {

    @SideOnly(Side.CLIENT)
    private static final class ModelCacheHolder {
        static final Map<String, ModelCompositeHelmet> CACHE = new HashMap<>();
    }

    public ItemCompositeHelmet() {
        super(ArmorMaterial.IRON, 2, EntityEquipmentSlot.HEAD);
        this.setUnlocalizedName("composite_helmet");
        this.setRegistryName("composite_helmet");
    }

    @SideOnly(Side.CLIENT)
    public static void invalidateCache(EntityPlayer player) {
        String uuidStr = player.getUniqueID().toString();
        ModelCacheHolder.CACHE.entrySet().removeIf(entry -> entry.getKey().contains(uuidStr));
    }

    @SideOnly(Side.CLIENT)
    private ModelBiped restrictToHead(ModelBiped model) {
        if (model == null) return null;
        model.bipedHead.showModel = true;
        model.bipedHeadwear.showModel = true;
        model.bipedBody.showModel = false;
        model.bipedRightArm.showModel = false;
        model.bipedLeftArm.showModel = false;
        model.bipedRightLeg.showModel = false;
        model.bipedLeftLeg.showModel = false;
        return model;
    }

    @SideOnly(Side.CLIENT)
    private int getArmorColor(ItemStack stack) {
        if (!stack.isEmpty() && stack.getItem() instanceof ItemArmor) {
            return ((ItemArmor) stack.getItem()).getColor(stack);
        }
        return -1;
    }

    @SideOnly(Side.CLIENT)
    private ModelBiped buildModelFromStack(EntityPlayer player, ItemStack stack) {
        if (stack.isEmpty() || !(stack.getItem() instanceof ItemArmor)) {
            return new ModelBiped(0.5F);
        }
        ItemArmor armor = (ItemArmor) stack.getItem();
        ModelBiped model = armor.getArmorModel(player, stack, EntityEquipmentSlot.HEAD, new ModelBiped(0.5F));
        if (model == null) {
            model = new ModelBiped(0.5F);
        }
        return restrictToHead(model);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public ModelBiped getArmorModel(EntityLivingBase entityLiving, ItemStack itemStack, EntityEquipmentSlot armorSlot, ModelBiped _default) {
        if (!(entityLiving instanceof EntityPlayer)) {
            return super.getArmorModel(entityLiving, itemStack, armorSlot, _default);
        }
        EntityPlayer player = (EntityPlayer) entityLiving;
        NBTTagCompound nbt = itemStack.getTagCompound();
        if (nbt == null) {
            return super.getArmorModel(entityLiving, itemStack, armorSlot, _default);
        }

        StringBuilder keyBuilder = new StringBuilder();
        keyBuilder.append(player.getUniqueID().toString());
        keyBuilder.append("|").append(nbt.toString());

        ItemStack mainHand = player.getHeldItemMainhand();
        keyBuilder.append("|mainhand:");
        if (mainHand.isEmpty()) {
            keyBuilder.append("empty");
        } else {
            keyBuilder.append(mainHand.getItem().getRegistryName());
            if (mainHand.hasTagCompound()) {
                if (mainHand.getTagCompound() != null) {
                    keyBuilder.append("#").append(mainHand.getTagCompound().hashCode());
                }
            }
            boolean onCooldown = player.getCooldownTracker().getCooldown(mainHand.getItem(), 0.0F) > 0.0F;
            keyBuilder.append("|cd:").append(onCooldown);
        }

        String cacheKey = keyBuilder.toString();
        ModelCompositeHelmet model = ModelCacheHolder.CACHE.get(cacheKey);
        if (model == null) {
            ItemStack helmetStack = ItemStack.EMPTY;
            if (nbt.hasKey("HelmetStack")) {
                helmetStack = new ItemStack(nbt.getCompoundTag("HelmetStack"));
            }
            ModelBiped helmetModel = buildModelFromStack(player, helmetStack);
            int helmetColor = getArmorColor(helmetStack);
            ResourceLocation helmetTex = nbt.hasKey("HelmetTexture") ? new ResourceLocation(nbt.getString("HelmetTexture")) : null;

            ItemStack leftStack = ItemStack.EMPTY;
            ItemStack rightStack = ItemStack.EMPTY;

            if (nbt.hasKey("LeftDojutsuStack")) {
                leftStack = new ItemStack(nbt.getCompoundTag("LeftDojutsuStack"));
            }
            if (nbt.hasKey("RightDojutsuStack")) {
                rightStack = new ItemStack(nbt.getCompoundTag("RightDojutsuStack"));
            }

            if (leftStack.isEmpty() && rightStack.isEmpty() && nbt.hasKey("DojutsuStacks")) {
                NBTTagList list = nbt.getTagList("DojutsuStacks", 10);
                for (int i = 0; i < list.tagCount(); i++) {
                    NBTTagCompound tag = list.getCompoundTagAt(i);
                    int slot = tag.getInteger("Slot");
                    ItemStack stack = new ItemStack(tag);
                    if (slot == 0) {
                        rightStack = stack;
                    } else if (slot == 1) {
                        leftStack = stack;
                    }
                }
            }

            ModelBiped leftModel = buildModelFromStack(player, leftStack);
            ModelBiped rightModel = buildModelFromStack(player, rightStack);

            int leftColor = -1;
            int rightColor = -1;

            ResourceLocation leftTex = nbt.hasKey("LeftDojutsuTexture") ? new ResourceLocation(nbt.getString("LeftDojutsuTexture")) : null;
            ResourceLocation rightTex = nbt.hasKey("RightDojutsuTexture") ? new ResourceLocation(nbt.getString("RightDojutsuTexture")) : null;

            model = new ModelCompositeHelmet(helmetModel, leftModel, rightModel,
                    helmetTex, leftTex, rightTex,
                    helmetColor, leftColor, rightColor);
            ModelCacheHolder.CACHE.put(cacheKey, model);
        }
        return model;
    }

    @Override
    public String getArmorTexture(ItemStack stack, Entity entity, EntityEquipmentSlot slot, String type) {
        return "dojutsu_slot:textures/models/armor/gunmu.png";
    }
}