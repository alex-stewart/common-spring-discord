package fun.pancakes.commonspringdiscord.command;

import fun.pancakes.commonspringdiscord.exception.DiscordException;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;

@Getter
@AllArgsConstructor
public enum CommandParameter {

    ;

    private final String name;
    private final String description;
    private final boolean autocomplete;
    private final List<CommandParameterChoice> commandParameterChoices;

    public static CommandParameter ofName(String string) {
        return Arrays.stream(CommandParameter.values())
                .filter(c -> c.getName().equals(string))
                .findFirst()
                .orElseThrow(() -> new DiscordException("Invalid CommandOptionType"));
    }

}
