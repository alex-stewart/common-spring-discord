package fun.pancakes.commonspringdiscord.service.interaction.listener;

import fun.pancakes.commonspringdiscord.command.CommandParameter;
import fun.pancakes.commonspringdiscord.service.interaction.autocomplete.CommandOptionChoiceFactory;
import io.micrometer.observation.Observation;
import io.micrometer.observation.ObservationRegistry;
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
public class DiscordAutocompleteListener implements AutocompleteCreateListener {

    private final Map<CommandParameter, CommandOptionChoiceFactory> autoCompleterMap;
    private final ObservationRegistry observationRegistry;

    public DiscordAutocompleteListener(List<CommandOptionChoiceFactory> commandOptionChoiceFactoryList,
                                       ObservationRegistry observationRegistry) {
        this.autoCompleterMap = commandOptionChoiceFactoryList.stream()
                .collect(Collectors.toMap(CommandOptionChoiceFactory::getCommandParameter, Function.identity()));
        this.observationRegistry = observationRegistry;
    }


    @Override
    public void onAutocompleteCreate(AutocompleteCreateEvent autocompleteCreateEvent) {
        AutocompleteInteraction interaction = autocompleteCreateEvent.getAutocompleteInteraction();
        String option = interaction.getFocusedOption().getName();

        Observation.createNotStarted("discordAutocomplete", observationRegistry)
                .lowCardinalityKeyValue("option", option)
                .highCardinalityKeyValue("interaction", interaction.getIdAsString())
                .observe(() -> {
                    try {
                        CommandParameter commandParameter = CommandParameter.ofName(option);
                        List<SlashCommandOptionChoice> choices = autoCompleterMap.get(commandParameter)
                                .autocompleteInteractionCommandOptionChoices(interaction);
                        interaction.respondWithChoices(choices);
                    } catch (Exception e) {
                        log.error("Failed to build autocomplete options for interaction: {}", interaction.getIdAsString(), e);
                        interaction.createImmediateResponder().setContent("Unknown error").respond();
                    }
                });
    }

}
