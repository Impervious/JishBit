package jish.commands;

import jish.JishBit;
import jish.Util;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;

public class CommandRoom implements Command {
    @Override
    public String getName() {
        return "room";
    }

    @Override
    public void execute(JishBit bot, IDiscordClient client, String[] args, IUser author, IGuild guild, IMessage msg, boolean isPrivate) {
        Util.botLog(msg);

        String reply;
        if (args.length == 0) {
            reply = "Click here to join your SnycLounge room:\n" + buildURL("");
        } else if (args.length == 1) {
            reply = "Click here to join your SnycLounge room:\n" + buildURL(args[0]);
        } else {
            reply = "Error u broke me";
        }

        Util.sendEmbed(msg.getChannel(), reply);

        Util.deleteMessage(msg);
    }

    private String buildURL(String roomName) {
        if (roomName.isEmpty()) {
            roomName = "ipa";
        }

        return "http://themediaserver.ddns.net:10081/slweb/#/join?server=http%3A%2F%2Fthemediaserver.ddns.net%3A10082%2Fslserver&room=" + roomName +"&owner=troyhayes50&password=pie";
    }
}