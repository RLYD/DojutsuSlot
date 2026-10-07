package com.fuskirby.dojutsu_slot.client;

import com.fuskirby.dojutsu_slot.DojutsuSlot;
import com.fuskirby.dojutsu_slot.network.packet.PacketCustomSusanoo;
import com.fuskirby.dojutsu_slot.network.packet.PacketOpenDojutsuSlotInventory;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import org.lwjgl.input.Keyboard;

public class KeyHandler {
    public KeyBinding keyOpenDojutsuSlotInventory = new KeyBinding("dojutsu_slot.key.opendojutsuslotinventory", Keyboard.KEY_NONE, "key.categories.dojutsu_slot");

    public KeyBinding keyCustomSusanoo = new KeyBinding("dojutsu_slot.key.custom_susanoo", Keyboard.KEY_U, "key.categories.dojutsu_slot");

    public KeyHandler()
    {
        ClientRegistry.registerKeyBinding(keyOpenDojutsuSlotInventory);
        ClientRegistry.registerKeyBinding(keyCustomSusanoo);
    }

    @SubscribeEvent
    public void handleEvent(ClientTickEvent event)
    {
        if (event.phase == Phase.START)
        {
            if (keyOpenDojutsuSlotInventory.isPressed() && FMLClientHandler.instance().getClient().inGameHasFocus)
                DojutsuSlot.network.sendToServer(new PacketOpenDojutsuSlotInventory());

            if (keyCustomSusanoo.isPressed() && FMLClientHandler.instance().getClient().inGameHasFocus)
                DojutsuSlot.network.sendToServer(new PacketCustomSusanoo());
        }
    }
}
