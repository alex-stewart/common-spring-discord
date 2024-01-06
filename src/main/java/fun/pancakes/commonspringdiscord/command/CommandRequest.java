package fun.pancakes.commonspringdiscord.command;

import fun.pancakes.commonspringdiscord.command.parameter.CommandParameter;

import java.awt.image.BufferedImage;
import java.time.Instant;
import java.util.Map;
import java.util.function.Supplier;

public interface CommandRequest {

    Instant getTime();

    String getUserId();

    Map<CommandParameter, String> getArguments();

    void respondWithError(String response);

    void respondWithSuccess(String response);

    void respondWithImage(Supplier<BufferedImage> bufferedImageSupplier, String fileName);

    void respondWithPrompt(CommandPrompt prompt);

}
