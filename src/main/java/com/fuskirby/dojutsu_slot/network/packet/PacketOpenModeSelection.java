package com.fuskirby.dojutsu_slot.network.packet;

import com.fuskirby.dojutsu_slot.client.gui.GUIModeSelection;
import com.fuskirby.dojutsu_slot.network.NetworkPacket;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class PacketOpenModeSelection extends NetworkPacket {
    public PacketOpenModeSelection() {}

    @SideOnly(Side.CLIENT)
    @Override
    public void handlePacketClient() {
        FMLClientHandler.instance().getClient().addScheduledTask(() -> {
            Minecraft.getMinecraft().displayGuiScreen(new GUIModeSelection());
        });
    }

    @Override
    public void handlePacketServer(EntityPlayerMP player) {}

    @Override
    public void readFromBuffer(ByteBuf buf) {}

    @Override
    public void writeToBuffer(ByteBuf buf) {}
}