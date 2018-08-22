package jish.commands;

import jish.JishBit;
import jish.Roles;
import jish.Util;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.handle.obj.*;
import sx.blah.discord.util.DiscordException;
import sx.blah.discord.util.MissingPermissionsException;
import sx.blah.discord.util.RateLimitException;

import java.awt.*;
import java.util.Random;

public class CommandColor implements Command {
    @Override
    public String getName() {
        return "color";
    }

    @Override
    public void execute(JishBit bot, IDiscordClient client, String[] args, IUser author ,IGuild guild, IMessage msg, boolean isPrivate) {
        Roles role = Roles.getUserRole(msg.getAuthor().getStringID());
        IUser user = guild.getUserByID(Long.parseLong(args[1]));
        if (args.length == 1 && role != null) {
            IRole userRole = msg.getGuild().getRoleByID(Long.parseLong(role.roleID));
            if (args[0].equalsIgnoreCase("current")) {
                if(!user.getStringID().equalsIgnoreCase(author.getStringID())) {
                    Util.sendMessage(msg.getChannel(), "The current value of " + args[1] + "'s color is #" + getCurrentColor(userRole));
                } else {
                    Util.sendMessage(msg.getChannel(), "The current value of " + userRole.mention() + "'s color is #" + getCurrentColor(userRole));
                }
            } else if (args[0].startsWith("#")) {
                try {
                    Color newColor = Color.decode(args[0]);
                    userRole.changeColor(newColor);
                    Util.sendMessage(msg.getChannel(), "Your color has been changed to #" + getCurrentColor(userRole));
                } catch (RateLimitException | DiscordException | MissingPermissionsException e) {
                    e.printStackTrace();
                }
            } else if(args[0].equalsIgnoreCase("random")) {
                Random random = new Random();
                int nextInt = random.nextInt(256*256*256);
                String colorCode = String.format("#%06x", nextInt);
                Color newColor = Color.decode(colorCode);
                userRole.changeColor(newColor);
                Util.sendMessage(msg.getChannel(), "Your new color is #" + getCurrentColor(userRole));
            } else {
                sendArgsError(msg.getChannel());
            }
        } else {
            sendArgsError(msg.getChannel());
        }
        Util.deleteMessage(msg);
        Util.botLog(msg);
    }

    private String getCurrentColor(IRole role) {
        String hexColor = Integer.toHexString(role.getColor().getRGB() & 0xffffff);
        if (hexColor.length() < 6) {
            hexColor = "000000".substring(0, 6 - hexColor.length()) + hexColor;
        }
        return hexColor;
    }

    private void sendArgsError(IChannel ch) {
        Util.sendMessage(ch, "Incorrect Syntax: `color <current|random|#HEX GOES HERE>");
    }
}