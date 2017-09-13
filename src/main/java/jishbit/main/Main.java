package jishbit.main;

import com.github.jreddit.entity.Submission;
import com.github.jreddit.retrieval.Submissions;
import com.github.jreddit.retrieval.params.SubmissionSort;
import com.github.jreddit.utils.restclient.HttpRestClient;
import com.github.jreddit.utils.restclient.RestClient;
import sx.blah.discord.api.ClientBuilder;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.impl.events.guild.member.UserJoinEvent;
import sx.blah.discord.handle.impl.events.guild.member.UserLeaveEvent;
import sx.blah.discord.handle.impl.events.ReadyEvent;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IRole;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.util.DiscordException;
import sx.blah.discord.util.EmbedBuilder;
import sx.blah.discord.handle.impl.events.shard.DisconnectedEvent;
import sx.blah.discord.util.RateLimitException;
import sx.blah.discord.util.RequestBuffer;
import sx.blah.discord.util.MessageBuilder;
import sx.blah.discord.util.MissingPermissionsException;

import java.awt.*;
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

	private RestClient restClient;
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
				deleteMessage(msg);
				if(msg.getAuthor().getStringID().equals("73463573900173312")) {
					try {
						String status = msg.getContent().split(" ", 2)[1];
						client.changePlayingText(status);
						sendMessage(msg.getChannel(), "Status changed to: " + status);
					} catch(Exception e) {
						e.printStackTrace();
					}		
				}
			}
			if(cmd.equalsIgnoreCase("meme")) {
				Submission submission = findMeme(1);
				//return if meme finding failed
				if(submission == null){
					sendMessage(event.getMessage().getChannel(), "Failed to find meme.");
					return;
				}

				deleteMessage(msg);
				String linkolio = submission.getUrl();
				if(linkolio.contains("imgur.com") && !linkolio.matches(".+\\.[A-Za-z]{1,5}$") && !linkolio.contains("/a/") && !linkolio.contains("/gallery/")) {
					linkolio += ".jpg";
				}
				EmbedBuilder builder = new EmbedBuilder();

			    //builder.withAuthorName(submission.getTitle());
			    builder.appendField(submission.getTitle(), "From: " + "[ /r/" + submission.getSubreddit() + "](https://reddit.com/r/" + submission.getSubreddit() + ")", false);
			    //builder.withAuthorUrl("https://www.reddit.com/r/" + submission.getSubreddit());

			    builder.withColor(3, 145, 18);
			    //builder.withDescription("*From /r/" + submission.getSubreddit()+ "*");
			    builder.withTimestamp(System.currentTimeMillis());
			    builder.withUrl(linkolio.replaceAll("&amp;", "&"));
			    builder.withImage(linkolio.replaceAll("&amp;", "&"));
			    
			    RequestBuffer.request(() -> event.getChannel().sendMessage(builder.build()));
				//sendMessage(msg.getChannel(), "*From /r/" + submission.getSubreddit() + ":* " + submission.getTitle() + "\n" + linkolio.replaceAll("&amp;", "&"));
			}
			
			if(cmd.equalsIgnoreCase("list")) {
				sendMessage(msg.getChannel(), "**Commands** \n `` `status <message>`` - Changes the status of the bot \n `` `meme`` - Posts a dank meme");
			}
		}
		
		if(text.equalsIgnoreCase("good bot")) {
			sendMessage(msg.getChannel(), ":D");
		} else if(text.equalsIgnoreCase("bad bot")) {
			if(msg.getAuthor().getStringID().equals("109109946565537792")) {
				sendMessage(msg.getChannel(), "Leave me alone troy.");
			} else {
				sendMessage(msg.getChannel(), "D:");
			}
		}

		if(text.equalsIgnoreCase("<@222446374271057920>")) {
			sendMessage(msg.getChannel(), "JishBit, the ultimate meme bot! \n Created by *Impervious* \n For a list of commands use `` `list``");
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
			//hi troy
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

	public static void sendMessage(IChannel channel, String message){
		try {
			channel.sendMessage(message);
		} catch(Exception e){}
	}

	public static void deleteMessage(IMessage message) {
		try {
			message.delete();
		} catch(Exception e) {}
	}

    @EventSubscriber
    public void ipaaMessageEvent(MessageReceivedEvent event) {
        IMessage message = event.getMessage();
        String text = message.getContent();
        String user = message.getAuthor().getStringID();
        if (message.getGuild().getStringID().equals("73463428634648576")) {
            if (text.toLowerCase().startsWith("`color")) {
                Roles role = Roles.getUserRole(user);
                if (role != null) {
                    String[] args = text.split(" ");
                    if (args.length >= 2) {
                        String argument = args[1];
                        Color currentColor = message.getGuild().getRoleByID(Long.parseLong(role.role)).getColor();
                        String hexColor = Integer.toHexString(currentColor.getRGB() & 0xffffff);
                        if (hexColor.length() < 6) {
                            hexColor = "000000".substring(0, 6 - hexColor.length()) + hexColor;
                        }
                        if (argument.startsWith("#")) {
                            Color color = Color.decode(argument);
                            try {
                                message.getGuild().getRoleByID(Long.parseLong(role.role)).changeColor(color);
                            } catch (RateLimitException | DiscordException | MissingPermissionsException e) {
                                e.printStackTrace();
                            }
                            String target = message.getGuild().getRoleByID(Long.parseLong(role.role)).mention();
                            sendMessage(message.getChannel(), "Color for " + target + " changed from #" + hexColor.toUpperCase() + " to " + argument.toUpperCase());
                        } else if (argument.equalsIgnoreCase("current")) {
                            String target = message.getGuild().getRoleByID(Long.parseLong(role.role)).mention();
                            sendMessage(message.getChannel(), "The current value of " + target + "'s color is #" + hexColor.toUpperCase());
                        } else {
                            try {
                                new MessageBuilder(event.getClient()).withChannel(message.getChannel()).withCode("XL", "ERROR: hex value must contain #").send();
                            } catch (RateLimitException | DiscordException | MissingPermissionsException e) {
                                e.printStackTrace();
                            }
                        }
                    } else {
                        try {
                            new MessageBuilder(event.getClient()).withChannel(message.getChannel()).withCode("XL", "USAGE: `color <hex value> MUST CONTAIN #").send();
                        } catch (RateLimitException | DiscordException | MissingPermissionsException e) {
                            e.printStackTrace();
                        }
                    }
                    deleteMessage(message);
                }
            }
        }
    }
    
    @EventSubscriber
    public void userLeft(UserLeaveEvent e) {
		String server = e.getGuild().getStringID();

		if(server.equals("73463428634648576")) {
			String leftGuy = e.getUser().toString();
			sendMessage(e.getClient().getChannelByID(Long.parseLong("73463428634648576")), leftGuy + " left in salt lol.");
			System.out.println("User left");
		} else {
			System.out.println("User left");
		}
    }
    
    @EventSubscriber
    public void userJoined(UserJoinEvent e) throws DiscordException {
    	System.out.println("User joined");
    	IUser joinGuy = e.getUser();
    	String user = e.getUser().getStringID();
    	String server = e.getGuild().getStringID();
    	sendMessage(e.getClient().getChannelByID(Long.parseLong("73463428634648576")), "Holy fuck look who's back it's " + joinGuy.toString());
    	
    	if(server.equals("73463428634648576")) {
    		Roles role = Roles.getUserRole(user);
    		if(role != null) {
    			IRole[] roles = {e.getGuild().getRoleByID(Long.parseLong(role.role)), e.getGuild().getRoleByID(Long.parseLong(role.human))};
    			try {
    				e.getGuild().editUserRoles(joinGuy, roles);
    				e.getGuild().setUserNickname(joinGuy, role.nick);
    				sendMessage(e.getClient().getChannelByID(Long.parseLong("73463428634648576")), joinGuy.toString() + "'s perms were updated.");
    			} catch(RateLimitException | MissingPermissionsException e1) {
    				e1.printStackTrace();
    			}
    		}
    	}
    }
}