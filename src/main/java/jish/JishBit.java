package jish;

import jish.commands.Command;
import org.reflections.Reflections;
import sx.blah.discord.api.ClientBuilder;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.ReadyEvent;
import sx.blah.discord.handle.impl.events.guild.channel.message.MentionEvent;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.impl.events.shard.DisconnectedEvent;
import sx.blah.discord.handle.obj.*;
import sx.blah.discord.util.*;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JishBit {

	private static JishBit bot;
	private static IDiscordClient client;

	public static IGuild guild;

	private List<Command> registeredCommands = new ArrayList<>();

	private static final Pattern COMMAND_PATTERN = Pattern.compile("^`([^\\s]+) ?(.*)", Pattern.CASE_INSENSITIVE);

	public static final long MAIN_CH_ID = 73463428634648576L;

	public static final long HUB_LOG_CH_ID = 247394948331077632L;

	public static void main(String[] args) throws Exception {
		new JishBit();
	}

	private JishBit() {
		bot = this;
		registerAllCommands();

		connect();
		client.getDispatcher().registerListener(this);
	}

	private void connect() {
		Optional<String> token = Util.getBotToken();
		if (!token.isPresent()) {
			System.out.println("Add your token to token.txt");
			System.out.println("Shutting down...");
			System.exit(0);
			return;
		}

		ClientBuilder clientBuilder = new ClientBuilder();
		clientBuilder.withToken(token.get());
		clientBuilder.setMaxReconnectAttempts(5);
		try {
			client = clientBuilder.login();
		} catch (DiscordException e) {
			e.printStackTrace();
		}
	}

	public static JishBit getInstance() {
		return bot;
	}

	public static IDiscordClient getClient() {
		return client;
	}

	@EventSubscriber
	public void onReadyEvent(ReadyEvent e) {
		client = e.getClient();
		new ShutUp(client);

		System.out.println("Connected.");
		client.changePresence(StatusType.ONLINE, ActivityType.WATCHING, "Memes");
	}

	@EventSubscriber
	public void onDisconnectEvent(DisconnectedEvent event) {
		System.out.println("BOT DISCONNECTED");
		System.out.println("Reason: " + event.getReason());
	}

	@EventSubscriber
	public void onMessageEvent(MessageReceivedEvent event) {
		IMessage msg = event.getMessage();
		String text = msg.getContent();

		if(event.getMessage().getAuthor().isBot()) return;

		boolean isPrivate = msg.getChannel().isPrivate();
		if(!isPrivate) guild = msg.getGuild();

		Matcher matcher = COMMAND_PATTERN.matcher(text);
		if (matcher.matches()) {

			String baseCommand = matcher.group(1).toLowerCase();
			Optional<Command> command = registeredCommands.stream().filter(com -> com.getName().equalsIgnoreCase(baseCommand)).findAny();

			if (command.isPresent()) {
				String args = matcher.group(2);
				String[] argsArr = args.isEmpty() ? new String[0] : args.split(" ");
				command.get().execute(bot, client, argsArr, guild, msg, isPrivate);
			}
		}

		if(text.equalsIgnoreCase("good bot")) {
			Util.sendMessage(msg.getChannel(), ":D");
		} else if(text.equalsIgnoreCase("bad bot")) {
			if(msg.getAuthor().getStringID().equals("109109946565537792")) {
				Util.sendMessage(msg.getChannel(), "Leave me alone troy.");
			} else {
				Util.sendMessage(msg.getChannel(), "D:");
			}
		}
	}

	private void registerAllCommands() {
		new Reflections("jish.commands").getSubTypesOf(Command.class).forEach(commandImpl -> {
			try {
				Command command = commandImpl.newInstance();
				Optional<Command> existingCommand = registeredCommands.stream().filter(cmd -> cmd.getName().equalsIgnoreCase(command.getName())).findAny();
				if (!existingCommand.isPresent()) {
					registeredCommands.add(command);
					System.out.println("Registered command: " + command.getName());
				} else {
					System.out.println("Attempted to register two commands with the same name: " + existingCommand.get().getName());
					System.out.println("Existing: " + existingCommand.get().getClass().getName());
					System.out.println("Attempted: " + commandImpl.getName());
				}
			} catch (InstantiationException | IllegalAccessException e) {
				e.printStackTrace();
			}
		});
	}

	@EventSubscriber
	public void botMention(MentionEvent e) {
		IMessage msg = e.getMessage();

		List<String> responses = new ArrayList<>();
		responses.add("what?");
		responses.add("wtf do you want?");
		responses.add("stop mentioning me");
		responses.add("omg what?" + " " + msg.getAuthor().mention());

		Random random = new Random();
		int size = random.nextInt(responses.size());
		String item = responses.get(size);
		//Util.sendMessage(e.getChannel(), item);
		Util.botLog(msg);

		if (msg.getAuthor().getStringID().equals("73463573900173312")) {
			Util.sendMessage(e.getChannel(), "Hi daddy <3");
		} else {
			Util.sendMessage(e.getChannel(), item);
		}
	}
}