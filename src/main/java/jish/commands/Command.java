package jish.commands;

import jish.JishBit;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;

public interface Command {
    String getName();

    void execute(JishBit bot, IDiscordClient client, String[] args, IUser author, IGuild guild, IMessage msg, boolean isPrivate);
}
