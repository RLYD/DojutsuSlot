package com.fuskirby.dojutsu_slot.network.packet;

import com.fuskirby.dojutsu_slot.DojutsuSlotContext;
import com.fuskirby.dojutsu_slot.network.NetworkPacket;
import com.fuskirby.dojutsu_slot.data.CustomSusanooData;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.World;
import net.narutomod.Chakra;
import net.narutomod.NarutomodModVariables;
import net.narutomod.entity.EntitySusanooBase;
import net.narutomod.entity.EntitySusanooSkeleton;
import net.narutomod.item.ItemRinnegan;
import net.narutomod.item.ItemSharingan;
import net.narutomod.potion.PotionFeatherFalling;
import net.narutomod.procedure.ProcedureSusanoo;
import net.narutomod.procedure.ProcedureUtils;

public class PacketCustomSusanoo extends NetworkPacket {
    @Override
    public void handlePacketServer(EntityPlayerMP player) {
        if (CustomSusanooData.hasAwakenedSusanoo(player)) {
            summonSusanooDirectly(player);
        }
    }

    @Override
    public void handlePacketClient() {}

    @Override
    public void readFromBuffer(ByteBuf buf) {}

    @Override
    public void writeToBuffer(ByteBuf buf) {}

    private void summonSusanooDirectly(EntityPlayerMP player) {
        World world = player.world;
        ItemStack helmet = DojutsuSlotContext.getDojutsuFromCurrentSlot(player);
        boolean flag = player.isCreative() || ProcedureUtils.hasAnyItemOfSubtype(player, ItemRinnegan.Base.class);

        if (player.getEntityData().getBoolean("susanoo_activated")) {
            double cooldown = player.getEntityData().getDouble("susanoo_ticks") * 0.25d;
            cooldown *= ProcedureUtils.getCooldownModifier(player);
            player.getEntityData().removeTag("susanoo_activated");
            player.getEntityData().removeTag("susanoo_ticks");
            Entity entitySpawned = world.getEntityByID(ProcedureSusanoo.getSummonedSusanooId(player));
            player.getEntityData().removeTag("summonedSusanooID");
            if (entitySpawned instanceof EntitySusanooBase) {
                entitySpawned.setDead();
                if (!flag && (!(helmet.getItem() instanceof ItemSharingan.Base) || !((ItemSharingan.Base) helmet.getItem()).isEternal())) {
                    player.addPotionEffect(new PotionEffect(MobEffects.WEAKNESS, (int) cooldown, 3));
                    player.addPotionEffect(new PotionEffect(MobEffects.NAUSEA, (int) cooldown, 2));
                }
                player.addPotionEffect(new PotionEffect(PotionFeatherFalling.potion, 60, 5));
            }
        } else {
            if (Chakra.pathway(player).consume(ProcedureSusanoo.BASE_CHAKRA_USAGE)) {
                player.getEntityData().setBoolean("susanoo_activated", true);
                player.getEntityData().setDouble("susanoo_cd", NarutomodModVariables.world_tick + 2400.0D);
                EntitySusanooSkeleton.EntityCustom susanoo = new EntitySusanooSkeleton.EntityCustom(player);
                world.spawnEntity(susanoo);
                player.getEntityData().setInteger("summonedSusanooID", susanoo.getEntityId());
            }
        }
    }
}
