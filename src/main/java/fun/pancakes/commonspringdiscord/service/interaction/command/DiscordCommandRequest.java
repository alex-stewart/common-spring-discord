package fun.pancakes.commonspringdiscord.service.interaction.command;

import fun.pancakes.commonspringdiscord.command.Command;
import fun.pancakes.commonspringdiscord.command.CommandPrompt;
import fun.pancakes.commonspringdiscord.command.CommandRequest;
import fun.pancakes.commonspringdiscord.constant.ResponseColor;
import fun.pancakes.commonspringdiscord.exception.DiscordException;
import fun.pancakes.commonspringdiscord.service.emoji.ServerEmoji;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.collections4.ListUtils;
import org.javacord.api.entity.emoji.CustomEmoji;
import org.javacord.api.entity.message.MessageFlag;
import org.javacord.api.entity.message.component.ActionRow;
import org.javacord.api.entity.message.component.ButtonBuilder;
import org.javacord.api.entity.message.component.ButtonStyle;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.server.Server;
import org.javacord.api.interaction.InteractionBase;
import org.javacord.api.interaction.callback.InteractionImmediateResponseBuilder;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.time.Instant;
import java.util.EnumSet;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@Log4j2
@RequiredArgsConstructor
public class DiscordCommandRequest implements CommandRequest {

    private final InteractionBase interactionBase;
    private final Command command;
    private final boolean responded = false;

    @Getter
    private final Map<String, String> arguments;

    public Instant getTime() {
        return interactionBase.getCreationTimestamp();
    }

    public String getUserId() {
        return interactionBase.getUser().getIdAsString();
    }

    public String getServerId() {
        return interactionBase.getServer().orElseThrow().getIdAsString();
    }

    public String getChannelId() {
        return interactionBase.getChannel().orElseThrow().getIdAsString();
    }

    public void respondWithError(String response) {
        if (responded) {
            log.error("Already responded to {}", interactionBase.getIdAsString());
        }
        respondWithContent(interactionBase, ResponseColor.ERROR, String.format("%s - Failed", command.getName()), response, true);
    }

    public void respondWithSuccess(String response) {
        if (responded) {
            log.error("Already responded to {}", interactionBase.getIdAsString());
        }
        respondWithContent(interactionBase, ResponseColor.SUCCESS, command.getName(), response, command.isResponseHidden());
    }

    public void respondWithSimpleText(String response) {
        if (responded) {
            log.error("Already responded to {}", interactionBase.getIdAsString());
        }
        InteractionImmediateResponseBuilder interactionImmediateResponseBuilder = interactionBase.createImmediateResponder()
                .setContent(response);

        if (command.isResponseHidden()) {
            interactionImmediateResponseBuilder
                    .setFlags(EnumSet.of(MessageFlag.EPHEMERAL));
        }

        interactionImmediateResponseBuilder
                .respond()
                .whenCompleteAsync((responseUpdater, ex) -> {
                    if (ex != null) {
                        log.error("Failed to respond simple text to interaction {}", interactionBase::getIdAsString, () -> ex);
                    }
                });
    }

    public void respondWithImage(Supplier<BufferedImage> bufferedImageSupplier, String fileName) {
        if (responded) {
            log.error("Already responded to {}", interactionBase.getIdAsString());
        }
        interactionBase.createImmediateResponder()
                .setContent(fileName)
                .respond()
                .whenCompleteAsync((interactionOriginalResponseUpdater, ex) -> {
                    if (ex != null) {
                        log.error("Failed to response to interaction {} with image", interactionBase.getIdAsString(), ex);
                    } else {
                        BufferedImage bufferedImage = null;

                        try {
                            bufferedImage = bufferedImageSupplier.get();
                        } catch (Exception e) {
                            log.error("Error getting buffered image", e);
                            return;
                        }
                        interactionOriginalResponseUpdater
                                .addAttachment(bufferedImage, fileName)
                                .setFlags(EnumSet.of(MessageFlag.EPHEMERAL))
                                .update()
                                .whenCompleteAsync((responseUpdater, ex2) -> {
                                    if (ex2 != null) {
                                        log.error("Failed to update response with image", ex2);
                                    }
                                });
                    }
                });
    }

    @Override
    public void respondWithPrompt(CommandPrompt prompt) {
        if (responded) {
            log.error("Already responded to {}", interactionBase.getIdAsString());
        }
        InteractionImmediateResponseBuilder responseBuilder = interactionBase.createImmediateResponder()
                .setFlags(EnumSet.of(MessageFlag.EPHEMERAL));

        Server server = interactionBase.getServer()
                .orElseThrow(() -> new DiscordException("Discord server not found."));

        ListUtils.partition(prompt.choices(), 3)
                .stream()
                .map(choices -> ActionRow.of(choices.stream()
                        .map(choice -> new ButtonBuilder()
                                .setStyle(ButtonStyle.PRIMARY)
                                .setDisabled(!choice.enabled())
                                .setLabel(choice.name())
                                .setEmoji(customEmoji(server, choice.emoji()))
                                .setCustomId(choice.value())
                                .build())
                        .collect(Collectors.toList())
                ))
                .forEach(responseBuilder::addComponents);

        responseBuilder.respond()
                .whenCompleteAsync((responseUpdater, ex) -> {
                    if (ex != null) {
                        log.error("Failed to response to interaction {} with command prompt {}", interactionBase::getIdAsString, () -> prompt);
                    }
                });

    }

    private CustomEmoji customEmoji(Server server, ServerEmoji serverEmoji) {
        return server.getCustomEmojisByName(serverEmoji.name())
                .stream().findFirst()
                .orElseThrow(() -> new DiscordException("Emoji with name not found."));
    }

    private void respondWithContent(InteractionBase interactionBase, Color colour, String title, String response, boolean isHidden) {
        InteractionImmediateResponseBuilder interactionImmediateResponseBuilder = interactionBase.createImmediateResponder()
                .addEmbed(new EmbedBuilder()
                        .setColor(colour)
                        .setTitle(title)
                        .addField("", response));

        if (isHidden) {
            interactionImmediateResponseBuilder
                    .setFlags(EnumSet.of(MessageFlag.EPHEMERAL));
        }

        interactionImmediateResponseBuilder
                .respond()
                .whenCompleteAsync((responseUpdater, ex) -> {
                    if (ex != null) {
                        log.error("Failed to respond with content to interaction {}", interactionBase::getIdAsString, () -> ex);
                    }
                });
    }

}
