package jish;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Optional;

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

    public static void sendMessage(IChannel channel, String message) {
        try {
            channel.sendMessage(message);
        } catch (Exception ignored) {
        }
    }

    public static void deleteMessage(IMessage message) {
        try {
            message.delete();
        } catch (Exception ignored) {
        }
    }

    public static IMessage sendEmbed(IChannel channel, EmbedObject embedObject) {
        RequestBuffer.RequestFuture<IMessage> future = RequestBuffer.request(() -> {
            try {
                return new MessageBuilder(JishBit.getInstance().getClient()).withEmbed(embedObject)
                        .withChannel(channel).send();
            } catch (MissingPermissionsException | DiscordException e) {
                e.printStackTrace();
            }
            return null;
        });
        return future.get();
    }


    public static void botLog(IMessage msg) {
        try {
            IChannel logChannel = msg.getChannel();
            //IChannel logChannel = client.getChannelByID(JishBit.HUB_LOG_CH_ID);

            EmbedBuilder bld = new EmbedBuilder()
                    .withColor(255, 255, 255)
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

}