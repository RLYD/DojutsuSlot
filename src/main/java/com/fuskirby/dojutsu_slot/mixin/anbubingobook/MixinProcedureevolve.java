package com.fuskirby.dojutsu_slot.mixin.anbubingobook;

import com.fuskirby.dojutsu_slot.util.DojutsuAddonHelper;
import com.fuskirby.dojutsu_slot.util.DojutsuSlotHelper;
import net.mcreator.anbubingobook.procedure.procedureevolve;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.items.ItemHandlerHelper;
import net.narutomod.item.ItemDojutsu;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

@Mixin(value = procedureevolve.class, remap = false)
public class MixinProcedureevolve {
    @Unique private static ItemStack dojutsu = null;

    @Inject(method = "executeProcedure", at = @At("HEAD"))
    private static void getItemStack(Map<String, Object> dependencies, CallbackInfo ci) {
        if (dependencies.get("itemstack") != null) {
            dojutsu = (ItemStack) dependencies.get("itemstack");
        }
    }

    @Redirect(
            method = "executeProcedure",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraftforge/items/ItemHandlerHelper;giveItemToPlayer(Lnet/minecraft/entity/player/EntityPlayer;Lnet/minecraft/item/ItemStack;)V"
            )
    )
    private static void redirectGiveItem(EntityPlayer player, ItemStack mangekyo) {
        if (dojutsu == null || dojutsu.getTagCompound().hasKey("awakened")) return;

        if (Loader.isModLoaded("addonrbnl")) {
            Item randomItem = net.addonraba.util.KekkeiGenkai.MANGEKYO.getItem();
            mangekyo = new ItemStack(randomItem, 1);
        }
        mangekyo = createMangekyoFromNbt(dojutsu, mangekyo);

        NBTTagCompound tag = dojutsu.getTagCompound();
        if (tag != null && tag.hasKey("dojutsu_state")) {
            String side = tag.getString("dojutsu_state");
            if ("left".equals(side)) {
                DojutsuSlotHelper.setDojutsuState(mangekyo, "left");
            } else if ("right".equals(side)) {
                DojutsuSlotHelper.setDojutsuState(mangekyo, "right");
            }
        }

        ((ItemDojutsu.Base) mangekyo.getItem()).copyOwner(mangekyo, dojutsu);
        DojutsuAddonHelper.setAwakener(mangekyo, player.getUniqueID(), player.getName());

        ItemHandlerHelper.giveItemToPlayer(player, mangekyo);
    }

    @Inject(method = "executeProcedure",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraftforge/items/ItemHandlerHelper;giveItemToPlayer(Lnet/minecraft/entity/player/EntityPlayer;Lnet/minecraft/item/ItemStack;)V",
                    shift = At.Shift.AFTER)
    )
    private static void ClearDojutsuSlotMatchingItems(Map<String, Object> dependencies, CallbackInfo ci) {
        EntityPlayerMP entity = (EntityPlayerMP)dependencies.get("entity");
        if (Loader.isModLoaded("dojutsu_addon")) {
            NBTTagCompound nbt = dojutsu.getTagCompound();
            if (nbt != null && !nbt.hasKey("awakened")) {
                nbt.setBoolean("awakened", true);
                if (!nbt.hasKey(DojutsuAddonHelper.AWAKENER_UUID_TAG)) {
                    DojutsuAddonHelper.setAwakener(dojutsu, entity.getUniqueID(), entity.getName());
                }
            }
        } else {
            dojutsu.shrink(1);
        }
    }

    @Unique
    private static ItemStack createMangekyoFromNbt(ItemStack sharingan ,ItemStack originalMangekyo) {
        if (sharingan != null && !sharingan.isEmpty()) {
            NBTTagCompound tag = sharingan.getTagCompound();
            if (tag != null && tag.hasKey("mangekyo_item_id")) {
                String itemId = tag.getString("mangekyo_item_id");
                Item item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(itemId));
                if (item != null) return new ItemStack(item, 1);
            }
        }
        return originalMangekyo; // fallback
    }
}
