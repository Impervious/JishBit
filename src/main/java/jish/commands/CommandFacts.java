package jish.commands;

import jish.JishBit;
import jish.Util;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IMessage;

public class CommandFacts implements Command {
	@Override
	public String getName() {
		return "fact";
	}

	@Override
	public void execute(JishBit bot, IDiscordClient client, String[] args, IGuild guild, IMessage msg, boolean isPrivate) {
		try {
			Util.sendMessage(client.getChannelByID(JishBit.HUB_MAIN_CH_ID), Util.getFact());
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
}
