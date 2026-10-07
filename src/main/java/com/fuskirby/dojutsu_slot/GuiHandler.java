package com.fuskirby.dojutsu_slot;

import com.fuskirby.dojutsu_slot.client.gui.GuiDojutsuSlotInventory;
import com.fuskirby.dojutsu_slot.inventory.ContainerDojutsuSlot;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.common.network.IGuiHandler;

import java.lang.reflect.Field;

public class GuiHandler implements IGuiHandler
{
    @Override
    public Object getClientGuiElement(int id, EntityPlayer player, World world, int x, int y, int z)
    {
        if (id == 1) {
            GuiDojutsuSlotInventory newGui = new GuiDojutsuSlotInventory(new ContainerDojutsuSlot(player.inventory, DojutsuSlot.invMan.getDojutsuSlotInventoryClient(player.getUniqueID()), player));
            GuiScreen gui = FMLClientHandler.instance().getClient().currentScreen;
            if (gui instanceof GuiInventory) {
                try {
                    Field oldMouseXField = GuiInventory.class.getDeclaredField("oldMouseX");
                    Field oldMouseYField = GuiInventory.class.getDeclaredField("oldMouseY");
                    oldMouseXField.setAccessible(true);
                    oldMouseYField.setAccessible(true);

                    float oldMouseX = (float) oldMouseXField.get(gui);
                    float oldMouseY = (float) oldMouseYField.get(gui);
                    newGui.oldMouseX = oldMouseX;
                    newGui.oldMouseY = oldMouseY;
                } catch (Exception e) {
                    newGui.oldMouseX = 0;
                    newGui.oldMouseY = 0;
                }
            }
            return newGui;
        }
        return null;
    }

    @Override
    public Object getServerGuiElement(int id, EntityPlayer player, World world, int x, int y, int z)
    {
        if (id == 1) {
            return new ContainerDojutsuSlot(player.inventory, DojutsuSlot.invMan.getDojutsuSlotInventory(player.getUniqueID()), player);
        }
        return null;
    }
}