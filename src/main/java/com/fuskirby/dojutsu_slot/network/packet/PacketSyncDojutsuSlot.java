package com.fuskirby.dojutsu_slot.network.packet;

import java.io.IOException;
import java.util.UUID;

import com.fuskirby.dojutsu_slot.DojutsuSlot;
import io.netty.buffer.ByteBuf;
import com.fuskirby.dojutsu_slot.inventory.InventoryDojutsuSlot;
import com.fuskirby.dojutsu_slot.network.NetworkPacket;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.client.FMLClientHandler;

public class PacketSyncDojutsuSlot extends NetworkPacket
{

    UUID uuid;
    int slot;
    ItemStack itemDojutsu;

    public PacketSyncDojutsuSlot() {}

    public PacketSyncDojutsuSlot(EntityPlayer player, int slot) {
        this.uuid = player.getUniqueID();
        this.slot = slot;
        this.itemDojutsu = DojutsuSlot.invMan.getDojutsuSlotInventory(this.uuid).getStackInSlot(slot);
    }

    @Override
    public void handlePacketClient() {
        FMLClientHandler.instance().getClient().addScheduledTask(() -> {
            InventoryDojutsuSlot inv = DojutsuSlot.invMan.getDojutsuSlotInventoryClient(uuid);
            inv.setInventorySlotContents(slot, itemDojutsu);
        });
    }

    @Override
    public void handlePacketServer(EntityPlayerMP player) {}

    @Override
    public void readFromBuffer(ByteBuf buf) {
        PacketBuffer pb = new PacketBuffer(buf);

        uuid = new UUID(pb.readLong(), pb.readLong());
        slot = pb.readByte();
        try
        {
            itemDojutsu = pb.readItemStack();
        }
        catch (IOException ignored)
        {
        }
    }

    @Override
    public void writeToBuffer(ByteBuf buf) {
        PacketBuffer pb = new PacketBuffer(buf);

        pb.writeLong(uuid.getMostSignificantBits());
        pb.writeLong(uuid.getLeastSignificantBits());
        pb.writeByte(slot);
        pb.writeItemStack(itemDojutsu);
    }

}
