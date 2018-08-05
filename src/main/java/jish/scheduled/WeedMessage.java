package jish.scheduled;

import jish.JishBit;
import jish.Util;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

public class WeedMessage implements org.quartz.Job {

	@Override
	public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
		Util.sendMessage(JishBit.getClient().getChannelByID(JishBit.HUB_MAIN_CH_ID), "It's 4:20! :pepe: :frowning:");
		Util.sendEmbed(JishBit.getClient().getChannelByID(JishBit.HUB_LOG_CH_ID), "did the thing");
	}
}