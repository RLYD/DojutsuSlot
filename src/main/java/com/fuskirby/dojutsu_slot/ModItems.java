package com.fuskirby.dojutsu_slot;

import com.fuskirby.dojutsu_slot.util.ItemCompositeHelmet;
import net.minecraft.item.Item;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod.EventBusSubscriber(modid = DojutsuSlot.MODID)
public class ModItems {

    public static ItemCompositeHelmet COMPOSITE_HELMET = new ItemCompositeHelmet();

    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Item> event) {
        event.getRegistry().register(COMPOSITE_HELMET);
    }
}
