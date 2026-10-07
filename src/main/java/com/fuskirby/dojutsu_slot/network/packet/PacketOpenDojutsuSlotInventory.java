package com.fuskirby.dojutsu_slot.network.packet;

import io.netty.buffer.ByteBuf;
import com.fuskirby.dojutsu_slot.DojutsuSlot;
import com.fuskirby.dojutsu_slot.network.NetworkPacket;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.FMLCommonHandler;

public class PacketOpenDojutsuSlotInventory extends NetworkPacket
{

    @Override
    public void handlePacketClient() {}

    @Override
    public void handlePacketServer(EntityPlayerMP player) {
        FMLCommonHandler.instance().getMinecraftServerInstance().addScheduledTask(() -> {
            player.openContainer.onContainerClosed(player);
            player.openGui(DojutsuSlot.instance, 1, player.world, (int) player.posX, (int) player.posY, (int) player.posZ);
        });
    }

    @Override
    public void readFromBuffer(ByteBuf buf) {}

    @Override
    public void writeToBuffer(ByteBuf buf) {}

}
