package fun.pancakes.commonspringdiscord.service.interaction.listener;

import fun.pancakes.commonspringdiscord.command.Command;
import fun.pancakes.commonspringdiscord.command.CommandParameter;
import io.micrometer.observation.annotation.Observed;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.javacord.api.event.interaction.SlashCommandCreateEvent;
import org.javacord.api.interaction.InteractionBase;
import org.javacord.api.interaction.SlashCommandInteraction;
import org.javacord.api.listener.interaction.SlashCommandCreateListener;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Log4j2
@Component
@Observed(name = "discordSlashCommandCreateListener")
@RequiredArgsConstructor
public class DiscordSlashCommandCreateListener extends AbstractDiscordInteractionListener implements SlashCommandCreateListener {

    private final List<Command> commands;

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
    protected Map<CommandParameter, String> getCommandArguments(InteractionBase interaction, Command command) {
        SlashCommandInteraction slashCommandInteraction = (SlashCommandInteraction) interaction;
        return command.getParameters()
                .stream()
                .filter(parameter -> slashCommandInteraction.getOptionByName(parameter.getName()).isPresent())
                .collect(Collectors.toMap(
                        Function.identity(),
                        parameter -> slashCommandInteraction.getOptionByName(parameter.getName()).get().getStringValue().get()));
    }

    private boolean isSlashCommandOfTypeCommand(SlashCommandInteraction slashCommandInteraction, Command command) {
        return slashCommandInteraction.getCommandName().equals(command.getName());
    }

}
