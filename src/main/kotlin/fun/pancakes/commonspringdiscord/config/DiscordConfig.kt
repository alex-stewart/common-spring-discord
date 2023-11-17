package `fun`.pancakes.commonspringdiscord.config

import org.javacord.api.DiscordApi
import org.javacord.api.DiscordApiBuilder
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class DiscordConfig {

    @Bean
    fun discordApi(@Value("\${discord.bot.token}") discordBotToken: String?): DiscordApi {
        return DiscordApiBuilder()
            .setToken(discordBotToken)
            .login().join()
    }

}