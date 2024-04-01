package fun.pancakes.commonspringdiscord.config;

import lombok.extern.log4j.Log4j2;
import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Log4j2
@Configuration
@ComponentScan("fun.pancakes.commonspringdiscord.*")
public class DiscordConfig {

    @Bean
    public DiscordProperties discordProperties() {
        return new DiscordProperties();
    }

    @Bean
    public DiscordApi discordApi(DiscordProperties discordProperties) {
        return new DiscordApiBuilder()
                .setToken(discordProperties.getBotToken())
                .login().join();
    }

}
