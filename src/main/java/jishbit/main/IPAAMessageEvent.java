package jishbit.main;

import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.impl.events.guild.member.UserJoinEvent;
import sx.blah.discord.handle.impl.events.guild.member.UserLeaveEvent;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IRole;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.util.DiscordException;
import sx.blah.discord.util.MessageBuilder;
import sx.blah.discord.util.MissingPermissionsException;
import sx.blah.discord.util.RateLimitException;

import java.awt.*;

public class IPAAMessageEvent {

    @EventSubscriber
    public void ipaaMessageEvent(MessageReceivedEvent event) {
        IMessage message = event.getMessage();
        String text = message.getContent();
        String user = message.getAuthor().getStringID();
        if (message.getGuild().getStringID().equals("73463428634648576")) {
            if (text.toLowerCase().startsWith("`color")) {
                Roles role = Roles.getUserRole(user);
                if (role != null) {
                    String[] args = text.split(" ");
                    if (args.length >= 2) {
                        String argument = args[1];
                        Color currentColor = message.getGuild().getRoleByID(Long.parseLong(role.role)).getColor();
                        String hexColor = Integer.toHexString(currentColor.getRGB() & 0xffffff);
                        if (hexColor.length() < 6) {
                            hexColor = "000000".substring(0, 6 - hexColor.length()) + hexColor;
                        }
                        if (argument.startsWith("#")) {
                            Color color = Color.decode(argument);
                            try {
                                message.getGuild().getRoleByID(Long.parseLong(role.role)).changeColor(color);
                            } catch (RateLimitException | DiscordException | MissingPermissionsException e) {
                                e.printStackTrace();
                            }
                            String target = message.getGuild().getRoleByID(Long.parseLong(role.role)).mention();
                            Util.sendMessage(message.getChannel(), "Color for " + target + " changed from #" + hexColor.toUpperCase() + " to " + argument.toUpperCase());
                        } else if (argument.equalsIgnoreCase("current")) {
                            String target = message.getGuild().getRoleByID(Long.parseLong(role.role)).mention();
                            Util.sendMessage(message.getChannel(), "The current value of " + target + "'s color is #" + hexColor.toUpperCase());
                        } else {
                            try {
                                new MessageBuilder(event.getClient()).withChannel(message.getChannel()).withCode("XL", "ERROR: hex value must contain #").send();
                            } catch (RateLimitException | DiscordException | MissingPermissionsException e) {
                                e.printStackTrace();
                            }
                        }
                    } else {
                        try {
                            new MessageBuilder(event.getClient()).withChannel(message.getChannel()).withCode("XL", "USAGE: `color <hex value> MUST CONTAIN #").send();
                        } catch (RateLimitException | DiscordException | MissingPermissionsException e) {
                            e.printStackTrace();
                        }
                    }
                    Util.deleteMessage(message);
                }
            }
        }
    }

    @EventSubscriber
    public void userLeft(UserLeaveEvent e) {
        String server = e.getGuild().getStringID();

        if(server.equals("73463428634648576")) {
            String leftGuy = e.getUser().toString();
            Util.sendMessage(e.getClient().getChannelByID(Long.parseLong("73463428634648576")), leftGuy + " left in salt lol.");
            System.out.println("User left");
        } else {
            System.out.println("User left");
        }
    }

    @EventSubscriber
    public void userJoined(UserJoinEvent e) throws DiscordException {
        System.out.println("User joined");
        IUser joinGuy = e.getUser();
        String user = e.getUser().getStringID();
        String server = e.getGuild().getStringID();
        Util.sendMessage(e.getClient().getChannelByID(Long.parseLong("73463428634648576")), "Holy fuck look who's back it's " + joinGuy.toString());

        if(server.equals("73463428634648576")) {
            Roles role = Roles.getUserRole(user);
            if(role != null) {
                IRole[] roles = {e.getGuild().getRoleByID(Long.parseLong(role.role)), e.getGuild().getRoleByID(Long.parseLong(role.human))};
                try {
                    e.getGuild().editUserRoles(joinGuy, roles);
                    e.getGuild().setUserNickname(joinGuy, role.nick);
                    Util.sendMessage(e.getClient().getChannelByID(Long.parseLong("73463428634648576")), joinGuy.toString() + "'s perms were updated.");
                } catch(RateLimitException | MissingPermissionsException e1) {
                    e1.printStackTrace();
                }
            }
        }
    }
}
