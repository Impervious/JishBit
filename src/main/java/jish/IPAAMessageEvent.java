package jish;

import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.guild.member.UserJoinEvent;
import sx.blah.discord.handle.impl.events.guild.member.UserLeaveEvent;
import sx.blah.discord.handle.obj.IRole;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.util.DiscordException;
import sx.blah.discord.util.MissingPermissionsException;
import sx.blah.discord.util.RequestBuffer;

public class IPAAMessageEvent {
    @EventSubscriber
    public void userLeft(UserLeaveEvent e) {
        String server = e.getGuild().getStringID();

        if (server.equals("73463428634648576")) {
            String leftGuy = e.getUser().toString();
            Util.sendMessage(e.getClient().getChannelByID(73463428634648576L), leftGuy + " left in salt lol.");
            System.out.println("User left");
        } else {
            System.out.println("User left");
        }
    }

    @EventSubscriber
    public void userJoined(UserJoinEvent event) {
        System.out.println("User joined");
        IUser joinGuy = event.getUser();
        String user = event.getUser().getStringID();
        String server = event.getGuild().getStringID();
        Util.sendMessage(event.getClient().getChannelByID(73463428634648576L), "Holy fuck look who's back it's " + joinGuy.toString());

        if (server.equals("73463428634648576")) {
            Roles role = Roles.getUserRole(user);
            if (role != null) {
                try {
                    IRole[] roles = {event.getGuild().getRoleByID(Long.parseLong(role.roleID)), event.getGuild().getRoleByID(Long.parseLong(role.human))};
                    RequestBuffer.request(() -> event.getGuild().editUserRoles(joinGuy, roles));
                    RequestBuffer.request(() -> event.getGuild().setUserNickname(joinGuy, role.nick));
                    Util.sendMessage(event.getClient().getChannelByID(73463428634648576L), joinGuy.toString() + "'s perms were updated.");
                } catch (DiscordException | MissingPermissionsException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
