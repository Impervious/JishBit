package jish.scheduled;

import jish.JishBit;
import jish.Util;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import sx.blah.discord.handle.impl.obj.ReactionEmoji;

public class WeedMessage implements org.quartz.Job {

	//TO-DO

	@Override
	public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
		ReactionEmoji reactionEmoji = ReactionEmoji.of("kush", 247255566953480192L);

		Util.sendMessage(JishBit.getClient().getChannelByID(JishBit.IPA_MAIN_CH_ID), "It's 4:20! " + reactionEmoji.toString());
		Util.sendEmbed(JishBit.getClient().getChannelByID(JishBit.HUB_LOG_CH_ID), "did the thing");
	}
}