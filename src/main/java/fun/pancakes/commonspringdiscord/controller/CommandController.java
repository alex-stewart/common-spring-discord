package fun.pancakes.commonspringdiscord.controller;

import fun.pancakes.commonspringdiscord.service.interaction.SlashCommandSync;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class CommandController {

    private final SlashCommandSync slashCommandSync;

    @PostMapping("/discord/commands/sync")
    public void syncGlobalCommands() {
        slashCommandSync.sync();
    }

}
