package com.fuskirby.dojutsu_slot.client.gui;

import com.google.common.collect.Lists;
import com.fuskirby.dojutsu_slot.ModConfigs;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.client.config.GuiConfig;

public class GuiConfigDojutsu extends GuiConfig
{

    public GuiConfigDojutsu(GuiScreen parent)
    {
        super(parent, Lists.newArrayList(new ConfigElement(ModConfigs.getLastConfig().getCategory(Configuration.CATEGORY_GENERAL)).getChildElements()), "dojutsu_slot", false, false, GuiConfig.getAbridgedConfigPath(ModConfigs.getLastConfig().getConfigFile().toString()));
    }

}
