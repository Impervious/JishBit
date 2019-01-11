package jish.commands;

import jish.JishBit;
import jish.Util;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IRole;
import sx.blah.discord.handle.obj.IUser;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class CommandRoleID implements Command {
    @Override
    public String getName() {
        return "roleid";
    }

    @Override
    public void execute(JishBit bot, IDiscordClient client, String[] args, IUser author, IGuild guild, IMessage msg, boolean isPrivate) {
        if (args.length == 1) {
            String roleName = Arrays.stream(args).collect(Collectors.joining(" "));
            List<IRole> serverRoles = guild.getRoles();

            if (roleName.equalsIgnoreCase("listall")) {
                String roleList = serverRoles.stream().map(role -> role.getName() + " " + role.getStringID()).reduce("", (a, b) -> a + b + "\n");

                Util.sendEmbed(msg.getChannel(), roleList);
            } else {
                Optional<IRole> foundRole = serverRoles.stream().filter(role -> role.getName().equalsIgnoreCase(roleName)).findAny();

                if (foundRole.isPresent()) {
                    Util.sendEmbed(msg.getChannel(), "Found id for **" + foundRole.get().getName() + "**: " + foundRole.get().getStringID());
                } else {
                    Util.sendEmbed(msg.getChannel(), "Role not found");
                }
            }
        }
        Util.deleteMessage(msg);
    }

}
