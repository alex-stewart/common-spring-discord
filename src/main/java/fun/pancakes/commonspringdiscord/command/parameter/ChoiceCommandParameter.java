package fun.pancakes.commonspringdiscord.command.parameter;

import fun.pancakes.commonspringdiscord.command.CommandParameterChoice;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChoiceCommandParameter extends CommandParameter {

    private boolean autocomplete;
    private List<CommandParameterChoice> commandParameterChoices;

}
