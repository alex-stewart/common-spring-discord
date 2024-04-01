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
public class DiscordMessageService implements MessageService {

    private final DiscordApi discordApi;

    public void sendMessageToLocation(String worldId, String locationId, String message) {
        sendMessageToLocation(worldId, locationId, message, new String[]{});
    }

    public void sendMessageToLocation(String worldId, String locationId, String message, String[] userMentions) {
        String formattedMessage = message;
        for (String userMention : userMentions) {
            formattedMessage = formattedMessage.replaceFirst("%s", String.format("<@%s>", userMention));
        }
        log.info("Sending formattedMessage to channel {} : {}", locationId, formattedMessage);

        ServerTextChannel serverTextChannel = discordApi.getChannelById(locationId)
                .flatMap(Channel::asServerTextChannel)
                .orElseThrow(() -> new DiscordException("Discord server text channel not found."));

        EmbedBuilder embed = new EmbedBuilder()
                .setColor(ResponseColor.DEFAULT)
                .addField("", formattedMessage);

        serverTextChannel.sendMessage(embed)
                .whenCompleteAsync((userMessage, ex) -> {
                    if (ex != null) {
                        log.error("Failed to send message to discord channel: {}", locationId, ex);
                    }
                });
    }

    public void sendMessageToUser(String worldId, String userId, String message) {
        sendMessageToUser(worldId, userId, message, new String[]{});
    }

    public void sendMessageToUser(String worldName, String userId, String message, String[] userMentions) {
        String formattedMessage = message;
        for (String userMention : userMentions) {
            formattedMessage = formattedMessage.replaceFirst("%s", String.format("<@%s>", userMention));
        }

        log.info("Sending formattedMessage to user {} : {}", userId, formattedMessage);

        log.info("Sending message to discord user {} : {}", userId, message);
        EmbedBuilder embed = new EmbedBuilder()
                .setColor(ResponseColor.DEFAULT)
                .addField("", worldName)
                .addField("message", formattedMessage);

        discordApi.getUserById(userId)
                .whenCompleteAsync((user, userException) -> {
                    if (userException != null) {
                        log.error("Failed to get discord user with id: {}", user, userException);
                    } else {
                        user.sendMessage(embed)
                                .whenCompleteAsync((userMessage, messageException) -> {
                                    if (messageException != null) {
                                        log.error("Failed to send message to discord user: {}", userId, messageException);
                                    }
                                });
                    }
                });
    }


}
