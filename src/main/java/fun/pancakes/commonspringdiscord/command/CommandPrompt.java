package fun.pancakes.commonspringdiscord.command;

import java.util.List;

public record CommandPrompt(String name, List<CommandPromptChoice> choices) {

}
