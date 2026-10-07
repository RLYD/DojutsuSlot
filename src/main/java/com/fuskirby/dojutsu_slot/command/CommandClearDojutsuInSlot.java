package com.fuskirby.dojutsu_slot.command;

import java.util.Collections;
import java.util.List;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import com.fuskirby.dojutsu_slot.DojutsuSlot;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;

public class CommandClearDojutsuInSlot extends CommandBase {

    @Override
    @ParametersAreNonnullByDefault
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
    {
        EntityPlayerMP target = args.length == 0 ? getCommandSenderAsPlayer(sender) : getPlayer(server, sender, args[0]);
        DojutsuSlot.invMan.getDojutsuSlotInventory(target.getUniqueID()).clear();
        notifyCommandListener(sender, this, "dojutsu_slot.command.cleardojutsuinslot.success", target.getName());
    }

    @Override
    @MethodsReturnNonnullByDefault
    public String getName()
    {
        return "cleardojutsuinslot";
    }

    @Override
    public int getRequiredPermissionLevel()
    {
        return 2;
    }

    @Override
    @MethodsReturnNonnullByDefault
    @ParametersAreNonnullByDefault
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos targetPos)
    {
        if (args.length == 1)
            return getListOfStringsMatchingLastWord(args, server.getOnlinePlayerNames());
        return Collections.emptyList();
    }

    @Override
    @MethodsReturnNonnullByDefault
    @ParametersAreNonnullByDefault
    public String getUsage(ICommandSender sender)
    {
        return "dojutsu_slot.command.cleardojutsuinslot";
    }

    @Override
    @ParametersAreNonnullByDefault
    public boolean isUsernameIndex(String[] args, int index)
    {
        return index == 0;
    }

}
