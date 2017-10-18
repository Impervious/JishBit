package jishbit.main.commands;

import com.github.jreddit.entity.Submission;
import com.github.jreddit.retrieval.Submissions;
import com.github.jreddit.retrieval.params.SubmissionSort;
import com.github.jreddit.utils.restclient.RestClient;
import jishbit.main.JishBit;
import jishbit.main.Subs;
import jishbit.main.Util;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.handle.impl.obj.ReactionEmoji;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.util.EmbedBuilder;
import sx.blah.discord.util.RequestBuffer;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class CommandMeme implements Command  {
    @Override
    public String getName() {
        return "meme";
    }

    private final Random rng = new Random();

    /**
     * List of submitted memes, do not add directly, use addMeme to retain max
     * size
     */

    private List<String> submittedMemes = new ArrayList<>();


    private RestClient restClient;
    String userAgent = "JishBit";
    String clientID = "XAdloE1QjMnMyyjZjMtrhqao_1c";
    String redirectURI = "www.google.ca";


    /**
     * Recursive function to find a random meme
     */
    private Submission findMeme(int functionAttempt){

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

    private void addMeme(String Url){
        //remove first element (earliest added meme) when list becomes too big
        if(submittedMemes.size() >= 100) submittedMemes.remove(0);
        submittedMemes.add(Url);
        System.out.println(Url);
    }

    @Override
    public void execute(JishBit bot, IDiscordClient client, String[] args, IGuild guild, IMessage msg, boolean isPrivate) {
        Submission submission = findMeme(1);
        //return if meme finding failed
        if(submission == null){
            Util.sendMessage(msg.getChannel(), "Failed to find meme.");
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

        IMessage ourMessage = Util.sendEmbed(msg.getChannel(), builder.build());

        ReactionEmoji dootdoot = ReactionEmoji.of("dootdoot", 304371941752700930L);
        ReactionEmoji downdoot = ReactionEmoji.of("downdoot", 365934862181597184L);
        RequestBuffer.request(() -> ourMessage.addReaction(downdoot));
        RequestBuffer.request(() -> ourMessage.addReaction(dootdoot));
    }
}
