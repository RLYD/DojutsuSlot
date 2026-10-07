package com.fuskirby.dojutsu_slot.network.packet;

import com.fuskirby.dojutsu_slot.client.ClientModeCache;
import com.fuskirby.dojutsu_slot.enums.WorldMode;
import com.fuskirby.dojutsu_slot.network.NetworkPacket;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class PacketSyncMode extends NetworkPacket {
    private WorldMode mode;

    public PacketSyncMode() {}

    public PacketSyncMode(WorldMode mode) {
        this.mode = mode;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void handlePacketClient() {
        FMLClientHandler.instance().getClient().addScheduledTask(() -> {
            ClientModeCache.setCurrentMode(mode);
        });
    }

    @Override
    public void handlePacketServer(EntityPlayerMP player) {}

    @Override
    public void readFromBuffer(ByteBuf buf) {
        int ordinal = buf.readInt();
        mode = WorldMode.values()[ordinal];
    }

    @Override
    public void writeToBuffer(ByteBuf buf) {
        buf.writeInt(mode.ordinal());
    }
}