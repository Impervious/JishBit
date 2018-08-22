package jish.commands;

import jish.JishBit;
import jish.Util;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;

public class CommandList implements Command  {
    @Override
    public String getName() {
        return "list";
    }

    @Override
    public void execute(JishBit bot, IDiscordClient client, String[] args, IUser author, IGuild guild, IMessage msg, boolean isPrivate) {
        Util.botLog(msg);
        Util.sendMessage(msg.getChannel(), "**Commands** \n `` `status <message>`` - Changes the status of the bot \n `` `meme`` - Posts a dank meme");
    }
}
