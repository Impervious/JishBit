package jish.scheduled;

import jish.JishBit;
import jish.Util;
import org.quartz.JobExecutionContext;

public class WeedMessage implements org.quartz.Job {

	//TO-DO

	@Override
	public void execute(JobExecutionContext jobExecutionContext) {
		Util.sendMessage(JishBit.getClient().getChannelByID(JishBit.IPA_MAIN_CH_ID), "jublz");
		Util.sendEmbed(JishBit.getClient().getChannelByID(JishBit.HUB_LOG_CH_ID), "Sent the 4:20 message.");
	}
}