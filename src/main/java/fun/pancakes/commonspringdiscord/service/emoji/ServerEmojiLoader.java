package fun.pancakes.commonspringdiscord.service.emoji;

import lombok.extern.log4j.Log4j2;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

@Log4j2
@Component
public class ServerEmojiLoader {

    private final Resource[] emojiResources;

    private final Map<ServerEmoji, BufferedImage> emojis = new HashMap<>();

    public ServerEmojiLoader(@Value("classpath:emoji/*") Resource[] emojiResources) {
        this.emojiResources = emojiResources;
    }

    public BufferedImage getImage(ServerEmoji serverEmoji) {
        return emojis.get(serverEmoji);
    }

    @EventListener(ApplicationReadyEvent.class)
    public void loadImages() throws IOException {
        log.info("Loading {} emoji images.", emojiResources.length);
        for (Resource emojiResource : emojiResources) {
            try {
                String name = FilenameUtils.removeExtension(emojiResource.getFilename());
                InputStream imgFile = emojiResource.getInputStream();
                emojis.put(new ServerEmoji(name), ImageIO.read(imgFile));
                log.info("Loaded map tile image {}.", name);
            } catch (Exception e) {
                log.error("Failed to load tile image {}", emojis, e);
            }
        }
        log.info("Loaded map tile images.");
    }

}
