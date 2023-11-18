package fun.pancakes.commonspringdiscord.command;

import fun.pancakes.commonspringdiscord.service.emoji.ServerEmoji;

public record CommandPromptChoice(String name, String value, ServerEmoji emoji, boolean enabled) {
}
