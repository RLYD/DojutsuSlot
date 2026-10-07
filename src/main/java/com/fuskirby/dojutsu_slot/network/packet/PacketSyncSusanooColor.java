package com.fuskirby.dojutsu_slot.network.packet;

import com.fuskirby.dojutsu_slot.network.NetworkPacket;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class PacketSyncSusanooColor extends NetworkPacket {
    private int playerId;
    private int color;
    private boolean awaken_susanoo;

    public PacketSyncSusanooColor() {}

    public PacketSyncSusanooColor(EntityPlayer player, int color, boolean awaken_susanoo) {
        this.playerId = player.getEntityId();
        this.color = color;
        this.awaken_susanoo = awaken_susanoo;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void handlePacketClient() {
        Minecraft.getMinecraft().addScheduledTask(() -> {
            WorldClient world = Minecraft.getMinecraft().world;
            if (world == null) return;
            Entity entity = world.getEntityByID(playerId);
            if (entity instanceof EntityPlayer) {
                EntityPlayer targetPlayer = (EntityPlayer) entity;
                if (awaken_susanoo) {
                    targetPlayer.getEntityData().setInteger("susanoo_color", color);
                } else {
                    targetPlayer.getEntityData().removeTag("susanoo_color");
                }
            }
        });
    }

    @Override
    public void handlePacketServer(EntityPlayerMP player) {}

    @Override
    public void readFromBuffer(ByteBuf buf) {
        playerId = buf.readInt();
        color = buf.readInt();
        awaken_susanoo = buf.readBoolean();
    }

    @Override
    public void writeToBuffer(ByteBuf buf) {
        buf.writeInt(playerId);
        buf.writeInt(color);
        buf.writeBoolean(awaken_susanoo);
    }
}