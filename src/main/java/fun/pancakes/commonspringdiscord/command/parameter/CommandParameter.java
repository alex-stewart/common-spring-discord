package fun.pancakes.commonspringdiscord.command.parameter;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class CommandParameter {

    private String name;
    private String description;

}
