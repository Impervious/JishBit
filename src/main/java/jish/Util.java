package jish;

import java.io.*;
import java.net.URISyntaxException;
import java.util.*;

import org.apache.commons.io.FileUtils;
import sx.blah.discord.api.internal.json.objects.EmbedObject;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.util.*;

public class Util {

    static File botPath;

    static {
        try {
            botPath = new File(JishBit.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath()).getParentFile();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    static Optional<String> getBotToken() {
        try {
            File tokenFile = new File(botPath, "token.txt");
            if (tokenFile.exists()) {
                String token = FileUtils.readFileToString(tokenFile, (String) null);
                if (!token.equalsIgnoreCase("TOKEN") && !token.isEmpty()) {
                    return Optional.of(token);
                } else {
                    return Optional.empty();
                }
            } else {
                FileUtils.writeStringToFile(tokenFile, "TOKEN", (String) null);
                return Optional.empty();
            }
        } catch (IOException e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }

    /*
     *  FACTS
     */

    static File factFile = new File(botPath + "/facts.txt");
    static List<String> facts;

    static {
        try {
            facts = FileUtils.readLines(factFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static Random random = new Random();
    static int size = random.nextInt(facts.size());

    @SuppressWarnings("deprecation")
    public static String randomFacts() {

        facts.remove(size);
        System.out.println(facts.size());
        return facts.get(size);
    }

    public static Integer arraySize() {
        return facts.size();
    }

    public static void removeBlankLines() {
        try {
            List<String> lines = FileUtils.readLines(factFile);

            lines.removeIf(line -> line.trim().isEmpty());
            FileUtils.writeLines(new File(String.valueOf(factFile)), lines);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    /*
     *  MESSAGE STUFF
     */

    public static void sendMessage(IChannel channel, String message) {
        try {
            channel.sendMessage(message);
        } catch (Exception ignored) {
        }
    }

    public static void sendMessage(IChannel channel, StringBuilder builder) {
        try {
            channel.sendMessage(String.valueOf(builder));
        } catch (Exception ignored) {

        }
    }

    public static void deleteMessage(IMessage message) {
        try {
            message.delete();
        } catch (Exception ignored) {
        }
    }

    /*
     *  ADVANCED EMBED
     */

    @SuppressWarnings("all")
    public static IMessage sendEmbed(IChannel channel, EmbedObject embedObject) {
        RequestBuffer.RequestFuture<IMessage> future = RequestBuffer.request(() -> {
            try {
                return new MessageBuilder(JishBit.getClient()).withEmbed(embedObject)
                        .withChannel(channel).send();
            } catch (MissingPermissionsException | DiscordException e) {
                e.printStackTrace();
            }
            return null;
        });
        return future.get();
    }

    /*
     *  SIMPLE EMBED
     */

    public static void sendEmbed(IChannel channel, String message) {
        sendEmbed(channel, new EmbedBuilder()
                .withDesc(message)
                .withAuthorName(channel.getClient().getOurUser().getName() + '#' + channel.getClient().getOurUser().getDiscriminator())
                .withAuthorIcon(channel.getClient().getOurUser().getAvatarURL())
                .withColor(32, 102, 148)
                .withTimestamp(System.currentTimeMillis())
                .build());
    }


    /*
     *  BOT LOG FOR USER MESSAGES
     */

    public static void botLog(IMessage msg) {
        try {
            IChannel logChannel = JishBit.getClient().getChannelByID(JishBit.HUB_LOG_CH_ID);

            EmbedBuilder bld = new EmbedBuilder()
                    .withColor(32, 102, 148)
                    .withAuthorName(msg.getAuthor().getName() + '#' + msg.getAuthor().getDiscriminator())
                    .withAuthorIcon(msg.getAuthor().getAvatarURL())
                    .withDesc(msg.getFormattedContent())
                    .withFooterText(msg.getGuild().getName() + "/#" + msg.getChannel().getName())
                    .withTimestamp(System.currentTimeMillis());

            sendEmbed(logChannel, bld.build());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /*
     *  BOT LOG FOR BOT MESSAGES
     */

    public static void botLog(String string) {
        try {
            IChannel logChannel = JishBit.getClient().getChannelByID(JishBit.HUB_LOG_CH_ID);

            StringBuilder bld = new StringBuilder().append(string);
            sendMessage(logChannel, bld);
        } catch(Exception ex) {
            ex.printStackTrace();
        }
    }

}