package com.fuskirby.dojutsu_slot;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.UUID;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.fuskirby.dojutsu_slot.api.event.DojutsuSlotDeathDrops;
import com.fuskirby.dojutsu_slot.inventory.InventoryDojutsuSlot;
import com.fuskirby.dojutsu_slot.network.packet.PacketSyncDojutsuSlot;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerDropsEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedOutEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.gameevent.TickEvent.PlayerTickEvent;

public class InventoryManager
{

    LoadingCache<UUID, InventoryDojutsuSlot> cache = CacheBuilder.newBuilder().build(new CacheLoader<UUID, InventoryDojutsuSlot>()
    {

        @Override
        public InventoryDojutsuSlot load(UUID owner) throws Exception
        {
            InventoryDojutsuSlot inv = new InventoryDojutsuSlot();

            try
            {
                forceLoad(owner, inv);
            }
            catch (IOException e)
            {
                System.err.println("Error loading Dojutsu data file: " + e.getMessage());
                e.printStackTrace();
                inv = new InventoryDojutsuSlot();
            }

            return inv;
        }

    });

    void forceLoad(UUID uuid, InventoryDojutsuSlot inv) throws IOException
    {
        try
        {
            inv.readFromNBT(CompressedStreamTools.readCompressed(new FileInputStream(getDataFile(uuid))));
        }
        catch (FileNotFoundException ignored)
        {
        }
    }

    void forceSave(UUID uuid, InventoryDojutsuSlot inv) throws IOException
    {
        NBTTagCompound compound = new NBTTagCompound();
        inv.writeToNBT(compound);
        CompressedStreamTools.writeCompressed(compound, new FileOutputStream(getDataFile(uuid)));
    }

    public InventoryDojutsuSlot getDojutsuSlotInventory(UUID uuid)
    {
        return cache.getUnchecked(uuid);
    }

    public InventoryDojutsuSlot getDojutsuSlotInventoryClient(UUID uuid)
    {
        throw new UnsupportedOperationException();
    }

    File getDataFile(UUID uuid)
    {
        return new File(new File(getSavesDirectory(), "playerdata"), uuid + ".dojutsuslot");
    }

    File getSavesDirectory()
    {
        if (FMLCommonHandler.instance().getSide().isClient()) {
            MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
            if (server != null) {
                return server.getWorld(0).getSaveHandler().getWorldDirectory();
            } else {
                File gameDir = Minecraft.getMinecraft().mcDataDir;
                return new File(gameDir, "saves");
            }
        } else {
            MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
            if (server != null) {
                return server.getFile(server.getFolderName());
            } else {
                return new File(".");
            }
        }
    }

    @SubscribeEvent
    public void handleEvent(PlayerDropsEvent event)
    {
        if (event.getEntityPlayer() instanceof EntityPlayerMP && !event.getEntityPlayer().world.isRemote && !event.getEntityPlayer().world.getGameRules().getBoolean("keepInventory"))
        {
            if (ModConfigs.DojutsuSlotKeepThroughDeath)
                return;
            InventoryDojutsuSlot inv = getDojutsuSlotInventory(event.getEntityPlayer().getUniqueID());
            if (MinecraftForge.EVENT_BUS.post(new DojutsuSlotDeathDrops(event.getEntityPlayer(), inv.getStacks())))
                return;
            for (int i = 0; i < inv.getSizeInventory(); i++)
            {
                ItemStack stack = inv.getStackInSlot(i);
                if (!stack.isEmpty())
                {
                    EntityItem ent = new EntityItem(event.getEntityPlayer().world, event.getEntityPlayer().posX, event.getEntityPlayer().posY + event.getEntityPlayer().getEyeHeight(), event.getEntityPlayer().posZ, stack.copy());
                    ent.setPickupDelay(40);
                    float f1 = event.getEntityPlayer().world.rand.nextFloat() * 0.5F;
                    float f2 = event.getEntityPlayer().world.rand.nextFloat() * (float) Math.PI * 2.0F;
                    ent.motionX = (double) (-MathHelper.sin(f2) * f1);
                    ent.motionZ = (double) (MathHelper.cos(f2) * f1);
                    ent.motionY = 0.20000000298023224D;
                    event.getDrops().add(ent);
                    inv.setInventorySlotContents(i, ItemStack.EMPTY);
                }
            }
        }
    }

