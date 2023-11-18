package fun.pancakes.commonspringdiscord.config;

import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DiscordConfig {

    @Bean
    public DiscordApi discordApi(@Value("${discord.bot.token}") String discordBotToken) {
        return new DiscordApiBuilder()
                .setToken(discordBotToken)
                .login().join();
    }

}
