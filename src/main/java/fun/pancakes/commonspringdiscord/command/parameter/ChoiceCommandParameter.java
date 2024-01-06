package fun.pancakes.commonspringdiscord.command.parameter;

import fun.pancakes.commonspringdiscord.command.CommandParameterChoice;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Builder
@Data
public class ChoiceCommandParameter extends CommandParameter {

    private boolean autocomplete;
    private List<CommandParameterChoice> commandParameterChoices;

}
