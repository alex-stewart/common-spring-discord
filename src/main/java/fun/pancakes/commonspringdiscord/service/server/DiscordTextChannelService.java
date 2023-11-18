package fun.pancakes.commonspringdiscord.service.server;

import fun.pancakes.commonspringdiscord.exception.DiscordException;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.javacord.api.DiscordApi;
import org.javacord.api.entity.channel.ChannelCategory;
import org.javacord.api.entity.channel.ServerChannel;
import org.javacord.api.entity.channel.ServerTextChannel;
import org.javacord.api.entity.permission.PermissionType;
import org.javacord.api.entity.permission.Permissions;
import org.javacord.api.entity.permission.PermissionsBuilder;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ExecutionException;

@Log4j2
@Service
@RequiredArgsConstructor
public class DiscordTextChannelService {

    private static final Permissions PERMISSIONS_ACCESSIBLE = new PermissionsBuilder()
            .setAllowed(PermissionType.VIEW_CHANNEL)
            .build();

    private static final Permissions PERMISSIONS_PRIVATE = new PermissionsBuilder()
            .setDenied(PermissionType.VIEW_CHANNEL)
            .build();

    private static final Permissions PERMISSIONS_READ_ONLY = new PermissionsBuilder()
            .setDenied(PermissionType.SEND_MESSAGES)
            .build();

    private final DiscordApi discordApi;

    public ServerTextChannel createServerTextChannel(@NonNull String discordServerId,
                                                     @NonNull String channelName) throws ExecutionException, InterruptedException {
        log.info("Creating discord server text channel with name {} for server {}", channelName, discordServerId);
        Server server = getDiscordServerById(discordServerId);

        return server.createTextChannelBuilder()
                .setName(channelName)
                .addPermissionOverwrite(server.getEveryoneRole(), PERMISSIONS_READ_ONLY)
                .create()
                .whenCompleteAsync((serverTextChannel, messageException) -> {
                    if (messageException != null) {
                        log.error("Failed to create server text channel with name {} for server {}", channelName, discordServerId, messageException);
                    } else {
                        log.info("Created server text channel {} with name {} for server {}", serverTextChannel.getIdAsString(), channelName, discordServerId);
                    }
                }).get();
    }

    public ServerTextChannel createServerTextChannel(@NonNull String discordServerId,
                                                     @NonNull String channelName,
                                                     @NonNull String categoryId) throws ExecutionException, InterruptedException {
        log.info("Creating discord server text channel with name {} for server {}", channelName, discordServerId);
        Server server = getDiscordServerById(discordServerId);

        ChannelCategory category = server.getChannelCategoryById(categoryId)
                .orElseThrow(() -> new DiscordException("Failed to find discord server channel category"));

        return server.createTextChannelBuilder()
                .setName(channelName)
                .addPermissionOverwrite(server.getEveryoneRole(), PERMISSIONS_PRIVATE)
                .setCategory(category)
                .create()
                .whenCompleteAsync((serverTextChannel, messageException) -> {
                    if (messageException != null) {
                        log.error("Failed to create server text channel with name {} for server {}", channelName, discordServerId, messageException);
                    } else {
                        log.info("Created server text channel {} with name {} for server {}", serverTextChannel.getIdAsString(), channelName, discordServerId);
                    }
                }).get();
    }

    public void denyAccessToChannel(@NonNull String discordServerId, @NonNull String discordUserId, Set<String> discordChannelIds) {
        Server server = getDiscordServerById(discordServerId);
        User user = getUserById(discordUserId);

        discordChannelIds.stream()
                .map(server::getChannelById)
                .flatMap(Optional::stream)
                .map(ServerChannel::asServerTextChannel)
                .flatMap(Optional::stream)
                .forEach(serverChannel -> serverChannel.createUpdater()
                        .addPermissionOverwrite(user, PERMISSIONS_ACCESSIBLE)
                        .update()
                        .whenCompleteAsync((serverTextChannel, messageException) -> {
                            if (messageException != null) {
                                log.error("Failed to deny access to channel {} for user {} in server {}", serverChannel.getIdAsString(), discordUserId, discordServerId, messageException);
                            } else {
                                log.info("Denied access to channel {} for user {} in server {}", serverChannel.getIdAsString(), discordChannelIds, discordServerId);
                            }
                        })
                );
    }

    public void grantAccessToChannel(@NonNull String discordServerId, @NonNull String discordUserId, Set<String> discordChannelIds) {
        Server server = getDiscordServerById(discordServerId);
        User user = getUserById(discordUserId);

        discordChannelIds.stream()
                .map(server::getChannelById)
                .flatMap(Optional::stream)
                .map(ServerChannel::asServerTextChannel)
                .flatMap(Optional::stream)
                .forEach(serverChannel -> serverChannel.createUpdater()
                        .addPermissionOverwrite(user, PERMISSIONS_PRIVATE)
                        .update()
                        .whenCompleteAsync((serverTextChannel, messageException) -> {
                            if (messageException != null) {
                                log.error("Failed to grant access to channel {} for user {} in server {}", serverChannel.getIdAsString(), discordUserId, discordServerId, messageException);
                            } else {
                                log.info("Granted access to channel {} for user {} in server {}", serverChannel.getIdAsString(), discordChannelIds, discordServerId);
                            }
                        })
                );
    }

    public User getUserById(String discordUserId) {
        try {
            return discordApi.getUserById(discordUserId)
                    .get();
        } catch (InterruptedException | ExecutionException e) {
            throw new DiscordException("Discord user not found.");
        }
    }

    private Server getDiscordServerById(String discordServerId) {
        return discordApi.getServerById(discordServerId)
                .orElseThrow(() -> new DiscordException("Discord server not found."));
    }

}
