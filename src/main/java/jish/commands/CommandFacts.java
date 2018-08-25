package jish.commands;

import jish.FactManager;
import jish.JishBit;
import jish.Util;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;

public class CommandFacts implements Command {
	@Override
	public String getName() {
		return "fact";
	}

	private FactManager factManager = new FactManager();

	@Override
	public void execute(JishBit bot, IDiscordClient client, String[] args, IUser author, IGuild guild, IMessage msg, boolean isPrivate) {
		Util.botLog(msg);

		if(args.length == 1) {
			if (args[0].equalsIgnoreCase("size")) {
				Util.sendMessage(msg.getChannel(), factManager.arraySize().toString());
			} else if(args[0].equalsIgnoreCase("clean")) {
				Util.deleteMessage(msg);
				factManager.removeBlankLines();
				Util.sendMessage(msg.getChannel(), "Removed blank lines from fact file");
			}
		} else {
			try {
				Util.deleteMessage(msg);
				Util.sendMessage(msg.getChannel(), factManager.randomFacts());
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}
}
