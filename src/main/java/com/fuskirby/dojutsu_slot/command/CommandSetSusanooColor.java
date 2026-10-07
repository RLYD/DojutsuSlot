package com.fuskirby.dojutsu_slot.command;

import com.fuskirby.dojutsu_slot.DojutsuSlot;
import com.fuskirby.dojutsu_slot.data.CustomSusanooData;
import com.fuskirby.dojutsu_slot.network.packet.PacketSyncSusanooColor;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentTranslation;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class CommandSetSusanooColor extends CommandBase {

    private static final Map<String, Integer> PRESET_COLORS = new HashMap<>();
    private static final Random RANDOM = new Random();
    private static final int FIXED_ALPHA = 0x20;

    static {
        PRESET_COLORS.put("red",     FIXED_ALPHA << 24 | 0xFF0000);
        PRESET_COLORS.put("blue",    FIXED_ALPHA << 24 | 0x0000FF);
        PRESET_COLORS.put("green",   FIXED_ALPHA << 24 | 0x00FF00);
        PRESET_COLORS.put("yellow",  FIXED_ALPHA << 24 | 0xFFFF00);
        PRESET_COLORS.put("orange",  FIXED_ALPHA << 24 | 0xFFA500);
        PRESET_COLORS.put("purple",  FIXED_ALPHA << 24 | 0x800080);
        PRESET_COLORS.put("cyan",    FIXED_ALPHA << 24 | 0x00FFFF);
        PRESET_COLORS.put("magenta", FIXED_ALPHA << 24 | 0xFF00FF);
        PRESET_COLORS.put("pink",    FIXED_ALPHA << 24 | 0xFFC0CB);
        PRESET_COLORS.put("white",   FIXED_ALPHA << 24 | 0xFFFFFF);
        PRESET_COLORS.put("black",   FIXED_ALPHA << 24);
        PRESET_COLORS.put("gray",    FIXED_ALPHA << 24 | 0x808080);
        PRESET_COLORS.put("brown",   FIXED_ALPHA << 24 | 0xA52A2A);
    }

    @Override
    @MethodsReturnNonnullByDefault
    public String getName() {
        return "setsusanoocolor";
    }

    @Override
    @MethodsReturnNonnullByDefault
    @ParametersAreNonnullByDefault
    public String getUsage(ICommandSender sender) {
        return "/setsusanoocolor <player> <color> (preset / hex / random)";
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 2;
    }

    @Override
    @ParametersAreNonnullByDefault
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if (args.length < 2) {
            sender.sendMessage(new TextComponentTranslation("command.setsusanoocolor.usage"));
            return;
        }

        String playerName = args[0];
        String colorArg = args[1];

        EntityPlayerMP player = getPlayer(server, sender, playerName);

        int color;
        if (colorArg.equalsIgnoreCase("random")) {
            color = generateRandomColor();
        } else {
            Integer preset = PRESET_COLORS.get(colorArg.toLowerCase());
            if (preset != null) {
                color = preset;
            } else {
                try {
                    color = parseRgbHex(colorArg);
                } catch (NumberFormatException e) {
                    sender.sendMessage(new TextComponentTranslation("command.setsusanoocolor.invalid_color", colorArg));
                    return;
                }
            }
        }

        player.getEntityData().setInteger("susanoo_color", color);
        boolean awakened = CustomSusanooData.hasAwakenedSusanoo(player);

        DojutsuSlot.network.sendTo(new PacketSyncSusanooColor(player, color, awakened), player);
        DojutsuSlot.network.sendToAll(new PacketSyncSusanooColor(player, color, awakened));

        String hex = String.format("%06X", color & 0xFFFFFF);
        sender.sendMessage(new TextComponentTranslation("command.setsusanoocolor.success", player.getName(), hex));
    }

    private int parseRgbHex(String hex) throws NumberFormatException {
        String str = hex.trim();
        if (str.startsWith("#")) {
            str = str.substring(1);
        } else if (str.startsWith("0x") || str.startsWith("0X")) {
            str = str.substring(2);
        }
        if (str.length() != 6) {
            throw new NumberFormatException("Invalid length");
        }
        long rgb = Long.parseLong(str, 16);
        if ((rgb & 0xFF000000L) != 0) {
            throw new NumberFormatException("RGB out of range");
        }
        return (FIXED_ALPHA << 24) | (int) rgb;
    }

    private int generateRandomColor() {
        int r = RANDOM.nextInt(256);
        int g = RANDOM.nextInt(256);
        int b = RANDOM.nextInt(256);
        return (FIXED_ALPHA << 24) | (r << 16) | (g << 8) | b;
    }
}