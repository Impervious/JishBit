package jish;

import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.guild.member.UserJoinEvent;
import sx.blah.discord.handle.impl.events.guild.member.UserLeaveEvent;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IRole;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.util.DiscordException;
import sx.blah.discord.util.MissingPermissionsException;
import sx.blah.discord.util.RequestBuffer;

public class IPAAMessageEvent {

    @EventSubscriber
    public void userLeft(UserLeaveEvent event) {
        IGuild server = event.getGuild();
        IChannel main = event.getClient().getChannelByID(JishBit.IPA_MAIN_CH_ID);
        if (server.equals(JishBit.IPA_MAIN_CH_ID)) {
            String leftGuy = event.getUser().toString();
            Util.sendMessage(main, leftGuy + " left in salt lol.");
            System.out.println("User left");
        } else {
            System.out.println("User left");
        }
    }

    @EventSubscriber
    public void userJoined(UserJoinEvent event) {
        System.out.println("User joined");
        IUser joinGuy = event.getUser();
        IChannel main = event.getClient().getChannelByID(JishBit.IPA_MAIN_CH_ID);
        String user = event.getUser().getStringID();
        String server = event.getGuild().getStringID();
        IGuild ipa = JishBit.getClient().getGuildByID(JishBit.IPA_MAIN_CH_ID);
        Util.sendMessage(main, "Holy fuck look who's back it's " + joinGuy.toString());

        if (server.equals(JishBit.getClient().getGuildByID(JishBit.IPA_MAIN_CH_ID))) {
            Roles role = Roles.getUserRole(user);
            if (role != null) {
                try {
                    IRole[] roles = {event.getGuild().getRoleByID(Long.parseLong(role.roleID)), event.getGuild().getRoleByID(Long.parseLong(role.human))};
                    RequestBuffer.request(() -> event.getGuild().editUserRoles(joinGuy, roles));
                    RequestBuffer.request(() -> event.getGuild().setUserNickname(joinGuy, role.nick));
                    Util.sendMessage(main, joinGuy.toString() + "'s perms were updated.");
                } catch (DiscordException | MissingPermissionsException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}