package fun.pancakes.commonspringdiscord.exception;

import lombok.Getter;

@Getter
public class DiscordException extends RuntimeException {

    private String userMessage = "Unexpected Error.";

    public DiscordException(String message) {
        super(message);
    }

    public DiscordException(String message, String userMessage) {
        super(message);
        this.userMessage = userMessage;
    }

    public DiscordException(String message, Throwable cause) {
        super(message, cause);
    }

    public DiscordException(String message, String userMessage, Throwable cause) {
        super(message, cause);
        this.userMessage = userMessage;
    }
}
