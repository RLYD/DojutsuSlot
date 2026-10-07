package com.fuskirby.dojutsu_slot.mixin.sharingan;

import com.fuskirby.dojutsu_slot.DojutsuSlot;
import com.fuskirby.dojutsu_slot.network.packet.PacketSyncSusanooColor;
import com.fuskirby.dojutsu_slot.data.CustomSusanooData;
import com.fuskirby.dojutsu_slot.util.DojutsuSlotHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.narutomod.item.ItemDojutsu;
import net.narutomod.item.ItemSharingan;
import net.narutomod.procedure.ProcedureSusanoo;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.UUID;

@Mixin(value = ProcedureSusanoo.class, remap = false)
public class MixinProcedureSusanoo {

    @Unique
    private static EntityPlayer _player = null;

    @Unique private static final String AWAKENER_UUID_TAG = "awakener_UUID";

    @Inject(method = "execute", at = @At(value = "HEAD"))
    private static void getPlayer(EntityPlayer player, CallbackInfo ci) {
        _player = player;
    }

    @Redirect(method = "execute",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/util/NonNullList;get(I)Ljava/lang/Object;"),
            remap = false)
    private static Object redirectGetHelmet(NonNullList<ItemStack> instance, int index) {
        if (index == 3) {
            return DojutsuSlotHelper.selectDojutsuForJutsu(_player);
        }
        return instance.get(index);
    }

    @Inject(method = "execute(Lnet/minecraft/entity/player/EntityPlayer;)V",
            at = @At("HEAD"),
            cancellable = true)
    private static void checkCanSummon(EntityPlayer player, CallbackInfo ci) {
        if (CustomSusanooData.hasAwakenedSusanoo(player)) {
            return;
        }

        ItemStack helmet = player.inventory.armorInventory.get(3);
        boolean helmetValid = !helmet.isEmpty()
                && helmet.getItem() instanceof ItemDojutsu.Base
                && ((ItemDojutsu.Base) helmet.getItem()).isOwner(helmet, player);

        if (helmetValid) {
            return;
        }

        if (net.minecraftforge.fml.common.Loader.isModLoaded("dojutsu_slot")) {
            ItemStack left = DojutsuSlotHelper.getLeftDojutsu(player);
            ItemStack right = DojutsuSlotHelper.getRightDojutsu(player);
            boolean leftValid = !left.isEmpty() && left.getItem() instanceof ItemSharingan.Base;
            boolean rightValid = !right.isEmpty() && right.getItem() instanceof ItemSharingan.Base;

            if (leftValid && rightValid) {
                EntityLivingBase leftOwner = ((ItemDojutsu.Base) left.getItem()).getOwner(left, player.world);
                EntityLivingBase rightOwner = ((ItemDojutsu.Base) right.getItem()).getOwner(right, player.world);

                UUID leftAwakener = null;
                UUID rightAwakener = null;
                if (left.getTagCompound() != null && left.getTagCompound().hasKey(AWAKENER_UUID_TAG)) {
                    leftAwakener = UUID.fromString(left.getTagCompound().getString(AWAKENER_UUID_TAG));
                }
                if (right.getTagCompound() != null && right.getTagCompound().hasKey(AWAKENER_UUID_TAG)) {
                    rightAwakener = UUID.fromString(right.getTagCompound().getString(AWAKENER_UUID_TAG));
                }

                if ((leftOwner != null && rightOwner != null) && leftOwner == rightOwner) {
                    return;
                } else if ((leftAwakener != null && rightAwakener != null) && leftAwakener == rightAwakener) {
                    if (player.getUniqueID() == leftAwakener) {
                        return;
                    }
                }
            }
        }

        ci.cancel();
    }

    @Inject(method = "execute(Lnet/minecraft/entity/player/EntityPlayer;)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;func_72838_d(Lnet/minecraft/entity/Entity;)Z", shift = At.Shift.AFTER))
    private static void afterSusanooSpawned(EntityPlayer player, CallbackInfo ci) {
        if (CustomSusanooData.hasAwakenedSusanoo(player)) return;

        int color = 0;

        ItemStack helmet = player.inventory.armorInventory.get(3);
        if (!helmet.isEmpty() && helmet.getItem() instanceof ItemDojutsu.Base
                && ((ItemDojutsu.Base) helmet.getItem()).isOwner(helmet, player)) {
            if (helmet.getItem() instanceof ItemSharingan.Base) {
                color = ((ItemSharingan.Base) helmet.getItem()).getColor(helmet);
            }
        } else {
            ItemStack left = DojutsuSlotHelper.getLeftDojutsu(player);
            if (!left.isEmpty() && left.getItem() instanceof ItemSharingan.Base) {
                color = ((ItemSharingan.Base) left.getItem()).getColor(left);
            }
        }

        if (color != 0) {
            CustomSusanooData.setAwakenedSusanoo(player, color);
            DojutsuSlot.network.sendTo(new PacketSyncSusanooColor(player, color, true), (EntityPlayerMP) player);
            DojutsuSlot.network.sendToAll(new PacketSyncSusanooColor(player, color, true));
        }
    }
}
