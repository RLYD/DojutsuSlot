package com.fuskirby.dojutsu_slot.client.render;

import java.util.*;
import java.util.concurrent.TimeUnit;
import com.fuskirby.dojutsu_slot.ModItems;
import com.fuskirby.dojutsu_slot.api.texture.DojutsuTextureAPI;
import com.fuskirby.dojutsu_slot.util.ItemCompositeHelmet;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.fuskirby.dojutsu_slot.DojutsuSlot;
import com.fuskirby.dojutsu_slot.inventory.InventoryDojutsuSlot;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.event.entity.living.LivingEquipmentChangeEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import javax.annotation.ParametersAreNonnullByDefault;

public class PlayerRenderHandler
{
    private final Map<UUID, Integer> lastMainhandState = new HashMap<>();

    private final Cache<String, ResourceLocation> textureCache = CacheBuilder.newBuilder()
            .maximumSize(100)
            .expireAfterAccess(10, TimeUnit.MINUTES)
            .build();

    private static class CachedInventory
    {
        NonNullList<ItemStack> stacks;
        int state;

        CachedInventory(int size)
        {
            stacks = NonNullList.withSize(size, ItemStack.EMPTY);
            state = 0;
        }
    }

    private final LoadingCache<EntityPlayer, CachedInventory> cache = CacheBuilder.newBuilder()
            .expireAfterAccess(60, TimeUnit.SECONDS)
            .build(new CacheLoader<EntityPlayer, CachedInventory>() {
                @Override
                @ParametersAreNonnullByDefault
                public CachedInventory load(EntityPlayer owner) {
                    return new CachedInventory(owner.inventory.armorInventory.size());
                }
            });

    private int computeMainhandStateHash(EntityPlayer player) {
        ItemStack mainhand = player.getHeldItemMainhand();
        if (mainhand.isEmpty()) {
            return 0;
        }
        int hash = Objects.requireNonNull(mainhand.getItem().getRegistryName()).hashCode();
        hash = hash * 31 + mainhand.getMetadata();
        if (mainhand.hasTagCompound()) {
            if (mainhand.getTagCompound() != null) {
                hash = hash * 31 + mainhand.getTagCompound().hashCode();
            }
        }
        boolean onCooldown = player.getCooldownTracker().getCooldown(mainhand.getItem(), 0) > 0;
        hash = hash * 31 + (onCooldown ? 1 : 0);
        return hash;
    }

