package fun.pancakes.commonspringdiscord.command.parameter;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public abstract class CommandParameter {

    private String name;
    private String description;

}
