package fun.pancakes.commonspringdiscord.controller;

import fun.pancakes.commonspringdiscord.controller.vo.DiscordEmojiSyncVO;
import fun.pancakes.commonspringdiscord.service.emoji.DiscordEmojiService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class EmojiController {

    private final DiscordEmojiService discordEmojiService;

    @PostMapping("/discord/emoji/sync")
    public void discordSyncEmojis(@RequestBody DiscordEmojiSyncVO discordEmojiSyncVO) {
        discordEmojiService.syncEmojis(discordEmojiSyncVO.getDiscordServerId());
    }

}
