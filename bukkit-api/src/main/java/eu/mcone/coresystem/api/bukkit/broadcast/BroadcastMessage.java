package eu.mcone.coresystem.api.bukkit.broadcast;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

@NoArgsConstructor
@Getter
public class BroadcastMessage {

    @Setter
    private String messageKey;
    private transient Object[] translationReplacements;
    private transient Player[] receivers;

    public BroadcastMessage(String messageKey) {
        this(messageKey, Bukkit.getOnlinePlayers().toArray(new Player[0]));
    }

    public BroadcastMessage(String messageKey, Player... receivers) {
        this(messageKey, new Object[0], receivers);
    }

    public BroadcastMessage(String messageKey, Object[] translationReplacements, Player... receivers) {
        if (receivers.length > 0) {
            this.messageKey = messageKey;
            this.translationReplacements = translationReplacements;
            this.receivers = receivers;
        } else throw new IllegalArgumentException("Cannot create broadcast message with key "+ messageKey +". Receiver array is empty!");
    }

}
