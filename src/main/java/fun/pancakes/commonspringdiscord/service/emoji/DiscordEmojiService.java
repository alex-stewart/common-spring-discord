package fun.pancakes.commonspringdiscord.service.emoji;

import fun.pancakes.commonspringdiscord.exception.DiscordException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.javacord.api.DiscordApi;
import org.javacord.api.entity.emoji.CustomEmoji;
import org.javacord.api.entity.emoji.CustomEmojiBuilder;
import org.javacord.api.entity.emoji.KnownCustomEmoji;
import org.javacord.api.entity.server.Server;
import org.springframework.stereotype.Service;

import java.awt.image.BufferedImage;
import java.util.Optional;

@Log4j2
@Service
@RequiredArgsConstructor
public class DiscordEmojiService {

    private final DiscordApi discordApi;
    private final ServerEmojiLoader serverEmojiLoader;
    private final ServerEmojiFactory serverEmojiFactory;

    public String getEmojiForServer(String discordServerId, ServerEmoji serverEmoji) {
        return queryEmojiIdForServer(discordServerId, serverEmoji)
                .orElseThrow(() -> new DiscordException("Failed to find emoji."));
    }

    public Optional<String> queryEmojiIdForServer(String discordServerId, ServerEmoji serverEmoji) {
        Optional<Server> serverOptional = discordApi.getServerById(discordServerId);
        if (serverOptional.isEmpty()) {
            throw new DiscordException("Discord server not found.");
        }
        Server server = serverOptional.get();
        return server.getCustomEmojisByName(serverEmoji.name()).stream()
                .findFirst()
                .map(CustomEmoji::getIdAsString);
    }

    public String addEmojiToServer(String discordServerId, ServerEmoji serverEmoji) {
        log.info("Adding emoji {} to discord server {}", serverEmoji, discordServerId);
        Optional<Server> serverOptional = discordApi.getServerById(discordServerId);
        if (serverOptional.isEmpty()) {
            throw new DiscordException("Discord server not found.");
        }
        Server server = serverOptional.get();

        try {
            BufferedImage image = serverEmojiLoader.getImage(serverEmoji);
            KnownCustomEmoji knownCustomEmoji = new CustomEmojiBuilder(server)
                    .setImage(image)
                    .setName(serverEmoji.name())
                    .create()
                    .get();
            log.info("Added emoji {} to discord server {} with id {}", serverEmoji, discordServerId, knownCustomEmoji.getIdAsString());
            return knownCustomEmoji.getIdAsString();
        } catch (Exception e) {
            log.error("Failed to add emoji {} to discord server {}", serverEmoji, discordServerId, e);
            throw new DiscordException("Failed to add emoji.");
        }
    }

    public void syncEmojis(String discordServerId) {
        log.info("Syncing discord emojis for server {}", discordServerId);
        serverEmojiFactory.getServerEmoji().stream()
                .filter(ge -> queryEmojiIdForServer(discordServerId, ge).isEmpty())
                .forEach(ge -> {
                    String emojiId = addEmojiToServer(discordServerId, ge);
                    log.info("Added emoji {} to server {}", emojiId, discordServerId);
                });
    }

}