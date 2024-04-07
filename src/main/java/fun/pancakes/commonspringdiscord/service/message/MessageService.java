package fun.pancakes.commonspringdiscord.service.message;

public interface MessageService {

    void sendMessageToLocation(String serverId, String locationId, String message);

    void sendMessageToLocation(String serverId, String locationId, String message, String[] userMentions);

    void sendMessageToUser(String serverId, String userId, String message);

    void sendMessageToUser(String serverName, String userId, String message, String[] userMentions);

}
