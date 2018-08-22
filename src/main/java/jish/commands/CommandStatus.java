package jish.commands;

import jish.JishBit;
import jish.Roles;
import jish.Util;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.handle.obj.*;

public class CommandStatus implements Command  {
    @Override
    public String getName() {
        return "status";
    }

    @Override
    public void execute(JishBit bot, IDiscordClient client, String[] args, IUser author, IGuild guild, IMessage msg, boolean isPrivate) {
        if(!isPrivate) {
            Util.botLog(msg);
            Util.deleteMessage(msg);
            if(msg.getAuthor().getStringID().equals(Roles.JOSH.userID)) {
                try {
                    String status = msg.getContent().split(" ", 2)[1];
                    client.changePresence(StatusType.ONLINE, ActivityType.WATCHING, status);
                    Util.sendMessage(msg.getChannel(), "Status changed to: " + status);
                } catch(Exception e) {
                    e.printStackTrace();
                }
            } else {
                Util. deleteMessage(msg);
                Util.sendMessage(msg.getChannel(), "Only my daddy can change my status.");
            }
        }
    }
}