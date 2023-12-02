package fun.pancakes.commonspringdiscord.service.interaction.listener;

import fun.pancakes.commonspringdiscord.command.CommandParameter;
import fun.pancakes.commonspringdiscord.service.interaction.autocomplete.CommandOptionChoiceFactory;
import io.micrometer.observation.annotation.Observed;
import lombok.extern.log4j.Log4j2;
import org.javacord.api.event.interaction.AutocompleteCreateEvent;
import org.javacord.api.interaction.AutocompleteInteraction;
import org.javacord.api.interaction.SlashCommandOptionChoice;
import org.javacord.api.listener.interaction.AutocompleteCreateListener;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Log4j2
@Component
@Observed(name = "discordAutocompleteListener")
public class DiscordAutocompleteListener implements AutocompleteCreateListener {

    private final Map<CommandParameter, CommandOptionChoiceFactory> autoCompleterMap;

    public DiscordAutocompleteListener(List<CommandOptionChoiceFactory> commandOptionChoiceFactoryList) {
        this.autoCompleterMap = commandOptionChoiceFactoryList.stream()
                .collect(Collectors.toMap(CommandOptionChoiceFactory::getCommandParameter, Function.identity()));
    }

    @Override
    public void onAutocompleteCreate(AutocompleteCreateEvent autocompleteCreateEvent) {
        AutocompleteInteraction interaction = autocompleteCreateEvent.getAutocompleteInteraction();
        log.debug("onAutocompleteCreate: {}", interaction.getIdAsString());

        try {
            String optionName = interaction.getFocusedOption().getName();
            CommandParameter commandParameter = CommandParameter.ofName(optionName);
            List<SlashCommandOptionChoice> choices = autoCompleterMap.get(commandParameter)
                    .autocompleteInteractionCommandOptionChoices(interaction);

            interaction.respondWithChoices(choices);
        } catch (Exception e) {
            log.error("Failed to build autocomplete options for interaction: {}", interaction.getIdAsString(), e);
            interaction.createImmediateResponder().setContent("Unknown error").respond();
        }
    }

}
