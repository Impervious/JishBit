package jishbit.main;

import java.util.Random;

public enum Subs {
	MEIRL("meirl"),
	ME_IRL("me_irl"),
	DM("DankMemes"),
	TWOMEIRD4MEIRL("2meirl4meirl"),
	PREQUEAL("prequealmemes"),
	WHOLESOME("wholesomememes");

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