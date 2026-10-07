package com.fuskirby.dojutsu_slot.mixin.addonrbnl.procedure;

import com.fuskirby.dojutsu_slot.util.DojutsuSlotHelper;
import net.addonrbnl.procedure.ProcedureTsukoyomi;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.NonNullList;
import net.minecraftforge.fml.common.FMLCommonHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = ProcedureTsukoyomi.class, remap = false)
public class MixinProcedureTsukoyomi {

    @Redirect(
            method = "onServerTick",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/util/NonNullList;get(I)Ljava/lang/Object;"),
            remap = false
    )
    private Object redirectGetHelmet(NonNullList<ItemStack> instance, int index) {
        if (index == 3) {
            EntityPlayer player = findPlayerByArmorList(instance);
            if (player != null) {
                ItemStack custom = DojutsuSlotHelper.selectDojutsuForJutsu(player);
                if (custom != null) {
                    return custom;
                }
            }
        }
        return instance.get(index);
    }

    @Unique
    private EntityPlayer findPlayerByArmorList(NonNullList<ItemStack> armorList) {
        MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
        for (EntityPlayer player : server.getPlayerList().getPlayers()) {
            if (player.inventory.armorInventory == armorList) {
                return player;
            }
        }
        return null;
    }
}
