package fun.pancakes.commonspringdiscord.command.info;

import fun.pancakes.commonspringdiscord.command.Command;
import fun.pancakes.commonspringdiscord.command.CommandRequest;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Log4j2
@Getter
@Component
@RequiredArgsConstructor
public class HelpCommand extends Command {

    private final String name = "help";
    private final String description = "View command help.";

    private final List<Command> commands;

    @Override
    public void handle(CommandRequest commandRequest) {
        String commandList = commands.stream()
                .map(this::formatCommand)
                .collect(Collectors.joining("\n"));

        String content = String.format("**Commands:%n**%s", commandList);
        commandRequest.respondWithSuccess(content);
    }

    private String formatCommand(Command command) {
        return String.format("`/%s` - %s", command.getName(), command.getDescription());
    }

}
