package com.fuskirby.dojutsu_slot.client;

import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.UUID;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.Maps;
import com.fuskirby.dojutsu_slot.InventoryManager;
import com.fuskirby.dojutsu_slot.inventory.InventoryDojutsuSlot;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent.ClientDisconnectionFromServerEvent;

import javax.annotation.ParametersAreNonnullByDefault;

public class InventoryManagerClient extends InventoryManager {

    LoadingCache<UUID, InventoryDojutsuSlot> cacheClient = CacheBuilder.newBuilder().build(new CacheLoader<UUID, InventoryDojutsuSlot>()
    {

        @Override
        @ParametersAreNonnullByDefault
        public InventoryDojutsuSlot load(UUID owner) {
            return new InventoryDojutsuSlot();
        }

    });

    Map<UUID, UUID> map = Maps.newHashMap();

    @Override
    public InventoryDojutsuSlot getDojutsuSlotInventoryClient(UUID uuid)
    {
        if (map.isEmpty())
        {
            Minecraft mc = FMLClientHandler.instance().getClient();
            if (mc.player != null)
                map.put(UUID.nameUUIDFromBytes(("OfflinePlayer:" + mc.player.getGameProfile().getName()).getBytes(StandardCharsets.UTF_8)), mc.player.getUniqueID());
        }
        if (map.containsKey(uuid))
            uuid = map.get(uuid);
        return cacheClient.getUnchecked(uuid);
    }

    @SubscribeEvent
    public void handleEvent(ClientDisconnectionFromServerEvent event)
    {
        cacheClient.invalidateAll();
        map.clear();
    }

}
