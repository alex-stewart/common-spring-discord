package fun.pancakes.commonspringdiscord.service.interaction.listener;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.javacord.api.DiscordApi;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Log4j2
@Component
@RequiredArgsConstructor
public class DiscordListenerRegistration {

    private final DiscordApi discordApi;
    private final DiscordSlashCommandCreateListener discordSlashCommandCreateListener;
    private final DiscordAutocompleteListener discordAutocompleteListener;
    private final DiscordInteractionCreateListener discordInteractionCreateListener;

    @PostConstruct
    public void registerSlashCommandListener() {
        log.info("Registering slash command interaction listener.");
        discordApi.addSlashCommandCreateListener(discordSlashCommandCreateListener);

        log.info("Registering autocomplete listener.");
        discordApi.addAutocompleteCreateListener(discordAutocompleteListener);

        log.info("Registering interaction create listener.");
        discordApi.addInteractionCreateListener(discordInteractionCreateListener);
    }
}
