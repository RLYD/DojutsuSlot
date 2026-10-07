package com.fuskirby.dojutsu_slot.event;

import com.fuskirby.dojutsu_slot.DojutsuSlot;
import com.fuskirby.dojutsu_slot.network.packet.PacketSyncSusanooColor;
import com.fuskirby.dojutsu_slot.data.CustomSusanooData;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class PlayerDataHandler {
    @SubscribeEvent
    public void onPlayerClone(PlayerEvent.Clone event) {
        if (!event.isWasDeath()) {
            return;
        }

        EntityPlayer oldPlayer = event.getOriginal();
        EntityPlayer newPlayer = event.getEntityPlayer();

        boolean hadAwakened = CustomSusanooData.hasAwakenedSusanoo(oldPlayer);
        if (hadAwakened) {
            int color = CustomSusanooData.getSusanooColor(oldPlayer);
            CustomSusanooData.setAwakenedSusanoo(newPlayer, color);
            DojutsuSlot.network.sendTo(new PacketSyncSusanooColor(newPlayer, color, true), (EntityPlayerMP) newPlayer);
            DojutsuSlot.network.sendToAll(new PacketSyncSusanooColor(newPlayer, color, true));
        }
    }
}
