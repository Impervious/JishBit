package jish.commands;

import jish.JishBit;
import jish.Util;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.handle.obj.ActivityType;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.StatusType;

public class CommandStatus implements Command  {
    @Override
    public String getName() {
        return "status";
    }

    @Override
    public void execute(JishBit bot, IDiscordClient client, String[] args, IGuild guild, IMessage msg, boolean isPrivate) {
        if(!isPrivate) {
            Util.botLog(msg);
            Util.deleteMessage(msg);
            if(msg.getAuthor().getStringID().equals("73463573900173312")) {
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