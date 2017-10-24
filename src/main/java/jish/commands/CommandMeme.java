package jish.commands;

import com.github.jreddit.entity.Submission;
import jish.JishBit;
import jish.Util;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.handle.impl.obj.ReactionEmoji;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.util.EmbedBuilder;
import sx.blah.discord.util.RequestBuffer;

public class CommandMeme implements Command {
    @Override
    public String getName() {
        return "meme";
    }

    @Override
    public void execute(JishBit bot, IDiscordClient client, String[] args, IGuild guild, IMessage msg, boolean isPrivate) {
        Submission submission = bot.getMemeManager().findMeme(1);

        //return if meme finding failed
        if (submission == null) {
            Util.sendMessage(msg.getChannel(), "Failed to find meme.");
            return;
        }

        Util.deleteMessage(msg);
        String linkolio = submission.getUrl();
        if (linkolio.contains("imgur.com") && !linkolio.matches(".+\\.[A-Za-z]{1,5}$") && !linkolio.contains("/a/") && !linkolio.contains("/gallery/")) {
            linkolio += ".jpg";
        }
        EmbedBuilder builder = new EmbedBuilder();

        builder.appendField(submission.getTitle(), "From: " + "[ /r/" + submission.getSubreddit() + "](https://reddit.com/r/" + submission.getSubreddit() + ")", false);

        builder.withColor(3, 145, 18);
        builder.withTimestamp(System.currentTimeMillis());
        builder.withUrl(linkolio.replaceAll("&amp;", "&"));
        builder.withImage(linkolio.replaceAll("&amp;", "&"));

        IMessage ourMessage = Util.sendEmbed(msg.getChannel(), builder.build());

        ReactionEmoji dootdoot = ReactionEmoji.of("dootdoot", 304371941752700930L);
        ReactionEmoji downdoot = ReactionEmoji.of("downdoot", 365934862181597184L);
        RequestBuffer.request(() -> ourMessage.addReaction(downdoot));
        RequestBuffer.request(() -> ourMessage.addReaction(dootdoot));
    }
}
