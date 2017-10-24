package jish;

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
    public void userLeft(UserLeaveEvent e) {
        String server = e.getGuild().getStringID();

        if(server.equals("73463428634648576")) {
            String leftGuy = e.getUser().toString();
            Util.sendMessage(e.getClient().getChannelByID(73463428634648576L), leftGuy + " left in salt lol.");
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
        Util.sendMessage(e.getClient().getChannelByID(73463428634648576L), "Holy fuck look who's back it's " + joinGuy.toString());

        if(server.equals("73463428634648576")) {
            Roles role = Roles.getUserRole(user);
            if(role != null) {
                IRole[] roles = {e.getGuild().getRoleByID(Long.parseLong(role.roleID)), e.getGuild().getRoleByID(Long.parseLong(role.human))};
                try {
                    e.getGuild().editUserRoles(joinGuy, roles);
                    e.getGuild().setUserNickname(joinGuy, role.nick);
                    Util.sendMessage(e.getClient().getChannelByID(73463428634648576L), joinGuy.toString() + "'s perms were updated.");
                } catch(RateLimitException | MissingPermissionsException e1) {
                    e1.printStackTrace();
                }
            }
        }
    }
}
