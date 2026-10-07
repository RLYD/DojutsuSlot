package com.fuskirby.dojutsu_slot.event;

import com.fuskirby.dojutsu_slot.DojutsuSlot;
import com.fuskirby.dojutsu_slot.data.ModWorldData;
import com.fuskirby.dojutsu_slot.network.packet.PacketSyncMode;
import com.fuskirby.dojutsu_slot.network.packet.PacketOpenModeSelection;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;

public class PlayerLoginHandler {

    @SubscribeEvent
    public void onPlayerLogin(PlayerLoggedInEvent event) {
        if (event.player.world.isRemote) return;
        EntityPlayerMP player = (EntityPlayerMP) event.player;
        ModWorldData data = ModWorldData.get(player.world);

        if (data == null) return;

        if (!data.isModeSet()) {
            DojutsuSlot.network.sendTo(new PacketOpenModeSelection(), player);
        } else {
            DojutsuSlot.network.sendTo(new PacketSyncMode(data.getMode()), player);
        }
    }
}