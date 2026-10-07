package com.fuskirby.dojutsu_slot;

import com.fuskirby.dojutsu_slot.api.mangekyo.MangekyoPoolAPI;
import com.fuskirby.dojutsu_slot.api.texture.DojutsuTextureAPI;
import com.fuskirby.dojutsu_slot.client.*;
import com.fuskirby.dojutsu_slot.client.gui.GuiEvents;
import com.fuskirby.dojutsu_slot.client.render.PlayerRenderHandler;
import com.fuskirby.dojutsu_slot.command.CommandChangeDojutsuSlotMode;
import com.fuskirby.dojutsu_slot.command.CommandClearDojutsuInSlot;
import com.fuskirby.dojutsu_slot.command.CommandClearSusanooData;
import com.fuskirby.dojutsu_slot.command.CommandSetSusanooColor;
import com.fuskirby.dojutsu_slot.event.ArmorHandler;
import com.fuskirby.dojutsu_slot.event.DojutsuEventHandler;
import com.fuskirby.dojutsu_slot.event.PlayerDataHandler;
import com.fuskirby.dojutsu_slot.event.PlayerLoginHandler;
import com.fuskirby.dojutsu_slot.network.NetworkManager;
import com.fuskirby.dojutsu_slot.network.packet.*;
import com.fuskirby.dojutsu_slot.event.KamuiAttackHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.event.FMLServerStoppingEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import org.apache.logging.log4j.Logger;

import static net.narutomod.item.ItemRinnegan.isRinnesharinganActivated;

@Mod(modid = DojutsuSlot.MODID, name = DojutsuSlot.NAME, version = DojutsuSlot.VERSION, dependencies = "required-after:narutomod")
public class DojutsuSlot
{
    public static final String MODID = "dojutsu_slot";
    public static final String NAME = "Dojutsu Slot";
    public static final String VERSION = "1.2.2";

    private static Logger logger;
    private static Configuration config;

    @Mod.Instance("dojutsu_slot")
    public static DojutsuSlot instance;

    @SideOnly(Side.CLIENT)
    public static KeyHandler keyHandler;

    @SidedProxy(serverSide = "com.fuskirby.dojutsu_slot.InventoryManager",
            clientSide = "com.fuskirby.dojutsu_slot.client.InventoryManagerClient")
    public static InventoryManager invMan;

