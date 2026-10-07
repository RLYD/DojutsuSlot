package com.fuskirby.dojutsu_slot.util;

import com.fuskirby.dojutsu_slot.DojutsuSlotContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.AxisAlignedBB;
import net.narutomod.PlayerTracker;
import net.narutomod.item.ItemMangekyoSharinganEternal;
import net.narutomod.item.ItemMangekyoSharinganObito;
import net.narutomod.procedure.ProcedureKamuiTeleportEntity;
import net.narutomod.world.WorldKamuiDimension;

public class UtilKamuiHelper {

    private static final double MAX_TIMER = 100.0;
    private static final long RECORD_VALID_TICKS = 100;
    private static final double CLOSE_DISTANCE = 3.0;

    public static boolean isLeftEye() {
        int slot = DojutsuSlotContext.getCurrentSlot();
        return slot == DojutsuSlotContext.SLOT_LEFT_ORIGINAL || slot == DojutsuSlotContext.SLOT_LEFT_EXTENDED;
    }

    public static boolean isRightEye() {
        int slot = DojutsuSlotContext.getCurrentSlot();
        return slot == DojutsuSlotContext.SLOT_RIGHT_ORIGINAL || slot == DojutsuSlotContext.SLOT_RIGHT_EXTENDED;
    }

    public static boolean hasLeftEye(EntityPlayer player) {
        ItemStack left = DojutsuSlotHelper.getLeftDojutsu(player);
        return left.getItem() == ItemMangekyoSharinganObito.helmet || left.getItem() == ItemMangekyoSharinganEternal.helmet;
    }

    public static boolean hasRightEye(EntityPlayer player) {
        ItemStack right = DojutsuSlotHelper.getRightDojutsu(player);
        return right.getItem() == ItemMangekyoSharinganObito.helmet || right.getItem() == ItemMangekyoSharinganEternal.helmet;

    }

    private static double getDistanceToEntitySurface(EntityPlayer player, Entity target) {
        AxisAlignedBB box = target.getEntityBoundingBox();
        double dx = player.posX - Math.max(box.minX, Math.min(player.posX, box.maxX));
        double dy = player.posY - Math.max(box.minY, Math.min(player.posY, box.maxY));
        double dz = player.posZ - Math.max(box.minZ, Math.min(player.posZ, box.maxZ));
        return Math.sqrt(dx * dx + dy * dy + dz * dz);
    }

    private static Entity getAttackRecordTarget(EntityPlayer player) {
        if (!player.getEntityData().hasKey("kamui_last_attack_entity_id")) return null;
        long lastAttack = player.getEntityData().getLong("kamui_last_attack_time");
        if (player.world.getTotalWorldTime() - lastAttack > RECORD_VALID_TICKS) return null;
        int targetId = player.getEntityData().getInteger("kamui_last_attack_entity_id");
        Entity target = player.world.getEntityByID(targetId);
        if (target == null || target.isDead) return null;
        double surfaceDist = getDistanceToEntitySurface(player, target);
        return surfaceDist <= CLOSE_DISTANCE ? target : null;
    }


    public static boolean tryRightEyeTeleport(EntityPlayer player) {
        Entity attackTarget = getAttackRecordTarget(player);
        if (attackTarget != null) {
            performEntityTeleport(player, attackTarget);
            resetKamuiState(player);
            return true;
        }

        return false;
    }

    private static void performEntityTeleport(EntityPlayer player, Entity target) {
        if (!(target instanceof EntityLivingBase)) {
            teleportEntity(target);
            resetKamuiState(player);
            return;
        }
        EntityLivingBase living = (EntityLivingBase) target;
        double dist = player.getDistance(target);
        double size = target.getEntityBoundingBox().getAverageEdgeLength();
        double level = PlayerTracker.getNinjaLevel(player);
        double denom = dist * size * (2.01 - level / 500.1);
        if (denom <= 0) denom = 0.001;
        double i = (MAX_TIMER - 5) / denom;

        if (i >= 1.0) {
            teleportEntity(target);
        } else {
            float damage = (float) Math.min(i * living.getMaxHealth(), 1024.0);
            DamageSource source = DamageSource.causeMobDamage(player)
                    .setDamageBypassesArmor().setDamageIsAbsolute();
            target.attackEntityFrom(source, damage);
            if (!player.world.isRemote) {
                player.sendMessage(new net.minecraft.util.text.TextComponentString(
                        "§c目标过于强大，造成 " + (int) damage + " 点伤害！"));
            }
        }
        resetKamuiState(player);
        player.getEntityData().removeTag("kamui_last_attack_entity_id");
        player.getEntityData().removeTag("kamui_last_attack_time");
    }

    private static void teleportEntity(Entity entity) {
        int targetDim = (entity.dimension == WorldKamuiDimension.DIMID) ? 0 : WorldKamuiDimension.DIMID;
        ProcedureKamuiTeleportEntity.eEntity(entity, (int) entity.posX, (int) entity.posZ, targetDim);
    }

    public static void teleportSelf(EntityPlayer player) {
        int dimid = player.dimension != WorldKamuiDimension.DIMID ? WorldKamuiDimension.DIMID : 0;
        ProcedureKamuiTeleportEntity.eEntity(player, (int) player.posX, (int) player.posZ, dimid);
        resetKamuiState(player);
    }

    public static void resetKamuiState(EntityPlayer player) {
        if (player.getEntityData().getBoolean("kamui_teleport")) {
            player.getEntityData().setBoolean("kamui_teleport", false);
        }
        if (player.getEntityData().getBoolean("kamui_intangible")) {
            player.getEntityData().setBoolean("kamui_intangible", false);
        }
        player.getEntityData().setDouble("kamui_timer", -1);
    }
}
