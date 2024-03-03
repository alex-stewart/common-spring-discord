package fun.pancakes.commonspringdiscord.command.parameter;

import fun.pancakes.commonspringdiscord.command.CommandParameterChoice;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class ChoiceCommandParameter extends CommandParameter {

    private boolean autocomplete;
    private List<CommandParameterChoice> commandParameterChoices;

    public ChoiceCommandParameter(String name,
                                  String description,
                                  boolean autocomplete,
                                  List<CommandParameterChoice> commandParameterChoices) {
        super();
        this.name = name;
        this.description = description;
        this.autocomplete = autocomplete;
        this.commandParameterChoices = commandParameterChoices;
    }

}
