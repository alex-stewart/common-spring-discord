package fun.pancakes.commonspringdiscord.service.interaction.autocomplete;

import fun.pancakes.commonspringdiscord.command.CommandParameter;
import org.javacord.api.interaction.AutocompleteInteraction;
import org.javacord.api.interaction.SlashCommandOptionChoice;

import java.util.List;

public interface CommandOptionChoiceFactory {

    int MAXIMUM_SELECT_OPTIONS = 25;

    CommandParameter getCommandParameter();

    List<SlashCommandOptionChoice> autocompleteInteractionCommandOptionChoices(AutocompleteInteraction autocompleteInteraction);

}
