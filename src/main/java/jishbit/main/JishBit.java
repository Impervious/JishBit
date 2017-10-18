package jishbit.main;

import com.github.jreddit.utils.restclient.HttpRestClient;
import com.github.jreddit.utils.restclient.RestClient;
import jishbit.main.commands.*;
import sx.blah.discord.api.ClientBuilder;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.ReadyEvent;
import sx.blah.discord.handle.impl.events.guild.channel.message.MentionEvent;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.impl.events.guild.channel.message.reaction.ReactionEvent;
import sx.blah.discord.handle.impl.events.shard.DisconnectedEvent;
import sx.blah.discord.handle.impl.obj.ReactionEmoji;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.util.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JishBit {

	public static JishBit bot;

	private static IDiscordClient client;
	public static IGuild guild;

	private List<Command> registeredCommands = new ArrayList<>();

	private static final Pattern COMMAND_PATTERN = Pattern.compile("^`([^\\s]+) ?(.*)", Pattern.CASE_INSENSITIVE);

	public static void main(String[] args) throws Exception {
		bot = new JishBit();
	}

	private JishBit() {

		registerCommand(new CommandList());
		registerCommand(new CommandMeme());
		registerCommand(new CommandName());
		registerCommand(new CommandStatus());

		RestClient restClient = new HttpRestClient();
		restClient.setUserAgent("JishBit");

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
		ClientBuilder cB = new ClientBuilder();
		cB.withToken(token.get());
		cB.setMaxReconnectAttempts(50);
		try {
			client = cB.login();
		} catch (DiscordException e) {
			e.printStackTrace();
		}
	}

	@EventSubscriber
	public void onReadyEvent(ReadyEvent e) {

		new ShutUp(e.getClient());

		System.out.println("Connected.");
		client.changePlayingText("Memes");
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

	public void registerCommand(Command command) {
		if(!registeredCommands.contains(command)) {
			registeredCommands.add(command);
			System.out.println("Registered Command: " + command.getName());
		}
	}

    @EventSubscriber
    public void onReactEvent(ReactionEvent event) {
		if(event.getReaction() != null) {
			IMessage message = event.getMessage();
			IUser author = event.getAuthor();
			IUser reactor = event.getUser();
			int count = event.getReaction().getUsers().size(); //workaround because event.getCount() is broke af
			if (author.getStringID().equals(client.getOurUser().getStringID()) && !reactor.isBot()) { //if message reacted to is from the bot
				ReactionEmoji emojiUsed = event.getReaction().getEmoji();
				ReactionEmoji downdoot = ReactionEmoji.of("downdoot", 365934862181597184L);
				if (emojiUsed.getLongID() == downdoot.getLongID()) {
					if (count >= 3) {
						System.out.println(count);
						Util.deleteMessage(message);
						Util.sendMessage(event.getChannel(), "Deleted that weak meme.");
					}
				}
			}
		}
    }

	@EventSubscriber
	public void botMention(MentionEvent e) {
		IMessage msg = e.getMessage();

		EmbedBuilder builder = new EmbedBuilder();

		builder.withAuthorName("JishBit");
		builder.withAuthorIcon("https://i.imgur.com/QRVYlDC.png");
		builder.appendField("suh", "Hello I am RLCompBot. I was created by <@73463573900173312>", false);
		builder.appendField("What do I do?", "Right now I greet new members to the server and assign roles and platforms! I plan on maybe doing more in the future.", false);
		builder.appendField("Source Code: ", "[`GitHub`](https://github.com/Impervious/JishBit)", true);
		builder.withColor(255, 30, 229);
		builder.withTimestamp(System.currentTimeMillis());

		RequestBuffer.request(() -> e.getChannel().sendMessage(builder.build()));
	}
}