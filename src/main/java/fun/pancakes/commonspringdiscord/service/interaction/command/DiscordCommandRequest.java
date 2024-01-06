package fun.pancakes.commonspringdiscord.service.interaction.command;

import fun.pancakes.commonspringdiscord.command.parameter.CommandParameter;
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

    @Getter
    private final Map<String, String> arguments;

    public Instant getTime() {
        return interactionBase.getCreationTimestamp();
    }

    public String getUserId() {
        return interactionBase.getUser().getIdAsString();
    }

    public void respondWithError(String response) {
        respondWithContent(interactionBase, ResponseColor.ERROR, response);
    }

    public void respondWithSuccess(String response) {
        respondWithContent(interactionBase, ResponseColor.SUCCESS, response);
    }

    public void respondWithImage(Supplier<BufferedImage> bufferedImageSupplier, String fileName) {
        interactionBase.createImmediateResponder()
                .setContent(fileName)
                .respond()
                .whenCompleteAsync((interactionOriginalResponseUpdater, ex) -> {
                    if (ex != null) {
                        log.error("Failed to response to interaction {} with map image", interactionBase.getIdAsString(), ex);
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
                                        log.error("Failed to update response with map image", ex2);
                                    }
                                });
                    }
                });
    }

    @Override
    public void respondWithPrompt(CommandPrompt prompt) {
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

    private void respondWithContent(InteractionBase interactionBase, Color colour, String response) {
        interactionBase.createImmediateResponder()
                .addEmbed(new EmbedBuilder()
                        .setColor(colour)
                        .addField("", response))
                .setFlags(EnumSet.of(MessageFlag.EPHEMERAL))
                .respond()
                .whenCompleteAsync((responseUpdater, ex) -> {
                    if (ex != null) {
                        log.error("Failed to response to interaction {} with content {}", interactionBase::getIdAsString, () -> response);
                    }
                });
    }

}
