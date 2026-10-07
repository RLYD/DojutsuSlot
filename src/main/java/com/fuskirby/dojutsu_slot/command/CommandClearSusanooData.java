package com.fuskirby.dojutsu_slot.command;

import com.fuskirby.dojutsu_slot.DojutsuSlot;
import com.fuskirby.dojutsu_slot.network.packet.PacketSyncSusanooColor;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;

import javax.annotation.ParametersAreNonnullByDefault;

public class CommandClearSusanooData extends CommandBase {

    @Override
    @MethodsReturnNonnullByDefault
    public String getName() {
        return "clearsusanoo";
    }

    @Override
    @MethodsReturnNonnullByDefault
    @ParametersAreNonnullByDefault
    public String getUsage(ICommandSender sender) {
        return "/clearsusanoo [color|awakened|all] [player]";
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 2;
    }

    @Override
    @ParametersAreNonnullByDefault
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if (args.length < 1) {
            sendColored(sender, TextFormatting.RED,
                    new TextComponentTranslation("dojutsu.command.clearsusanoo.usage", getUsage(sender)));
            return;
        }

        String target = args.length >= 2 ? args[1] : sender.getName();
        EntityPlayerMP player = getPlayer(server, sender, target);

        String action = args[0].toLowerCase();

        switch (action) {
            case "color":
                player.getEntityData().removeTag("susanoo_color");
                sendColored(sender, TextFormatting.GREEN,
                        new TextComponentTranslation("dojutsu.command.clearsusanoo.cleared_color", player.getName()));
                DojutsuSlot.network.sendTo(new PacketSyncSusanooColor(player, 0xFF00AAFF, false), player);
                DojutsuSlot.network.sendToAll(new PacketSyncSusanooColor(player, 0xFF00AAFF, false));
                break;

            case "awakened":
                player.getEntityData().removeTag("has_awakened_susanoo");
                sendColored(sender, TextFormatting.GREEN,
                        new TextComponentTranslation("dojutsu.command.clearsusanoo.cleared_awakened", player.getName()));
                break;

            case "all":
                player.getEntityData().removeTag("has_awakened_susanoo");
                player.getEntityData().removeTag("susanoo_color");
                sendColored(sender, TextFormatting.GREEN,
                        new TextComponentTranslation("dojutsu.command.clearsusanoo.cleared_all", player.getName()));
                DojutsuSlot.network.sendTo(new PacketSyncSusanooColor(player, 0xFF00AAFF, false), player);
                DojutsuSlot.network.sendToAll(new PacketSyncSusanooColor(player, 0xFF00AAFF, false));
                break;

            default:
                sendColored(sender, TextFormatting.RED,
                        new TextComponentTranslation("dojutsu.command.clearsusanoo.unknown", args[0]));
                return;
        }

        if (player.getEntityData().getBoolean("susanoo_activated")) {
            player.getEntityData().removeTag("susanoo_activated");
            player.getEntityData().removeTag("susanoo_ticks");
            player.dismountRidingEntity();
            sendColored(sender, TextFormatting.YELLOW,
                    new TextComponentTranslation("dojutsu.command.clearsusanoo.warning_active", player.getName()));
        }
    }

    private static void sendColored(ICommandSender sender, TextFormatting color,
                                    TextComponentTranslation message) {
        message.getStyle().setColor(color);
        sender.sendMessage(message);
    }
}