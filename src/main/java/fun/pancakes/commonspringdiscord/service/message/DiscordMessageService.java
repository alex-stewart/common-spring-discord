package fun.pancakes.commonspringdiscord.service.message;

import fun.pancakes.commonspringdiscord.constant.ResponseColor;
import fun.pancakes.commonspringdiscord.exception.DiscordException;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.javacord.api.DiscordApi;
import org.javacord.api.entity.channel.Channel;
import org.javacord.api.entity.channel.ServerTextChannel;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.springframework.stereotype.Service;

@Log4j2
@Service
@AllArgsConstructor
public class DiscordMessageService {

    private final DiscordApi discordApi;

    public void sendMessageToServerTextChannel(String discordChannelId, String message) {
        log.info("Sending message to channel {} : {}", discordChannelId, message);
        ServerTextChannel serverTextChannel = discordApi.getChannelById(discordChannelId)
                .flatMap(Channel::asServerTextChannel)
                .orElseThrow(() -> new DiscordException("Discord server text channel not found."));

        EmbedBuilder embed = new EmbedBuilder()
                .setColor(ResponseColor.DEFAULT)
                .addField("", message);

        serverTextChannel.sendMessage(embed)
                .whenCompleteAsync((userMessage, ex) -> {
                    if (ex != null) {
                        log.error("Failed to send message to discord channel: {}", discordChannelId, ex);
                    }
                });
    }

    public void sendMessageToDiscordUser(String gameName, String discordUserId, String message) {
        log.info("Sending message to discord user {} : {}", discordUserId, message);
        EmbedBuilder embed = new EmbedBuilder()
                .setColor(ResponseColor.DEFAULT)
                .addField("", gameName)
                .addField("message", message);

        discordApi.getUserById(discordUserId)
                .whenCompleteAsync((user, userException) -> {
                    if (userException != null) {
                        log.error("Failed to get discord user with id: {}", discordUserId, userException);
                    } else {
                        user.sendMessage(embed)
                                .whenCompleteAsync((userMessage, messageException) -> {
                                    if (messageException != null) {
                                        log.error("Failed to send message to discord user: {}", discordUserId, messageException);
                                    }
                                });
                    }
                });
    }


}
