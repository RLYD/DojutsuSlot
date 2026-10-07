package com.fuskirby.dojutsu_slot.mixin.sharingan;

import com.fuskirby.dojutsu_slot.util.DojutsuAddonHelper;
import com.fuskirby.dojutsu_slot.util.DojutsuSlotHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.items.ItemHandlerHelper;
import net.narutomod.item.ItemDojutsu;
import net.narutomod.procedure.ProcedureSharinganHelmetTickEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

@Mixin(value = ProcedureSharinganHelmetTickEvent.class, remap = false)
public class MixinProcedureSharinganHelmetTickEvent {
    @Unique private static final ThreadLocal<Map<String, Object>> currentDependencies = new ThreadLocal<>();
    @Unique private static final ThreadLocal<NBTTagCompound> dojutsu_state = new ThreadLocal<>();

    @Inject(method = "executeProcedure", at = @At("HEAD"))
    private static void captureDependencies(Map<String, Object> dependencies, CallbackInfo ci) {
        currentDependencies.set(dependencies);
    }

    @Inject(method = "executeProcedure", at = @At("RETURN"))
    private static void clearDependencies(Map<String, Object> dependencies, CallbackInfo ci) {
        currentDependencies.remove();
    }

//    @Inject(
//            method = "executeProcedure",
//            at = @At(value = "INVOKE", target = "Lnet/minecraftforge/items/ItemHandlerHelper;giveItemToPlayer(Lnet/minecraft/entity/player/EntityPlayer;Lnet/minecraft/item/ItemStack;)V",
//            ordinal = 1),
//            locals = LocalCapture.CAPTURE_FAILHARD)
//    private static void onGetItem(Map<String, Object> dependencies, ItemStack mangekyo, CallbackInfo ci) {
//
//    }

//    @ModifyVariable(
//        method = "executeProcedure",
//            at = @At(value = "INVOKE", target = "Lnet/minecraftforge/items/ItemHandlerHelper;giveItemToPlayer(Lnet/minecraft/entity/player/EntityPlayer;Lnet/minecraft/item/ItemStack;)V",
//            ordinal = 1),
//        index = 1)
//    private static ItemStack onGiveMangekyo(ItemStack stack) {
//        ItemStack itemStack = (ItemStack)currentDependencies.get().get("itemstack");
//        if (itemStack.getTagCompound().hasKey("dojutsu_state")) {
//            dojutsu_state = itemStack.getTagCompound().getCompoundTag("dojutsu_state");
//            if (dojutsu_state.getBoolean("left")) {
//                setDojutsuState(stack, "left");
//            } else if (dojutsu_state.getBoolean("right")) {
//                setDojutsuState(stack, "right");
//            }
//        }
//        return stack;
//    }

    @Redirect(method = "executeProcedure",
            at = @At(value = "INVOKE", target = "Lnet/minecraftforge/items/ItemHandlerHelper;giveItemToPlayer(Lnet/minecraft/entity/player/EntityPlayer;Lnet/minecraft/item/ItemStack;)V",
                    ordinal = 0))
    private static void redirectGiveItemToPlayer(EntityPlayer player, ItemStack stack) {
        ItemStack itemStack = (ItemStack) currentDependencies.get().get("itemstack");
        String state = DojutsuSlotHelper.getDojutsuState(itemStack);
        DojutsuSlotHelper.setDojutsuState(itemStack, state);
        ItemHandlerHelper.giveItemToPlayer(player, stack);
    }

    @Redirect(method = "executeProcedure",
            at = @At(value = "INVOKE", target = "Lnet/minecraftforge/items/ItemHandlerHelper;giveItemToPlayer(Lnet/minecraft/entity/player/EntityPlayer;Lnet/minecraft/item/ItemStack;)V",
                    ordinal = 1))
    private static void redirectGiveMangekyoToPlayer(EntityPlayer player, ItemStack stack) {
        ItemStack itemStack = (ItemStack) currentDependencies.get().get("itemstack");
        stack = createMangekyoFromNbt(itemStack, stack);

        NBTTagCompound tag = itemStack.getTagCompound();
        if (tag != null && tag.hasKey("dojutsu_state")) {
            dojutsu_state.set(tag);
            String side = dojutsu_state.get().getString("dojutsu_state");
            if ("left".equals(side)) {
                DojutsuSlotHelper.setDojutsuState(stack, "left");
            } else if ("right".equals(side)) {
                DojutsuSlotHelper.setDojutsuState(stack, "right");
            }
        }

        ((ItemDojutsu.Base) stack.getItem()).copyOwner(stack, itemStack);
        DojutsuAddonHelper.setAwakener(stack, player.getUniqueID(), player.getName());

        ItemHandlerHelper.giveItemToPlayer(player, stack);
    }

    @Inject(method = "executeProcedure",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/entity/player/InventoryPlayer;func_174925_a(Lnet/minecraft/item/Item;IILnet/minecraft/nbt/NBTTagCompound;)I",
                    shift = At.Shift.AFTER)
    )
    private static void ClearDojutsuSlotMatchingItems(Map<String, Object> dependencies, CallbackInfo ci) {
        if (Loader.isModLoaded("dojutsu_addon")) return;

        String side = dojutsu_state.get().getString("dojutsu_state");
        EntityPlayer player = (EntityPlayer) dependencies.get("player");

        if ("left".equals(side)) {
            DojutsuSlotHelper.getLeftDojutsu(player).shrink(1);
        } else if ("right".equals(side)) {
            DojutsuSlotHelper.getRightDojutsu(player).shrink(1);
        }
    }

    @Unique
    private static ItemStack createMangekyoFromNbt(ItemStack sharingan , ItemStack originalMangekyo) {
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
