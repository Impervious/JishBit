package jish;

import java.util.Random;

public enum Subs {

	DOOT("ledootgeneration"),
	SPOOK("2spooky4me"),
	MEIRL("meirl"),
	ME_IRL("me_irl"),
	DM("DankMemes"),
	TWOMEIRD4MEIRL("2meirl4meirl"),
	PREQUEAL("prequealmemes"),
	WHOLESOME("wholesomememes"),
	GODMEMES("dankchristianmemes"),
	IZLAM("Izlam"),
	TREBUCHET("trebuchetmemes"),
	MINYAN("breakawayminyan");

    private static final Random RNG = new Random();

    public String subreddit;

    Subs(String subreddit) {
        this.subreddit = subreddit;
    }

    public static Subs getRandomSubreddit() {
        int index = RNG.nextInt(values().length);
        return values()[index];
    }
}