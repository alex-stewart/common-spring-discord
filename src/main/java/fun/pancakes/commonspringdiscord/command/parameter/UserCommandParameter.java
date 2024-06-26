package fun.pancakes.commonspringdiscord.command.parameter;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class UserCommandParameter extends CommandParameter {

    public UserCommandParameter(String name, String description) {
        super();
        this.name = name;
        this.description = description;
    }

}
