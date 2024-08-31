package fun.pancakes.commonspringdiscord.service.interaction.listener;

import fun.pancakes.commonspringdiscord.command.Command;
import fun.pancakes.commonspringdiscord.command.parameter.CommandParameter;
import fun.pancakes.commonspringdiscord.exception.DiscordException;
import io.micrometer.observation.ObservationRegistry;
import lombok.extern.log4j.Log4j2;
import org.javacord.api.event.interaction.SlashCommandCreateEvent;
import org.javacord.api.interaction.InteractionBase;
import org.javacord.api.interaction.SlashCommandInteraction;
import org.javacord.api.interaction.SlashCommandInteractionOption;
import org.javacord.api.listener.interaction.SlashCommandCreateListener;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Log4j2
@Component
public class DiscordSlashCommandCreateListener extends AbstractDiscordInteractionListener implements SlashCommandCreateListener {

    private final List<Command> commands;

    public DiscordSlashCommandCreateListener(ObservationRegistry observationRegistry,
                                             List<Command> commands) {
        super(observationRegistry);
        this.commands = commands;
    }

    @Override
    public void onSlashCommandCreate(SlashCommandCreateEvent event) {
        SlashCommandInteraction slashCommandInteraction = event.getSlashCommandInteraction();
        log.info("Handling slashCommandInteraction {} from user {}",
                slashCommandInteraction::getIdAsString,
                () -> slashCommandInteraction.getUser().getIdAsString());
        commands.stream()
                .filter(c -> isSlashCommandOfTypeCommand(slashCommandInteraction, c))
                .findFirst()
                .ifPresent(command -> handleCommand(command, slashCommandInteraction));
    }

    @Override
    protected Map<String, String> getCommandArguments(InteractionBase interaction, Command command) {
        SlashCommandInteraction slashCommandInteraction = (SlashCommandInteraction) interaction;
        return command.getParameters()
                .stream()
                .map(CommandParameter::getName)
                .filter(parameter -> slashCommandInteraction.getOptionByName(parameter).isPresent())
                .collect(Collectors.toMap(
                        Function.identity(),
                        parameter -> getParameterAsString(slashCommandInteraction, parameter)));
    }

    @Override
    protected boolean containsArgument(InteractionBase interaction, String argument) {
        SlashCommandInteraction slashCommandInteraction = (SlashCommandInteraction) interaction;
        return slashCommandInteraction.getOptionByName(argument)
                .isPresent();
    }

    private String getParameterAsString(SlashCommandInteraction slashCommandInteraction, String parameterName) {
        return slashCommandInteraction.getOptionByName(parameterName)
                .flatMap(SlashCommandInteractionOption::getStringRepresentationValue)
                .orElseThrow(() -> new DiscordException(""));
    }

    private boolean isSlashCommandOfTypeCommand(SlashCommandInteraction slashCommandInteraction, Command command) {
        return slashCommandInteraction.getCommandName().equals(command.getName());
    }

}
