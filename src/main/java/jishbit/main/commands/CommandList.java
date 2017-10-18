package jishbit.main.commands;

import jishbit.main.JishBit;
import jishbit.main.Util;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IMessage;

public class CommandList implements Command  {
    @Override
    public String getName() {
        return "list";
    }

    @Override
    public void execute(JishBit bot, IDiscordClient client, String[] args, IGuild guild, IMessage msg, boolean isPrivate) {
        Util.sendMessage(msg.getChannel(), "**Commands** \n `` `status <message>`` - Changes the status of the bot \n `` `meme`` - Posts a dank meme");
    }
}
