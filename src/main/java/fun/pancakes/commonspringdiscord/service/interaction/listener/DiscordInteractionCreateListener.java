package fun.pancakes.commonspringdiscord.service.interaction.listener;

import fun.pancakes.commonspringdiscord.command.Command;
import io.micrometer.observation.ObservationRegistry;
import lombok.extern.log4j.Log4j2;
import org.javacord.api.event.interaction.InteractionCreateEvent;
import org.javacord.api.interaction.InteractionBase;
import org.javacord.api.interaction.MessageComponentInteraction;
import org.javacord.api.listener.interaction.InteractionCreateListener;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Log4j2
@Component
public class DiscordInteractionCreateListener extends AbstractDiscordInteractionListener implements InteractionCreateListener {

    private final List<Command> commands;

    public DiscordInteractionCreateListener(ObservationRegistry observationRegistry,
                                            List<Command> commands) {
        super(observationRegistry);
        this.commands = commands;
    }

    @Override
    public void onInteractionCreate(InteractionCreateEvent event) {
        if (event.getMessageComponentInteraction().isEmpty()) {
            return;
        }
        MessageComponentInteraction messageComponentInteraction = event.getMessageComponentInteraction().get();
        log.info("Handling messageComponentInteraction {} from user {}",
                messageComponentInteraction::getIdAsString,
                () -> messageComponentInteraction.getUser().getIdAsString());

        commands.stream()
                .filter(c -> isCommandOfType(messageComponentInteraction, c))
                .findFirst()
                .ifPresent(command -> handleCommand(command, messageComponentInteraction));

        messageComponentInteraction.getMessage().delete();
    }

    private boolean isCommandOfType(MessageComponentInteraction interaction, Command command) {
        String commandName = interaction.getCustomId().split("_")[0];
        return command.getName().equals(commandName);
    }

    @Override
    protected Map<String, String> getCommandArguments(InteractionBase interaction, Command command) {
        MessageComponentInteraction messageComponentInteraction = (MessageComponentInteraction) interaction;
        String parameter = messageComponentInteraction.getCustomId().split("_")[1];
        String value = messageComponentInteraction.getCustomId().split("_")[2];
        return Map.of(parameter, value);
    }
}