    public static final NetworkManager network = new NetworkManager("com|fus|slot");

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        logger = event.getModLog();
        if (event.getSide().isClient()) {
            DojutsuTextureAPI.preInit(event);
            registerDojutsuTextures();
        }
        MangekyoPoolAPI.preInit(event);
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event)
    {
        network.registerPacket(1, PacketSyncDojutsuSlot.class);
        network.registerPacket(2, PacketSyncCtrlKey.class);
        network.registerPacket(3, PacketOpenDojutsuSlotInventory.class);
        network.registerPacket(4, PacketOpenNormalInventory.class);
        network.registerPacket(5, PacketSyncMode.class);
        network.registerPacket(6, PacketOpenModeSelection.class);
        network.registerPacket(7, PacketModeSelected.class);
        network.registerPacket(8, PacketCustomSusanoo.class);
        network.registerPacket(9, PacketSyncSusanooColor.class);

        NetworkRegistry.INSTANCE.registerGuiHandler(this, new GuiHandler());

        if (event.getSide().isClient())
        {
            MinecraftForge.EVENT_BUS.register(new PlayerRenderHandler());
            MinecraftForge.EVENT_BUS.register(new PlayerRenderHandler.DojutsuSlotEffectHandler());
            MinecraftForge.EVENT_BUS.register(keyHandler = new KeyHandler());
            MinecraftForge.EVENT_BUS.register(new GuiEvents());
        }

        MinecraftForge.EVENT_BUS.register(new PlayerLoginHandler());
        MinecraftForge.EVENT_BUS.register(new DojutsuEventHandler());
        MinecraftForge.EVENT_BUS.register(new ArmorHandler());
        MinecraftForge.EVENT_BUS.register(new PlayerDataHandler());
        MinecraftForge.EVENT_BUS.register(new KamuiAttackHandler());

        MinecraftForge.EVENT_BUS.register(invMan);
    }

    @Mod.EventHandler
    public void onServerStarting(FMLServerStartingEvent event)
    {
        event.registerServerCommand(new CommandClearDojutsuInSlot());
        event.registerServerCommand(new CommandClearSusanooData());
        event.registerServerCommand(new CommandChangeDojutsuSlotMode());
        event.registerServerCommand(new CommandSetSusanooColor());
        invMan.onServerStarting();
    }

    @Mod.EventHandler
    public void onServerStopping(FMLServerStoppingEvent event)
    {
        invMan.onServerStopping();
    }

    public void registerDojutsuTextures() {
        // rinnegan
        DojutsuTextureAPI.registerDojutsuTexture(
                "narutomod", "rinneganhelmet",
                "narutomod:textures/rinneganhelmet.png",
                "dojutsu_slot:textures/models/armor/rinnegan/rinneganhelmet_left.png",
                "dojutsu_slot:textures/models/armor/rinnegan/rinneganhelmet_right.png",
                stack -> !isRinnesharinganActivated(stack)
        );

        // rinnesharingan
        DojutsuTextureAPI.registerDojutsuTexture(
                "narutomod", "rinneganhelmet",
                "narutomod:textures/rinnesharinganhelmet.png",
                "dojutsu_slot:textures/models/armor/rinnegan/rinnesharinganhelmet_left.png",
                "dojutsu_slot:textures/models/armor/rinnegan/rinnesharinganhelmet_right.png",
                stack -> isRinnesharinganActivated(stack)
        );

        // byakugan
        DojutsuTextureAPI.registerDojutsuTexture(
                "narutomod", "byakuganhelmet",
                "narutomod:textures/byakuganhelmet.png",
                "dojutsu_slot:textures/models/armor/byakugan/byakuganhelmet_left.png",
                "dojutsu_slot:textures/models/armor/byakugan/byakuganhelmet_right.png",
                stack -> !isRinnesharinganActivated(stack)
        );

        // byakurinnesharingan
        DojutsuTextureAPI.registerDojutsuTexture(
                "narutomod", "byakuganhelmet",
                "narutomod:textures/byakurinnesharingan_helmet.png",
                "dojutsu_slot:textures/models/armor/byakugan/byakurinnesharingan_helmet_left.png",
                "dojutsu_slot:textures/models/armor/byakugan/byakurinnesharingan_helmet_right.png",
                stack -> isRinnesharinganActivated(stack)
        );

        if (Loader.isModLoaded("ahznbcursemarkaddon")) {
            // baseeyes
            DojutsuTextureAPI.registerDojutsuTexture(
                    "ahznbcursemarkaddon", "eyehelmet",
                    "ahznbcursemarkaddon:textures/dojutsu/boruto/base/helmet.png",
                    "dojutsu_slot:textures/compat/ahznbcursemarkaddon/boruto/base/helmet_left.png",
                    "dojutsu_slot:textures/compat/ahznbcursemarkaddon/boruto/base/helmet_right.png",
                    stack -> !net.mcreator.ahznbcursemarkaddon.procedure.ProcedureKokuganKarma.isKarmaActive() && !net.mcreator.ahznbcursemarkaddon.procedure.ProcedureJouganKarma.isKarmaActive()
                    && !net.mcreator.ahznbcursemarkaddon.procedure.ProcedureCodeKarma.isKarmaActive() && !net.mcreator.ahznbcursemarkaddon.procedure.ProcedureJigenKarma.isKarmaActive()
            );

            DojutsuTextureAPI.registerDojutsuTexture(
                    "ahznbcursemarkaddon", "eyehelmet",
                    "ahznbcursemarkaddon:textures/dojutsu/boruto/kawaki/helmet.png",
                    "dojutsu_slot:textures/compat/ahznbcursemarkaddon/boruto/kawaki/helmet_left.png",
                    "dojutsu_slot:textures/compat/ahznbcursemarkaddon/boruto/kawaki/helmet_right.png",
                    stack -> net.mcreator.ahznbcursemarkaddon.procedure.ProcedureKokuganKarma.isKarmaActive()
            );

            DojutsuTextureAPI.registerDojutsuTexture(
                    "ahznbcursemarkaddon", "eyehelmet",
                    "ahznbcursemarkaddon:textures/dojutsu/boruto/boruto/helmet.png",
                    "dojutsu_slot:textures/compat/ahznbcursemarkaddon/boruto/boruto/helmet_left.png",
                    "dojutsu_slot:textures/compat/ahznbcursemarkaddon/boruto/boruto/helmet_right.png",
                    stack -> net.mcreator.ahznbcursemarkaddon.procedure.ProcedureJouganKarma.isKarmaActive()
            );

            DojutsuTextureAPI.registerDojutsuTexture(
                    "ahznbcursemarkaddon", "eyehelmet",
                    "ahznbcursemarkaddon:textures/dojutsu/boruto/code/helmet.png",
                    "dojutsu_slot:textures/compat/ahznbcursemarkaddon/boruto/code/helmet_left.png",
                    "dojutsu_slot:textures/compat/ahznbcursemarkaddon/boruto/code/helmet_right.png",
                    stack -> net.mcreator.ahznbcursemarkaddon.procedure.ProcedureCodeKarma.isKarmaActive()
            );

            DojutsuTextureAPI.registerDojutsuTexture(
                    "ahznbcursemarkaddon", "eyehelmet",
                    "ahznbcursemarkaddon:textures/dojutsu/boruto/jigen/helmet.png",
                    "dojutsu_slot:textures/compat/ahznbcursemarkaddon/boruto/jigen/helmet_left.png",
                    "dojutsu_slot:textures/compat/ahznbcursemarkaddon/boruto/jigen/helmet_right.png",
                    stack -> net.mcreator.ahznbcursemarkaddon.procedure.ProcedureJigenKarma.isKarmaActive()
            );

            // kingan
            DojutsuTextureAPI.registerDojutsuTexture(
                    "ahznbcursemarkaddon", "kinganhelmet",
                    "ahznbcursemarkaddon:textures/dojutsu/boruto/dourado/helmet.png",
                    "dojutsu_slot:textures/compat/ahznbcursemarkaddon/boruto/dourado/helmet_left.png",
                    "dojutsu_slot:textures/compat/ahznbcursemarkaddon/boruto/dourado/helmet_right.png",
                    stack -> !isRinnesharinganActivated(stack)
            );

            DojutsuTextureAPI.registerDojutsuTexture(
                    "ahznbcursemarkaddon", "kinganhelmet",
                    "narutomod:textures/byakurinnesharingan_helmet.png",
                    "dojutsu_slot:textures/models/armor/byakugan/byakurinnesharingan_helmet_left.png",
                    "dojutsu_slot:textures/models/armor/byakugan/byakurinnesharingan_helmet_right.png",
                    stack -> isRinnesharinganActivated(stack)
            );

            // madara_rinnegan
            DojutsuTextureAPI.registerDojutsuTexture(
                    "narutomod", "madara_rinneganhelmet",
                    "narutomod:textures/rinneganhelmet.png",
                    "dojutsu_slot:textures/models/armor/rinnegan/rinneganhelmet_left.png",
                    "dojutsu_slot:textures/models/armor/rinnegan/rinneganhelmet_right.png",
                    stack -> !isRinnesharinganActivated(stack)
            );

            DojutsuTextureAPI.registerDojutsuTexture(
                    "narutomod", "madara_rinneganhelmet",
                    "narutomod:textures/rinnesharinganhelmet.png",
                    "dojutsu_slot:textures/models/armor/rinnegan/rinnesharinganhelmet_left.png",
                    "dojutsu_slot:textures/models/armor/rinnegan/rinnesharinganhelmet_right.png",
                    stack -> isRinnesharinganActivated(stack)
            );
        }
    }
}