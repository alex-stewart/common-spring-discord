package fun.pancakes.commonspringdiscord.command.parameter;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
public class NumberCommandParameter extends CommandParameter {

    public NumberCommandParameter(String name, String description) {
        super();
        this.name = name;
        this.description = description;
    }

}
