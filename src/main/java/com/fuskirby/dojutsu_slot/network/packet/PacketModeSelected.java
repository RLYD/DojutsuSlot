package com.fuskirby.dojutsu_slot.network.packet;

import com.fuskirby.dojutsu_slot.DojutsuSlot;
import com.fuskirby.dojutsu_slot.data.ModWorldData;
import com.fuskirby.dojutsu_slot.enums.WorldMode;
import com.fuskirby.dojutsu_slot.network.NetworkPacket;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.text.TextComponentTranslation;

public class PacketModeSelected extends NetworkPacket {
    private WorldMode mode;

    public PacketModeSelected() {}

    public PacketModeSelected(WorldMode mode) {
        this.mode = mode;
    }

    @Override
    public void handlePacketClient() {}

    @Override
    public void handlePacketServer(EntityPlayerMP player) {
        player.getServerWorld().addScheduledTask(() -> {
            ModWorldData data = ModWorldData.get(player.world);
            if (data != null && !data.isModeSet()) {
                data.setMode(mode);
                DojutsuSlot.network.sendToAll(new PacketSyncMode(mode));
                player.sendMessage(new TextComponentTranslation(
                        "dojutsu.mode.set",
                        new TextComponentTranslation(mode.getTranslationKey())));
            } else {
                player.sendMessage(new TextComponentTranslation("dojutsu.mode.already_set"));
            }
        });
    }

    @Override
    public void readFromBuffer(ByteBuf buf) {
        mode = WorldMode.values()[buf.readInt()];
    }

    @Override
    public void writeToBuffer(ByteBuf buf) {
        buf.writeInt(mode.ordinal());
    }
}