    @SubscribeEvent(priority = EventPriority.LOWEST, receiveCanceled = true)
    public void handleCanceledEvent(RenderPlayerEvent.Pre event) {
        if (!event.isCanceled())
            return;

        CachedInventory cachedInv = cache.getUnchecked(event.getEntityPlayer());
        NonNullList<ItemStack> cachedArmor = cachedInv.stacks;
        NonNullList<ItemStack> armor = event.getEntityPlayer().inventory.armorInventory;

        if (armor.size() > cachedArmor.size()) {
            cache.invalidate(event.getEntityPlayer());
            return;
        }

        if (cachedInv.state != 0) {
            for (int i = 0; i < armor.size(); i++)
                armor.set(i, cachedArmor.get(i));
            cachedInv.state = 0;
        }
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    public void handleEvent(RenderPlayerEvent.Post event) {
        CachedInventory cachedInv = cache.getUnchecked(event.getEntityPlayer());
        NonNullList<ItemStack> cachedArmor = cachedInv.stacks;
        NonNullList<ItemStack> armor = event.getEntityPlayer().inventory.armorInventory;

        if (armor.size() > cachedArmor.size()) {
            cache.invalidate(event.getEntityPlayer());
            return;
        }

        if (cachedInv.state != 0) {
            for (int i = 0; i < armor.size(); i++)
                armor.set(i, cachedArmor.get(i));
            cachedInv.state = 0;
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGH, receiveCanceled = true)
    public void handleEvent(RenderPlayerEvent.Pre event) {
        EntityPlayer player = event.getEntityPlayer();
        UUID uuid = player.getUniqueID();

        int currentState = computeMainhandStateHash(player);
        Integer lastState = lastMainhandState.get(uuid);
        if (lastState == null || lastState != currentState) {
            lastMainhandState.put(uuid, currentState);
            ItemCompositeHelmet.invalidateCache(player);
            textureCache.invalidateAll();
        }
        CachedInventory cachedInv = cache.getUnchecked(player);
        NonNullList<ItemStack> cachedArmor = cachedInv.stacks;
        InventoryDojutsuSlot dojutsuSlot = DojutsuSlot.invMan.getDojutsuSlotInventoryClient(player.getUniqueID());
        NonNullList<ItemStack> armor = player.inventory.armorInventory;

        if (armor.size() > cachedArmor.size()) {
            cache.invalidate(player);
            return;
        }

        if (cachedInv.state != 0) {
            for (int i = 0; i < armor.size(); i++)
                armor.set(i, cachedArmor.get(i));
            cachedInv.state = 0;
        }

        for (int i = 0; i < armor.size(); i++)
            cachedArmor.set(i, armor.get(i));

        ItemStack helmetStack = armor.get(3);

        ItemStack slot0 = dojutsuSlot.getStackInSlot(0);
        ItemStack slot1 = dojutsuSlot.getStackInSlot(1);
        ItemStack slot2 = dojutsuSlot.getStackInSlot(2);
        ItemStack slot3 = dojutsuSlot.getStackInSlot(3);

        ItemStack leftEyeStack = !slot3.isEmpty() ? slot3 : slot1;
        ItemStack rightEyeStack = !slot2.isEmpty() ? slot2 : slot0;

        if (!rightEyeStack.isEmpty() || !leftEyeStack.isEmpty()) {
            ResourceLocation leftTex = getDojutsuTexture(player, leftEyeStack, 1);
            ResourceLocation rightTex = getDojutsuTexture(player, rightEyeStack, 2);

            String helmetTexPath = null;
            if (!helmetStack.isEmpty() && helmetStack.getItem() instanceof ItemArmor) {
                ItemArmor helmetArmor = (ItemArmor) helmetStack.getItem();
                helmetTexPath = helmetArmor.getArmorTexture(helmetStack, player, EntityEquipmentSlot.HEAD, null);
            }

            ItemStack compositeHelmet = new ItemStack(ModItems.COMPOSITE_HELMET);
            NBTTagCompound nbt = new NBTTagCompound();

            if (!helmetStack.isEmpty()) {
                nbt.setTag("HelmetStack", helmetStack.serializeNBT());
            }

            NBTTagList dojutsuList = new NBTTagList();
            for (int i = 0; i < dojutsuSlot.getSizeInventory(); i++) {
                ItemStack stack = dojutsuSlot.getStackInSlot(i);
                if (!stack.isEmpty()) {
                    NBTTagCompound tag = stack.serializeNBT();
                    tag.setInteger("Slot", i);
                    dojutsuList.appendTag(tag);
                }
            }
            nbt.setTag("DojutsuStacks", dojutsuList);

            if (helmetTexPath != null) {
                nbt.setString("HelmetTexture", helmetTexPath);
            }
            if (leftTex != null) {
                nbt.setString("LeftDojutsuTexture", leftTex.toString());
            }
            if (rightTex != null) {
                nbt.setString("RightDojutsuTexture", rightTex.toString());
            }

            if (!leftEyeStack.isEmpty()) {
                nbt.setTag("LeftDojutsuStack", leftEyeStack.serializeNBT());
            }
            if (!rightEyeStack.isEmpty()) {
                nbt.setTag("RightDojutsuStack", rightEyeStack.serializeNBT());
            }

            compositeHelmet.setTagCompound(nbt);
            armor.set(3, compositeHelmet);
        }

        cachedInv.state = 1;
    }

    private ResourceLocation getDojutsuTexture(EntityPlayer player, ItemStack stack, int slotType) {
        if (stack.isEmpty()) return null;
        ResourceLocation tex = DojutsuTextureAPI.getTextureForSlot(stack, slotType);
        if (tex != null) return tex;
        if (stack.getItem() instanceof ItemArmor) {
            ItemArmor armor = (ItemArmor) stack.getItem();
            String path = armor.getArmorTexture(stack, player, EntityEquipmentSlot.HEAD, null);
            if (path != null) return new ResourceLocation(path);
        }
        return null;
    }

    public static class DojutsuSlotEffectHandler {

        @SubscribeEvent
        public void onPlayerUpdate(LivingUpdateEvent event) {
            if (event.getEntityLiving() instanceof EntityPlayer) {
                EntityPlayer player = (EntityPlayer) event.getEntityLiving();

                InventoryDojutsuSlot inv = DojutsuSlot.invMan.getDojutsuSlotInventoryClient(player.getUniqueID());

                for (int i = 0; i < inv.getSizeInventory(); i++) {
                    ItemStack dojutsuStack = inv.getStackInSlot(i);
                    if (!dojutsuStack.isEmpty()) {
                        applyDojutsuEffects(player, dojutsuStack, i);
                    }
                }
            }
        }

        private void applyDojutsuEffects(EntityPlayer player, ItemStack dojutsuStack, int slot) {
            if (dojutsuStack.getItem() instanceof net.minecraft.item.ItemArmor) {
                (dojutsuStack.getItem()).onArmorTick(player.world, player, dojutsuStack);
            }

            if (isNarutomodDojutsu(dojutsuStack)) {
                applyNarutomodDojutsuEffects(player, dojutsuStack, slot);
            }

            applyPotionEffects(player, dojutsuStack);
        }

        private boolean isNarutomodDojutsu(ItemStack stack) {
            try {
                Class<?> dojutsuClass = Class.forName("net.narutomod.item.ItemDojutsu$Base");
                return dojutsuClass.isInstance(stack.getItem());
            } catch (ClassNotFoundException e) {
                return false;
            }
        }

        private void applyNarutomodDojutsuEffects(EntityPlayer player, ItemStack dojutsuStack, int slot) {
            try {
                java.lang.reflect.Method onArmorTick = dojutsuStack.getItem().getClass()
                        .getMethod("onArmorTick", net.minecraft.world.World.class,
                                EntityPlayer.class, ItemStack.class);
                onArmorTick.invoke(dojutsuStack.getItem(), player.world, player, dojutsuStack);
            } catch (Exception ignored) {}
        }

        private void applyPotionEffects(EntityPlayer player, ItemStack dojutsuStack) {
            if (dojutsuStack.hasTagCompound()) {
                net.minecraft.nbt.NBTTagCompound nbt = dojutsuStack.getTagCompound();
                if (nbt != null && nbt.hasKey("PotionEffects")) {
                    NBTTagList effects = nbt.getTagList("PotionEffects", 10);
                    for (int i = 0; i < effects.tagCount(); i++) {
                        NBTTagCompound effectNbt = effects.getCompoundTagAt(i);
                        PotionEffect effect = PotionEffect.readCustomPotionEffectFromNBT(effectNbt);
                        player.addPotionEffect(new PotionEffect(effect));
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public void onEquipmentChange(LivingEquipmentChangeEvent event) {
        if (event.getSlot() == EntityEquipmentSlot.MAINHAND && event.getEntityLiving().world.isRemote) {
            if (event.getEntityLiving() instanceof EntityPlayer) {
                EntityPlayer player = (EntityPlayer) event.getEntityLiving();
                ItemCompositeHelmet.invalidateCache(player);
                textureCache.invalidateAll();
            }
        }
    }
}