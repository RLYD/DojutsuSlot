package com.fuskirby.dojutsu_slot.client.gui;

import com.fuskirby.dojutsu_slot.DojutsuSlot;
import com.fuskirby.dojutsu_slot.ModConfigs;
import com.fuskirby.dojutsu_slot.network.packet.PacketOpenDojutsuSlotInventory;
import com.fuskirby.dojutsu_slot.network.packet.PacketOpenNormalInventory;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.lang.reflect.Field;

public class GuiEvents
{
    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public void guiPostAction(GuiScreenEvent.ActionPerformedEvent.Post event)
    {
        if (event.getGui() instanceof GuiInventory || event.getGui() instanceof GuiDojutsuSlotInventory)
        {
            GuiContainer gui = (GuiContainer) event.getGui();

            int guiLeft = gui.getGuiLeft();
            int guiTop = gui.getGuiTop();

            for (GuiButton t : event.getButtonList())
            {
                if (t.id == 418) {
                    t.x = guiLeft + ModConfigs.DojutsuSlotGuiButton_Left;
                    t.y = guiTop + ModConfigs.DojutsuSlotGuiButton_Top;
                }
            }

            if (event.getButton().id == 418)
            {
                if (gui instanceof GuiDojutsuSlotInventory)
                {
                    GuiInventory newGui = new GuiInventory(gui.mc.player);
                    float oldMouseX = ((GuiDojutsuSlotInventory) gui).oldMouseX;
                    float oldMouseY = ((GuiDojutsuSlotInventory) gui).oldMouseY;

                    try {
                        Field oldMouseXField = GuiInventory.class.getDeclaredField("oldMouseX");
                        Field oldMouseYField = GuiInventory.class.getDeclaredField("oldMouseY");
                        oldMouseXField.setAccessible(true);
                        oldMouseYField.setAccessible(true);
                        oldMouseXField.set(newGui, oldMouseX);
                        oldMouseYField.set(newGui, oldMouseY);
                    } catch (Exception ignored) {}

                    gui.mc.displayGuiScreen(newGui);
                    DojutsuSlot.network.sendToServer(new PacketOpenNormalInventory());
                }
                else
                {
                    DojutsuSlot.network.sendToServer(new PacketOpenDojutsuSlotInventory());
                }
            }
        }
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public void guiPostInit(GuiScreenEvent.InitGuiEvent.Post event)
    {
        if (event.getGui() instanceof GuiInventory || event.getGui() instanceof GuiDojutsuSlotInventory)
        {
            GuiContainer gui = (GuiContainer) event.getGui();
            int guiLeft = gui.getGuiLeft();
            int guiTop = gui.getGuiTop();

            if (!ModConfigs.DojutsuSlotGuiButton_Hidden) {
                event.getButtonList().add(new GuiDojutsuSlotButton(418, guiLeft + ModConfigs.DojutsuSlotGuiButton_Left, guiTop + ModConfigs.DojutsuSlotGuiButton_Top, 10, 10, event.getGui() instanceof GuiDojutsuSlotInventory ? "dojutsu_slot.gui.buttonnormal" : "dojutsu_slot.gui.buttondojutsu"));
            }
        }
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public void onConfigChangedEvent(ConfigChangedEvent.OnConfigChangedEvent event)
    {
        if ("dojutsu_slot".equals(event.getModID()))
            ModConfigs.loadConfigs(ModConfigs.getLastConfig());
    }
}