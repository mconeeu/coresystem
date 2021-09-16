package eu.mcone.coresystem.api.bukkit.chat;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bson.codecs.pojo.annotations.BsonDiscriminator;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;

@BsonDiscriminator
@NoArgsConstructor
@Getter
public abstract class Broadcast {

    @Setter
    private BroadcastMessage mainMessage;
    @Setter
    private transient boolean sendMainMessage = true;
    private final transient Set<BroadcastMessage> additionalMessages = new HashSet<>();

    public Broadcast(BroadcastMessage mainMessage) {
        this.mainMessage = mainMessage;
    }

    public Broadcast(BroadcastMessage mainMessage, boolean sendMessage) {
        this.mainMessage = mainMessage;
        this.sendMainMessage = sendMessage;
    }

    public void addAdditionalMessage(String messageKey) {
        additionalMessages.add(
                new BroadcastMessage(messageKey)
        );
    }

    public void addAdditionalMessage(String messageKey, Player... receivers) {
        additionalMessages.add(
                new BroadcastMessage(messageKey, receivers)
        );
    }

    public void addAdditionalMessage(String messageKey, Object[] translationReplacements, Player... receivers) {
        additionalMessages.add(
                new BroadcastMessage(messageKey, translationReplacements, receivers)
        );
    }

}
