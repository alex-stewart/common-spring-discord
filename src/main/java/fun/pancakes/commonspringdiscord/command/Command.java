package fun.pancakes.commonspringdiscord.command;

import fun.pancakes.commonspringdiscord.command.parameter.CommandParameter;
import lombok.extern.log4j.Log4j2;

import java.util.ArrayList;
import java.util.List;

@Log4j2
public abstract class Command {

    public abstract String getName();

    public abstract String getDescription();

    public List<CommandParameter> getParameters() {
        return new ArrayList<>();
    }

    public abstract void handle(CommandRequest commandRequest);

}
