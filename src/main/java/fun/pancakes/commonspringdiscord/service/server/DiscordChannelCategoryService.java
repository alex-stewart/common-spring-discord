package fun.pancakes.commonspringdiscord.service.server;

import fun.pancakes.commonspringdiscord.exception.DiscordException;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.javacord.api.DiscordApi;
import org.javacord.api.entity.channel.ChannelCategory;
import org.javacord.api.entity.server.Server;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.concurrent.ExecutionException;

@Log4j2
@Service
@RequiredArgsConstructor
public class DiscordChannelCategoryService {

    private final DiscordApi discordApi;

    public ChannelCategory createCategory(@NonNull String discordServerId, @NonNull String categoryName) throws ExecutionException, InterruptedException {
        log.info("Creating discord server channel category with name {} for server {}", categoryName, discordServerId);
        Optional<Server> serverOptional = discordApi.getServerById(discordServerId);
        if (serverOptional.isEmpty()) {
            throw new DiscordException("Discord server not found.");
        }

        return serverOptional.get().createChannelCategoryBuilder()
                .setName(categoryName)
                .create()
                .whenCompleteAsync((channelCategory, messageException) -> {
                    if (messageException != null) {
                        log.error("Failed to create channel category {} for server {}", categoryName, discordServerId, messageException);
                    } else {
                        log.info("Created channel category {} with name {} for server {}", channelCategory.getIdAsString(), categoryName, discordServerId);
                    }
                }).get();
    }

}
