package com.fuskirby.dojutsu_slot.client.gui;

import java.util.Set;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.fml.client.IModGuiFactory;

public class GuiFactoryDojutsu implements IModGuiFactory
{

    @Override
    public GuiScreen createConfigGui(GuiScreen arg0)
    {
        return new GuiConfigDojutsu(arg0);
    }

    @Override
    public boolean hasConfigGui()
    {
        return true;
    }

    @Override
    public void initialize(Minecraft arg0)
    {
    }

    @Override
    public Set<RuntimeOptionCategoryElement> runtimeGuiCategories()
    {
        return null;
    }

}
