package com.fuskirby.dojutsu_slot;

import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;

@Config(modid = "dojutsu_slot")
public class ModConfigs
{
    public static boolean DojutsuSlotGuiButton_Hidden = false;
    public static int DojutsuSlotGuiButton_Left = 65;
    public static int DojutsuSlotGuiButton_Top = 67;
    public static boolean DojutsuSlotKeepThroughDeath = false;
    public static boolean DojutsuSlotDisableRecipeBook = false;

    private static Configuration lastConfig;

    public static Configuration getLastConfig()
    {
        return lastConfig;
    }

    public static void loadConfigs(Configuration config)
    {
        lastConfig = config;

        Property prop = config.get(Configuration.CATEGORY_GENERAL, "DojutsuSlotGuiButton_Hidden", false);
        prop.setComment("Hide DojutsuSlotGuiButton? (this has no effect on the server side)");
        DojutsuSlotGuiButton_Hidden = prop.getBoolean();

        prop = config.get(Configuration.CATEGORY_GENERAL, "DojutsuSlotGuiButton_Left", 65);
        prop.setComment("The distance from left of the inventory gui for DojutsuSlotGuiButton. (this has no effect on the server side)");
        DojutsuSlotGuiButton_Left = prop.getInt();

        prop = config.get(Configuration.CATEGORY_GENERAL, "DojutsuSlotGuiButton_Top", 67);
        prop.setComment("The distance from top of the inventory gui for DojutsuSlotGuiButton. (this has no effect on the server side)");
        DojutsuSlotGuiButton_Top = prop.getInt();

        prop = config.get(Configuration.CATEGORY_GENERAL, "DojutsuSlotKeepThroughDeath", false);
        prop.setComment("If you want to keep your dojutsu slots through death, change this to true. (if you are on a server, only the setting on the server side will take effect)");
        DojutsuSlotKeepThroughDeath = prop.getBoolean();

        prop = config.get(Configuration.CATEGORY_GENERAL, "DojutsuSlotDisableRecipeBook", false);
        prop.setComment("If you want to disable the RecipeBook in DojutsuSlotInventory, change this to true. (you need to make sure this is same as the server to avoid potential de-sync issues)");
        DojutsuSlotDisableRecipeBook = prop.getBoolean();

        if (config.hasChanged())
            config.save();
    }
}