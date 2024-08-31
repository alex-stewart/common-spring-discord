package fun.pancakes.commonspringdiscord.service.interaction.listener;

import fun.pancakes.commonspringdiscord.command.Command;
import fun.pancakes.commonspringdiscord.command.SubCommand;
import fun.pancakes.commonspringdiscord.constant.ResponseColor;
import fun.pancakes.commonspringdiscord.exception.DiscordException;
import fun.pancakes.commonspringdiscord.service.interaction.command.DiscordCommandRequest;
import io.micrometer.observation.Observation;
import io.micrometer.observation.ObservationRegistry;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.javacord.api.entity.message.MessageFlag;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.interaction.InteractionBase;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

@Log4j2
@RequiredArgsConstructor
public abstract class AbstractDiscordInteractionListener {

    private final ObservationRegistry observationRegistry;

    protected abstract Map<String, String> getCommandArguments(InteractionBase interaction, Command command);

    protected Map<String, String> getSubCommandArguments(InteractionBase interaction, Command command, String subCommandName) {
        return new HashMap<>();
    }

    protected boolean containsArgument(InteractionBase interaction, String argument) {
        return false;
    }

    protected void handleCommand(Command command, InteractionBase interaction) {
        String userId = interaction.getUser().getIdAsString();
        String interactionId = interaction.getIdAsString();

        SubCommand subCommand = null;
        if (!command.getSubCommands().isEmpty()) {
            subCommand = command.getSubCommands().stream()
                    .filter(c -> containsArgument(interaction, c.getName()))
                    .findFirst()
                    .orElse(null);
        }
        String subCommandName = subCommand == null ? "" : subCommand.getName();
        Command selectedCommand = subCommand == null ? command : subCommand;
        Map<String, String> arguments =  subCommand == null
                ? getCommandArguments(interaction, command)
                : getSubCommandArguments(interaction, command, subCommandName);

        Observation.createNotStarted("discordCommand", observationRegistry)
                .lowCardinalityKeyValue("command", command.getName())
                .lowCardinalityKeyValue("subCommand", subCommandName)
                .highCardinalityKeyValue("interaction", interactionId)
                .highCardinalityKeyValue("user", userId)
                .observe(() -> {
                    try {
                        DiscordCommandRequest commandRequest = new DiscordCommandRequest(interaction, selectedCommand, arguments);
                        selectedCommand.handle(commandRequest);
                    } catch (DiscordException e) {
                        log.error("Caught DiscordException when handling command {} {} interaction {}", command.getName(), subCommandName, interactionId, e);
                        respondWithError(interaction, e.getUserMessage());
                    } catch (Exception e) {
                        log.error("Caught Exception when handling command {} {} interaction {}", command.getName(), subCommandName, interactionId, e);
                        respondWithError(interaction, "Unexpected Error.");
                    }
                });
    }

    private void respondWithError(InteractionBase interaction, String message) {
        interaction.createImmediateResponder()
                .addEmbed(new EmbedBuilder()
                        .setColor(ResponseColor.ERROR)
                        .addField("Command Failed", message))
                .setFlags(EnumSet.of(MessageFlag.EPHEMERAL))
                .respond()
                .whenCompleteAsync((t, ex) -> {
                    if (ex != null) {
                        log.error("Failed to produce error response ", ex);
                    }
                });
    }

}
