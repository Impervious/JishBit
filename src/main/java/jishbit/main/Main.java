package jishbit.main;

import com.github.jreddit.entity.Submission;
import com.github.jreddit.retrieval.Submissions;
import com.github.jreddit.retrieval.params.SubmissionSort;
import com.github.jreddit.utils.restclient.HttpRestClient;
import com.github.jreddit.utils.restclient.RestClient;
import sx.blah.discord.api.ClientBuilder;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.api.internal.json.objects.EmbedObject;
import sx.blah.discord.handle.impl.events.ReadyEvent;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.impl.events.guild.channel.message.reaction.ReactionEvent;
import sx.blah.discord.handle.impl.events.shard.DisconnectedEvent;
import sx.blah.discord.handle.impl.obj.ReactionEmoji;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.util.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

public class Main {

	public static Main bot;

	public static IDiscordClient client;
	public static IGuild guild;

	/**
	 * List of submitted memes, do not add directly, use addMeme to retain max
	 * size
	 */
	private List<String> submittedMemes = new ArrayList<>();

	public RestClient restClient;
	String userAgent = "JishBit";
	String clientID = "XAdloE1QjMnMyyjZjMtrhqao_1c";
	String redirectURI = "www.google.ca";

	private final Random rng = new Random();

	public static void main(String[] args) throws Exception {
		bot = new Main();
	}

	public Main() {
		restClient = new HttpRestClient();
		restClient.setUserAgent("JishBit");
		connect();
		client.getDispatcher().registerListener(this);
	}

	public void connect() {
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

		new ShutUp(this, e.getClient());

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

		if(text.startsWith("`")) {
			String cmd = text.substring(1).split(" ")[0].toLowerCase();

			if(cmd.equalsIgnoreCase("status")) {
				Util.deleteMessage(msg);
				if(msg.getAuthor().getStringID().equals("73463573900173312")) {
					try {
						String status = msg.getContent().split(" ", 2)[1];
						client.changePlayingText(status);
						Util.sendMessage(msg.getChannel(), "Status changed to: " + status);
					} catch(Exception e) {
						e.printStackTrace();
					}		
				} else {
					Util. deleteMessage(msg);
					Util.sendMessage(msg.getChannel(), "Only my daddy can change my status.");
                }
			}

			if(cmd.equalsIgnoreCase("name")) {
				Util.deleteMessage(msg);
			    if(msg.getAuthor().getStringID().equals("73463573900173312")) {
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

			if(cmd.equalsIgnoreCase("meme")) {
				Submission submission = findMeme(1);
				//return if meme finding failed
				if(submission == null){
					Util.sendMessage(event.getMessage().getChannel(), "Failed to find meme.");
					return;
				}

				Util.deleteMessage(msg);
				String linkolio = submission.getUrl();
				if(linkolio.contains("imgur.com") && !linkolio.matches(".+\\.[A-Za-z]{1,5}$") && !linkolio.contains("/a/") && !linkolio.contains("/gallery/")) {
					linkolio += ".jpg";
				}
				EmbedBuilder builder = new EmbedBuilder();

			    builder.appendField(submission.getTitle(), "From: " + "[ /r/" + submission.getSubreddit() + "](https://reddit.com/r/" + submission.getSubreddit() + ")", false);

			    builder.withColor(3, 145, 18);
			    builder.withTimestamp(System.currentTimeMillis());
			    builder.withUrl(linkolio.replaceAll("&amp;", "&"));
			    builder.withImage(linkolio.replaceAll("&amp;", "&"));

                IMessage ourMessage = sendEmbed(event.getChannel(), builder.build());

                ReactionEmoji dootdoot = ReactionEmoji.of("dootdoot", 304371941752700930l);
                ReactionEmoji downdoot = ReactionEmoji.of("downdoot", 365934862181597184l);
                RequestBuffer.request(() -> ourMessage.addReaction(downdoot));
                RequestBuffer.request(() -> ourMessage.addReaction(dootdoot));
			}
			
			if(cmd.equalsIgnoreCase("list")) {
				Util.sendMessage(msg.getChannel(), "**Commands** \n `` `status <message>`` - Changes the status of the bot \n `` `meme`` - Posts a dank meme");
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

		if(text.equalsIgnoreCase("<@222446374271057920>")) {
			Util.sendMessage(msg.getChannel(), "JishBit, the ultimate meme bot! \n Created by *Impervious* \n For a list of commands use `` `list``");
		}
	}

    public static IMessage sendEmbed(IChannel channel, EmbedObject embedObject) {
        RequestBuffer.RequestFuture<IMessage> future = RequestBuffer.request(() -> {
            try {
                return new MessageBuilder(client).withEmbed(embedObject).withChannel(channel).send();
            } catch (MissingPermissionsException | DiscordException e) {
                e.printStackTrace();
            }
            return null;
         });
        return future.get();
	}

    @EventSubscriber
    public void onReactEvent(ReactionEvent event) {
		if(event.getReaction() != null) {
			IMessage message = event.getMessage();
			IUser author = event.getAuthor();
			IUser reacter = event.getUser();
			int count = event.getReaction().getUsers().size(); //workaround because event.getCount() is broke af
			if (author.getStringID().equals(client.getOurUser().getStringID()) && !reacter.isBot()) { //if message reacted to is from the bot
				ReactionEmoji emojiUsed = event.getReaction().getEmoji();
				ReactionEmoji downdoot = ReactionEmoji.of("downdoot", 365934862181597184l);
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

	/**
	 * Recursive function to find a random meme
	 */
	public Submission findMeme(int functionAttempt){

		Subs sub = Subs.getRandomSubreddit();
		String subToUse = sub.subreddit;

		Submissions subms = new Submissions(restClient);

		List<Submission> submissionsSubreddit = subms.ofSubreddit(subToUse, SubmissionSort.TOP, -1, 100, null, null, true);

		Submission submissionToUse = null;
		int attempts = 0;
		while(submissionToUse == null || submittedMemes.contains(submissionToUse.getUrl())) {
			int index = rng.nextInt(submissionsSubreddit.size());
			submissionToUse = submissionsSubreddit.get(index);
			//if after 50 attempts no unused meme is found in this subreddit, retry with new submission request and subreddit
			attempts++;
			if(attempts >= 50){
				functionAttempt++;
				//if after 10 recursive calls no meme is found, accept failure and return null
				if(functionAttempt > 10) {
					System.out.println("FAILED TO FIND MEME. MEMERGENCY.");
					return null;
				}else{
					return findMeme(functionAttempt);
				}
			}
		}
		System.out.println("Found meme after " + functionAttempt + " subreddit searches and " + attempts + " posts.");
		addMeme(submissionToUse.getUrl());
		return submissionToUse;
	}

	public void addMeme(String Url){
		//remove first element (earliest added meme) when list becomes too big
		if(submittedMemes.size() >= 100) submittedMemes.remove(0);
		submittedMemes.add(Url);
		System.out.println(Url);
	}
}