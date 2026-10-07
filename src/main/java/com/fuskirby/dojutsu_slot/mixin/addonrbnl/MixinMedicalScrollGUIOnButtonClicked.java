package com.fuskirby.dojutsu_slot.mixin.addonrbnl;

import com.fuskirby.dojutsu_slot.util.DojutsuSlotHelper;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.narutomod.procedure.ProcedureMedicalScrollGUIOnButtonClicked;
import net.rabalib.hooklib.hooks.HookProcedureMedicalScrollGUIOnButtonClicked;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;
import java.util.WeakHashMap;
import java.util.function.Supplier;

import static com.fuskirby.dojutsu_slot.util.WorldModeHelper.isDojutsuMode;

@Mixin(value = HookProcedureMedicalScrollGUIOnButtonClicked.class, remap = false)
public class MixinMedicalScrollGUIOnButtonClicked {

    @Unique
    private static final WeakHashMap<EntityPlayerMP, String> pendingState = new WeakHashMap<>();

    @Inject(
            method = "executeProcedure",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/player/EntityPlayerMP;func_192039_O()Lnet/minecraft/advancements/PlayerAdvancements;",
                    ordinal = 0
            ),
            cancellable = true,
            remap = false
    )
    private static void checkDojutsuStateMatch(ProcedureMedicalScrollGUIOnButtonClicked procedure, Map<String, Object> dependencies, CallbackInfo ci) {
        World world = (World) dependencies.get("world");
        if (!isDojutsuMode(world)) return;

        Object entityObj = dependencies.get("entity");
        if (!(entityObj instanceof EntityPlayerMP)) return;
        EntityPlayerMP player = (EntityPlayerMP) entityObj;

        ItemStack stack0 = getStackInSlot(player, 0);
        ItemStack stack1 = getStackInSlot(player, 1);
        if (stack0.isEmpty() || stack1.isEmpty()) return;

        String state0 = DojutsuSlotHelper.getDojutsuState(stack0);
        String state1 = DojutsuSlotHelper.getDojutsuState(stack1);

        if (state0 == null || state1 == null || !state0.equals(state1)) {
            ci.cancel();
        } else {
            pendingState.put(player, state0);
        }
    }

    @Inject(
            method = "executeProcedure",
            at = @At(value = "RETURN"),
            remap = false
    )
    private static void applyDojutsuStateToOutput(ProcedureMedicalScrollGUIOnButtonClicked procedure, Map<String, Object> dependencies, CallbackInfo ci) {
        World world = (World) dependencies.get("world");
        if (!isDojutsuMode(world)) return;

        Object entityObj = dependencies.get("entity");
        if (!(entityObj instanceof EntityPlayerMP)) return;
        EntityPlayerMP player = (EntityPlayerMP) entityObj;

        String state = pendingState.remove(player);
        if (state == null) return;

        ItemStack output = getStackInSlot(player, 2);
        if (output.isEmpty()) return;

        NBTTagCompound tag = output.getTagCompound();
        if (tag == null) tag = new NBTTagCompound();
        tag.setString("dojutsu_state", state);
        output.setTagCompound(tag);

        Container container = player.openContainer;
        if (container instanceof Supplier) {
            container.detectAndSendChanges();
        }
    }


    @Unique
    private static ItemStack getStackInSlot(EntityPlayerMP player, int slotId) {
        Container container = player.openContainer;
        if (container instanceof Supplier) {
            Object invObj = ((Supplier<?>) container).get();
            if (invObj instanceof Map) {
                Slot slot = (Slot) ((Map<?, ?>) invObj).get(slotId);
                if (slot != null) return slot.getStack();
            }
        }
        return ItemStack.EMPTY;
    }
}
