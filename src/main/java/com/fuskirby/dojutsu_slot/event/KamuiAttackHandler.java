package com.fuskirby.dojutsu_slot.event;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class KamuiAttackHandler {
    @SubscribeEvent
    public void onPlayerAttack(AttackEntityEvent event) {
        EntityPlayer player = event.getEntityPlayer();
        Entity target = event.getTarget();
        if (target != null && !target.isDead) {
            player.getEntityData().setInteger("kamui_last_attack_entity_id", target.getEntityId());
            player.getEntityData().setLong("kamui_last_attack_time", player.world.getTotalWorldTime());
            player.getEntityData().setDouble("kamui_last_attack_x", target.posX);
            player.getEntityData().setDouble("kamui_last_attack_y", target.posY);
            player.getEntityData().setDouble("kamui_last_attack_z", target.posZ);
        }
    }
}
