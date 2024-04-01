package fun.pancakes.commonspringdiscord.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "discord")
public class DiscordProperties {

    private String botToken;
    private boolean syncOnStartup = false;

}
