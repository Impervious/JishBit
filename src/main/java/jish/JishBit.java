package jish;

import jish.commands.Command;
import jish.scheduled.WeedMessage;

import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.impl.StdSchedulerFactory;

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
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.quartz.JobBuilder.newJob;
import static org.quartz.SimpleScheduleBuilder.simpleSchedule;
import static org.quartz.TriggerBuilder.newTrigger;

public class JishBit {

	private static JishBit bot;
	private static IDiscordClient client;

	public static IGuild guild;
	public static IRole role;

	public Scheduler scheduler;

	{
		try {
			scheduler = StdSchedulerFactory.getDefaultScheduler();
		} catch (SchedulerException e) {
			e.printStackTrace();
		}
	}


	private List<Command> registeredCommands = new ArrayList<>();

	private static final Pattern COMMAND_PATTERN = Pattern.compile("^`([^\\s]+) ?(.*)", Pattern.CASE_INSENSITIVE);

	/*
	 * IPA CHANNELS
	 */

	public static final long IPA_MAIN_CH_ID = 73463428634648576L;

	/*
	 * HUB SERVER CHANNELS
	 */

	public static final long HUB_MAIN_CH_ID = 247394948331077632L;
	public static final long HUB_LOG_CH_ID = 473993256372404243L;

	public static void main(String[] args) {
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
		clientBuilder.setMaxReconnectAttempts(50);
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
		try {
			weedMessage();
			scheduler.start();
		} catch (SchedulerException ex) {
			ex.printStackTrace();
		}


		System.out.println("Connected.");
		client.changePresence(StatusType.ONLINE, ActivityType.WATCHING, "Troy");
	}

	@EventSubscriber
	public void onDisconnectEvent(DisconnectedEvent event) {
		Util.botLog("Bot has been shut down because " + event.getReason());
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
				command.get().execute(bot, client, argsArr, msg.getAuthor() ,guild, msg, isPrivate);
			}
		}

		if(text.equalsIgnoreCase("good bot")) {
			Util.botLog(msg);
			Util.sendMessage(msg.getChannel(), ":D");
		} else if(text.equalsIgnoreCase("bad bot")) {
			Util.botLog(msg);
			if(msg.getAuthor().getStringID().equals(Roles.TROY.userID)) {
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
		Util.botLog(msg);

		if (msg.getAuthor().getStringID().equals(Roles.JOSH.userID)) {
			Util.sendMessage(e.getChannel(), "Hi daddy <3");
		} else {
			Util.sendMessage(e.getChannel(), item);
		}
	}

	/*private static int getOffset() {

		TO-DO

		//winter - 18000s
		//summer - 14400s

		boolean inSavingsTime = TimeZone.getTimeZone("US/Eastern").inDaylightTime( new Date() );
		if (inSavingsTime) {
			return 14400;
		} else {
			return 18000;
		}
	}*/

	public void weedMessage() throws SchedulerException {

		/*
		 *  4:20
		 */

		Calendar calendar = Calendar.getInstance();
		calendar.setTimeZone(TimeZone.getTimeZone("EST"));
		calendar.set(Calendar.HOUR_OF_DAY, 16);
		calendar.set(Calendar.MINUTE, 20);
		calendar.set(Calendar.SECOND, 0);
		Date date = calendar.getTime();

		JobDetail job = newJob(WeedMessage.class)
				.withIdentity("420Job", "weedGroup")
				.build();

		Trigger trigger = newTrigger()
				.withIdentity("420Trigger", "weedGroup")
				.startAt(date)
				.withSchedule(simpleSchedule()
				.withIntervalInSeconds(97200) // 97,200 = 1 day + 4 hours. Because why find out why it's broken amirite?
				.repeatForever())
				.build();

		scheduler.scheduleJob(job, trigger);
	}
}