package fun.pancakes.commonspringdiscord.config;

import lombok.extern.log4j.Log4j2;
import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Log4j2
@Configuration
@ComponentScan("fun.pancakes.commonspringdiscord.*")
public class DiscordConfig {

    @Bean
    public DiscordApi discordApi(@Value("${discord.bot.token}") String discordBotToken) {
        log.debug("Authenticating discord bot with token: {}", discordBotToken);
        return new DiscordApiBuilder()
                .setToken(discordBotToken)
                .login().join();
    }

}
