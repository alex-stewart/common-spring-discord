package fun.pancakes.commonspringdiscord.command.parameter;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class CommandParameter {

    protected String name;
    protected String description;

}
