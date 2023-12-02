package fun.pancakes.commonspringdiscord.service.interaction.listener;

import fun.pancakes.commonspringdiscord.command.Command;
import fun.pancakes.commonspringdiscord.command.CommandParameter;
import fun.pancakes.commonspringdiscord.constant.ResponseColor;
import fun.pancakes.commonspringdiscord.service.interaction.command.DiscordCommandRequest;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.javacord.api.entity.message.MessageFlag;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.interaction.InteractionBase;

import java.util.EnumSet;
import java.util.Map;

@Log4j2
@NoArgsConstructor
public abstract class AbstractDiscordInteractionListener {

    protected abstract Map<CommandParameter, String> getCommandArguments(InteractionBase interaction, Command command);

    protected void handleCommand(Command command, InteractionBase interaction) {
        String userId = interaction.getUser().getIdAsString();
        String interactionId = interaction.getIdAsString();

        log.info("Executing command {} for slashCommandInteraction {} from user {}", command.getName(), interactionId, userId);
        try {
            Map<CommandParameter, String> arguments = getCommandArguments(interaction, command);
            DiscordCommandRequest commandRequest = new DiscordCommandRequest(interaction, arguments);
            command.handle(commandRequest);
        } catch (Exception e) {
            log.error("Caught exception when handling command {} interaction {}", command.getName(), interactionId, e);
            respondWithError(interaction, e.getMessage());
        }
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
