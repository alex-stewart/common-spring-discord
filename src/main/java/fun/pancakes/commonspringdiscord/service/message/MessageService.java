package fun.pancakes.commonspringdiscord.service.message;

public interface MessageService {

    void sendMessageToLocation(String worldId, String locationId, String message);

    void sendMessageToLocation(String worldId, String locationId, String message, String[] userMentions);

    void sendMessageToUser(String worldId, String userId, String message);

    void sendMessageToUser(String gameName, String userId, String message, String[] userMentions);

}