    @SubscribeEvent
    public void handleEvent(PlayerEvent.LoadFromFile event)
    {
        UUID uuid = UUID.fromString(event.getPlayerUUID());
        InventoryDojutsuSlot inv = getDojutsuSlotInventory(uuid);

        try
        {
            inv.readFromNBT(CompressedStreamTools.readCompressed(new FileInputStream(getDataFile(uuid))));
        }
        catch (FileNotFoundException ignored)
        {
        }
        catch (IOException e)
        {
            System.err.println("Error loading Dojutsu data file: " + e.getMessage());
            e.printStackTrace();
            cache.refresh(uuid);
            inv = getDojutsuSlotInventory(uuid);
        }
    }

    @SubscribeEvent
    public void handleEvent(PlayerEvent.SaveToFile event)
    {
        UUID uuid = UUID.fromString(event.getPlayerUUID());
        InventoryDojutsuSlot inv = getDojutsuSlotInventory(uuid);
        NBTTagCompound compound = new NBTTagCompound();
        inv.writeToNBT(compound);
        try
        {
            CompressedStreamTools.writeCompressed(compound, new FileOutputStream(getDataFile(uuid)));
        }
        catch (IOException e)
        {
            System.err.println("Error saving Dojutsu data file: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @SubscribeEvent
    public void handleEvent(PlayerLoggedInEvent event)
    {
        if (event.player instanceof EntityPlayerMP)
        {
            InventoryDojutsuSlot inv = getDojutsuSlotInventory(event.player.getUniqueID());
            for (int i = 0; i < inv.getSizeInventory(); i++)
                DojutsuSlot.network.sendToAll(new PacketSyncDojutsuSlot(event.player, i));
            inv.markClean();

            for (EntityPlayerMP other : FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList().getPlayers())
            {
                if (other == event.player)
                    continue;
                inv = getDojutsuSlotInventory(other.getUniqueID());
                for (int i = 0; i < inv.getSizeInventory(); i++)
                    DojutsuSlot.network.sendTo(new PacketSyncDojutsuSlot(other, i), (EntityPlayerMP) event.player);
            }
        }
    }

    @SubscribeEvent
    public void handleEvent(PlayerLoggedOutEvent event)
    {
        if (event.player instanceof EntityPlayerMP)
        {
            UUID uuid = event.player.getUniqueID();
            try
            {
                forceSave(uuid, getDojutsuSlotInventory(uuid));
            }
            catch (IOException e)
            {
                System.err.println("Error saving Dojutsu data file: " + e.getMessage());
                e.printStackTrace();
            }
            cache.invalidate(uuid);
        }
    }

    @SubscribeEvent
    public void handleEvent(PlayerTickEvent event)
    {
        if (event.phase == Phase.START)
        {
            if (event.player instanceof EntityPlayerMP)
            {
                InventoryDojutsuSlot inv = getDojutsuSlotInventory(event.player.getUniqueID());
                if (inv.isDirty())
                {
                    for (int i = 0; i < inv.getSizeInventory(); i++)
                        DojutsuSlot.network.sendToAll(new PacketSyncDojutsuSlot(event.player, i));
                    inv.markClean();
                }
            }
        }
    }

    void onServerStarting()
    {
        cache.invalidateAll();
    }

    void onServerStopping()
    {
        System.out.println("Server is stopping... force saving all loaded Dojutsu data.");
        for (UUID uuid : cache.asMap().keySet())
        {
            System.out.println(uuid);
            try
            {
                forceSave(uuid, getDojutsuSlotInventory(uuid));
            }
            catch (IOException e)
            {
                System.err.println("Error saving Dojutsu data file: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

}
