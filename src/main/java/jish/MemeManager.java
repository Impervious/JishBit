package jish;

import com.github.jreddit.entity.Submission;
import com.github.jreddit.retrieval.Submissions;
import com.github.jreddit.retrieval.params.SubmissionSort;
import com.github.jreddit.utils.restclient.HttpRestClient;
import com.github.jreddit.utils.restclient.RestClient;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MemeManager {
    private final Random rng = new Random();

    /**
     * List of submitted memes, do not add directly, use addMeme to retain max
     * size
     */

    private List<String> submittedMemes = new ArrayList<>();


    private RestClient restClient;
    String userAgent = "JishBit";

    public MemeManager() {
        restClient = new HttpRestClient();
        restClient.setUserAgent(userAgent);
    }


    /**
     * Recursive function to find a random meme
     */
    public Submission findMeme(int functionAttempt) {

        Subs sub = Subs.getRandomSubreddit();
        String subToUse = sub.subreddit;

        Submissions subms = new Submissions(restClient);

        List<Submission> submissionsSubreddit = subms.ofSubreddit(subToUse, SubmissionSort.TOP, -1, 100, null, null, true);

        Submission submissionToUse = null;
        int attempts = 0;
        while (submissionToUse == null || submittedMemes.contains(submissionToUse.getUrl())) {
            int index = rng.nextInt(submissionsSubreddit.size());
            submissionToUse = submissionsSubreddit.get(index);
            //if after 50 attempts no unused meme is found in this subreddit, retry with new submission request and subreddit
            attempts++;
            if (attempts >= 50) {
                functionAttempt++;
                //if after 10 recursive calls no meme is found, accept failure and return null
                if (functionAttempt > 10) {
                    System.out.println("FAILED TO FIND MEME. MEMERGENCY.");
                    return null;
                } else {
                    return findMeme(functionAttempt);
                }
            }
        }
        System.out.println("Found meme after " + functionAttempt + " subreddit searches and " + attempts + " posts.");
        addMeme(submissionToUse.getUrl());
        return submissionToUse;
    }

    private void addMeme(String Url) {
        //remove first element (earliest added meme) when list becomes too big
        if (submittedMemes.size() >= 100) submittedMemes.remove(0);
        submittedMemes.add(Url);
        System.out.println(Url);
    }

}
