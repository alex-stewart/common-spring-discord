package fun.pancakes.commonspringdiscord.service.interaction.autocomplete;

import org.javacord.api.interaction.AutocompleteInteraction;
import org.javacord.api.interaction.SlashCommandOptionChoice;

import java.util.List;

public interface CommandOptionChoiceFactory {
    
    String getCommandParameter();

    List<SlashCommandOptionChoice> autocompleteInteractionCommandOptionChoices(AutocompleteInteraction autocompleteInteraction);

}
