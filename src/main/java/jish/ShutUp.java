package jish;

import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.impl.events.guild.voice.user.UserSpeakingEvent;
import sx.blah.discord.handle.impl.events.guild.voice.user.UserVoiceChannelJoinEvent;
import sx.blah.discord.handle.impl.events.guild.voice.user.UserVoiceChannelLeaveEvent;
import sx.blah.discord.handle.obj.*;
import sx.blah.discord.util.audio.AudioPlayer;

import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ShutUp {

    private static final File SHUTUP_SOUND = new File(Util.botPath, "shutup.mp3");

    private IDiscordClient bot;

    private boolean shutUpMode = false;
    private IVoiceChannel shutUpChannel;
    private List<String> speakingUsers = new ArrayList<>();

    ShutUp(IDiscordClient bot) {
        this.bot = bot;
        bot.getDispatcher().registerListener(this);
    }

	@EventSubscriber
    public void onMessageEvent(MessageReceivedEvent event) {
        IMessage msg = event.getMessage();
        String text = msg.getContent();

        if(text.startsWith("`")) {
            String cmd = text.substring(1).split(" ")[0].toLowerCase();

            if (cmd.equalsIgnoreCase("shutup")) {
                if(!shutUpMode) {
                    Util.deleteMessage(msg);
                    Optional<IVoiceChannel> connectedChannel = getUserVoiceChannel(event.getAuthor(), event.getGuild());
                    if (connectedChannel.isPresent()) {
                        shutUpMode = true;
                        speakingUsers.clear();
                        shutUpChannel = connectedChannel.get();
                        if (!shutUpChannel.isConnected()) {
                            shutUpChannel.join();
                            if (!isBotInVoiceChannel(shutUpChannel.getGuild())) {
                                try {
                                    //wait for bot to join the channel before queueing the sound
                                    bot.getDispatcher().waitFor((UserVoiceChannelJoinEvent joinEvent) -> joinEvent.getUser().equals(joinEvent.getClient().getOurUser()));
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                }
            }
            if (cmd.equalsIgnoreCase("noshutup")) {
                Util.deleteMessage(msg);
                if(shutUpChannel != null) shutUpChannel.leave();
                noShutUp();
            }
        }
    }

    @EventSubscriber
    public void onUserSpeakUpdate(UserSpeakingEvent event) {
        System.out.println(event.getUser().getName() + " " + event.isSpeaking());
        if(shutUpMode) {
            if (!event.getUser().equals(bot.getOurUser())) {
                String id = event.getUser().getStringID();
                System.out.println(event.getSsrc() + " " + event.getUser().getName() + " " + event.isSpeaking());
                if (event.isSpeaking()) {
                    if (!speakingUsers.contains(id)) {
                        speakingUsers.add(id);
                    }
                } else {
                    speakingUsers.remove(id);
                }
                checkSpamming();
            }
        }
    }

    @EventSubscriber
    public void onUserLeave(UserVoiceChannelLeaveEvent event) {
        if(shutUpMode) {
            if (!event.getUser().equals(bot.getOurUser())) {
                String id = event.getUser().getStringID();
                speakingUsers.remove(id);
                checkSpamming();
            } else {
                //bot left or disconnected
                noShutUp();
            }
        }
    }

    private static Optional<IVoiceChannel> getUserVoiceChannel(IUser user, IGuild guild){
        Optional<IVoiceState> state = user.getVoiceStates().values().stream().filter(voiceState -> voiceState.getGuild().equals(guild)).findAny();
        if(state.isPresent())
            return Optional.ofNullable(state.get().getChannel());
        else
            return Optional.empty();
    }

    private boolean isBotInVoiceChannel(IGuild guild) {
        return guild.getClient().getConnectedVoiceChannels().stream().anyMatch(channel -> channel.getGuild().equals(guild));
    }

    private void noShutUp(){
        if(shutUpChannel != null) {
            AudioPlayer.getAudioPlayerForGuild(shutUpChannel.getGuild()).clear();
            shutUpChannel.leave();
        }
        shutUpMode = false;
        shutUpChannel = null;
        speakingUsers.clear();
    }

    private void checkSpamming(){
        if(shutUpMode && shutUpChannel != null) {
            AudioPlayer player = AudioPlayer.getAudioPlayerForGuild(shutUpChannel.getGuild());
            if(speakingUsers.isEmpty()){
                player.clear();
            } else {
                if(SHUTUP_SOUND.exists()) {
                    try {
                        player.setLoop(true);
                        player.queue(SHUTUP_SOUND);
                    } catch (IOException | UnsupportedAudioFileException e) {
                        e.printStackTrace();
                    }
                } else {
                    System.out.println("No shutup.mp3 file found!");
                }
            }
        }
    }
}
