package eu.mcone.coresystem.bungee.overwatch.punish.log;

import eu.mcone.coresystem.bungee.overwatch.Overwatch;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ChatLogManager {

    private final Overwatch overwatch;
    private final Map<UUID, Map<Long, String>> messages;

    public ChatLogManager(Overwatch overwatch) {
        this.overwatch = overwatch;
        this.messages = new HashMap<>();
    }

    public void addChatMessage(ProxiedPlayer player, String message) {
        if (messages.containsKey(player.getUniqueId())) {
            messages.get(player.getUniqueId()).put(System.currentTimeMillis() / 1000, message);
        } else {
            messages.put(player.getUniqueId(), new HashMap<Long, String>() {{
                put(System.currentTimeMillis() / 1000, message);
            }});
        }
    }

    public boolean createChatLog(UUID target, UUID player) {
        if (messages.containsKey(target)) {
            return true;
        }

        return false;
    }

    public Map<Long, Map<String, String>> mergeMessages() {
        return new HashMap<>();
    }

    public boolean sendMessages(UUID player) {
        return messages.containsKey(player);
    }
}
