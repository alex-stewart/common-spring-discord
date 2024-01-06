package fun.pancakes.commonspringdiscord.service.interaction;

import fun.pancakes.commonspringdiscord.command.Command;
import fun.pancakes.commonspringdiscord.command.CommandParameterChoice;
import fun.pancakes.commonspringdiscord.command.parameter.ChoiceCommandParameter;
import fun.pancakes.commonspringdiscord.command.parameter.CommandParameter;
import fun.pancakes.commonspringdiscord.command.parameter.UserCommandParameter;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.javacord.api.DiscordApi;
import org.javacord.api.interaction.*;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Log4j2
@Component
@ConditionalOnProperty("discord.command.sync.enabled")
@RequiredArgsConstructor
public class SlashCommandSync {

    private final DiscordApi discordApi;
    private final List<Command> commands;

    @EventListener(ApplicationReadyEvent.class)
    public void sync() {
        log.info("Syncing global commands.");
        Set<SlashCommandBuilder> slashCommands = commands.stream()
                .map(this::buildDiscordSlashCommand)
                .collect(Collectors.toSet());
        discordApi.bulkOverwriteGlobalApplicationCommands(slashCommands)
                .whenCompleteAsync((setCommands, ex) -> {
                    if (ex != null) {
                        log.error("Failed to overwrite global commands.", ex);
                    } else {
                        String commandStr = commands.stream()
                                .map(Command::getName)
                                .collect(Collectors.joining(", ", "[ ", " ]"));
                        log.info("Overwritten global commands: {}", commandStr);
                    }
                });
    }

    private SlashCommandBuilder buildDiscordSlashCommand(Command command) {
        List<SlashCommandOption> options = command.getParameters().stream()
                .map(this::buildDiscordSlashCommandOption)
                .toList();

        return new SlashCommandBuilder()
                .setEnabledInDms(false)
                .setName(command.getName())
                .setDescription(command.getDescription())
                .setOptions(options);
    }

    private SlashCommandOption buildDiscordSlashCommandOption(CommandParameter commandParameter) {
        SlashCommandOptionBuilder slashCommandOptionBuilder = new SlashCommandOptionBuilder()
                .setName(commandParameter.getName())
                .setDescription(commandParameter.getDescription())
                .setRequired(true);

        if (commandParameter instanceof ChoiceCommandParameter choiceCommandParameter) {
            slashCommandOptionBuilder
                    .setType(SlashCommandOptionType.STRING)
                    .setAutocompletable(choiceCommandParameter.isAutocomplete());
            if (choiceCommandParameter.getCommandParameterChoices() != null) {
                slashCommandOptionBuilder.setChoices(
                        choiceCommandParameter.getCommandParameterChoices().stream()
                                .map(this::buildDiscordSlashCommandOptionChoice)
                                .collect(Collectors.toList())
                );
            }
        } else if (commandParameter instanceof UserCommandParameter) {
            slashCommandOptionBuilder.setType(SlashCommandOptionType.USER);
        }

        return slashCommandOptionBuilder.build();
    }

    private SlashCommandOptionChoice buildDiscordSlashCommandOptionChoice(CommandParameterChoice commandParameterChoice) {
        return new SlashCommandOptionChoiceBuilder()
                .setName(commandParameterChoice.name())
                .setValue(commandParameterChoice.value())
                .build();
    }

}
