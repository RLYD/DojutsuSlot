package com.fuskirby.dojutsu_slot.network.packet;

import com.fuskirby.dojutsu_slot.network.NetworkPacket;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.PacketBuffer;

public class PacketSyncCtrlKey extends NetworkPacket {

    private boolean ctrlPressed;

    public PacketSyncCtrlKey() {
    }

    public PacketSyncCtrlKey(boolean ctrlPressed) {
        this.ctrlPressed = ctrlPressed;
    }

    @Override
    public void handlePacketClient() {}

    @Override
    public void handlePacketServer(EntityPlayerMP playerMP) {
        playerMP.getServerWorld().addScheduledTask(() -> {
            playerMP.getEntityData().setBoolean("dojutsu_ctrl_pressed", this.ctrlPressed);
        });
    }

    @Override
    public void readFromBuffer(ByteBuf buf) {
        PacketBuffer pb = new PacketBuffer(buf);
        this.ctrlPressed = pb.readBoolean();
    }

    @Override
    public void writeToBuffer(ByteBuf buf) {
        PacketBuffer pb = new PacketBuffer(buf);
        pb.writeBoolean(this.ctrlPressed);
    }
}