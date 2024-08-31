package fun.pancakes.commonspringdiscord.command.parameter;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
public class FileCommandParameter extends CommandParameter {

    public FileCommandParameter(String name, String description) {
        super();
        this.name = name;
        this.description = description;
    }

}
