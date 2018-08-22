package jish.commands;

import jish.JishBit;
import jish.Roles;
import jish.Util;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;

public class CommandName implements Command  {
    @Override
    public String getName() {
        return "name";
    }

    @Override
    public void execute(JishBit bot, IDiscordClient client, String[] args, IUser author, IGuild guild, IMessage msg, boolean isPrivate) {
        if(!isPrivate) {
            Util.botLog(msg);
            Util.deleteMessage(msg);
            if(msg.getAuthor().getStringID().equals(Roles.JOSH.userID)) {
                try {
                    String name = msg.getContent().split(" ", 2)[1];
                    client.changeUsername(name);
                    Util.sendMessage(msg.getChannel(), "Name changed to: " + name);
                } catch(Exception e) {
                    e.printStackTrace();
                }
            } else {
                Util.deleteMessage(msg);
                Util.sendMessage(msg.getChannel(), "Only my daddy can change my name.");
            }
        }
    }
}
