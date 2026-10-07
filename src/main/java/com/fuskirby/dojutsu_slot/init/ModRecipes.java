package com.fuskirby.dojutsu_slot.init;

import com.fuskirby.dojutsu_slot.DojutsuSlot;
import com.fuskirby.dojutsu_slot.crafting.RecipeRepairWithNbtCheck;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.narutomod.item.ItemSharingan;

@Mod.EventBusSubscriber(modid = DojutsuSlot.MODID)
public class ModRecipes {

    public static final IRecipe REPAIR_SHARINGAN =
            new RecipeRepairWithNbtCheck(ItemSharingan.helmet, "awakened");

    @SubscribeEvent
    public static void registerRecipes(RegistryEvent.Register<IRecipe> event) {
        event.getRegistry().register(
                REPAIR_SHARINGAN.setRegistryName(new ResourceLocation(DojutsuSlot.MODID, "repair_sword_no_unrepairable"))
        );
    }
}