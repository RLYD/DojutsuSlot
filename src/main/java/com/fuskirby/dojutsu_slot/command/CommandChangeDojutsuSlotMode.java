package com.fuskirby.dojutsu_slot.command;

import com.fuskirby.dojutsu_slot.data.ModWorldData;
import com.fuskirby.dojutsu_slot.enums.WorldMode;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;

import javax.annotation.ParametersAreNonnullByDefault;

public class CommandChangeDojutsuSlotMode extends CommandBase {

    @Override
    @MethodsReturnNonnullByDefault
    public String getName() {
        return "changedojutsuslotmode";
    }

    @Override
    @ParametersAreNonnullByDefault
    @MethodsReturnNonnullByDefault
    public String getUsage(ICommandSender sender) {
        return "/changedojutsuslotmode <classic|dojutsu>";
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 2;
    }

    @Override
    @ParametersAreNonnullByDefault
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if (args.length != 1) {
            throw new WrongUsageException(getUsage(sender));
        }

        WorldMode newMode;
        String arg = args[0].toLowerCase();
        switch (arg) {
            case "classic":
                newMode = WorldMode.CLASSIC;
                break;
            case "dojutsu":
                newMode = WorldMode.DOJUTSU;
                break;
            default:
                throw new CommandException("commands.changedojutsuslotmode.invalid_mode", arg);
        }

        World world = sender.getEntityWorld();

        ModWorldData data = ModWorldData.get(world);
        if (data == null) {
            throw new CommandException("commands.changedojutsuslotmode.no_data");
        }
        data.setMode(newMode);

        sender.sendMessage(new TextComponentTranslation("commands.changedojutsuslotmode.success", newMode.name()));
    }
}