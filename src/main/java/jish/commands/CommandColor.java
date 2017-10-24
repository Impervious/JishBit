package jish.commands;

import jish.JishBit;
import jish.Roles;
import jish.Util;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IRole;
import sx.blah.discord.util.DiscordException;
import sx.blah.discord.util.MissingPermissionsException;
import sx.blah.discord.util.RateLimitException;

import java.awt.*;

public class CommandColor implements Command {
    @Override
    public String getName() {
        return "color";
    }

    @Override
    public void execute(JishBit bot, IDiscordClient client, String[] args, IGuild guild, IMessage msg, boolean isPrivate) {
        Roles role = Roles.getUserRole(msg.getAuthor().getStringID());
        if (args.length == 1 && role != null) {
            IRole userRole = msg.getGuild().getRoleByID(Long.parseLong(role.roleID));
            if (args[0].equalsIgnoreCase("current")) {
                Util.sendMessage(msg.getChannel(), "The current value of " + userRole.mention() + "'s color is #" + getCurrentColor(userRole));
            } else if (args[0].startsWith("#")) {
                try {
                    Color newColor = Color.decode(args[0]);
                    userRole.changeColor(newColor);
                } catch (RateLimitException | DiscordException | MissingPermissionsException e) {
                    e.printStackTrace();
                }
            } else {
                sendArgsError(msg.getChannel());
            }
        } else {
            sendArgsError(msg.getChannel());
        }
        Util.deleteMessage(msg);
    }

    private String getCurrentColor(IRole role) {
        String hexColor = Integer.toHexString(role.getColor().getRGB() & 0xffffff);
        if (hexColor.length() < 6) {
            hexColor = "000000".substring(0, 6 - hexColor.length()) + hexColor;
        }
        return hexColor;
    }

    private void sendArgsError(IChannel ch) {
        Util.sendMessage(ch, "Error: You did a wrong argument I think...");
    }
}