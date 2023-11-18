package fun.pancakes.commonspringdiscord.controller;

import lombok.RequiredArgsConstructor;
import org.javacord.api.DiscordApi;
import org.javacord.api.entity.Deletable;
import org.javacord.api.entity.server.Server;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ChannelController {

    private final DiscordApi discordApi;

    @PostMapping("/discord/channels/purge")
    public void purgeChannels(@RequestParam String discordServerId) {
        Server server = discordApi.getServerById(discordServerId).get();
        server.getChannelCategories().forEach(Deletable::delete);
        server.getChannels().forEach(Deletable::delete);
    }

}
