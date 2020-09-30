package eu.mcone.coresystem.api.bukkit.broadcast;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bson.codecs.pojo.annotations.BsonDiscriminator;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

@BsonDiscriminator
@NoArgsConstructor
@Getter
@Setter
public abstract class Broadcast {

    private String messageKey;
    private transient Player[] players;

    public Broadcast(String messageKey) {
        this(messageKey, Bukkit.getOnlinePlayers().toArray(new Player[0]));
    }

    public Broadcast(String messageKey, Player... players) {
        if (players.length > 0) {
            this.messageKey = messageKey;
            this.players = players;
        } else throw new IllegalArgumentException("Cannot broadcast Message with key "+messageKey+". Target player array is empty!");
    }

    public Object[] getTranslationReplacements() {
        return null;
    }

}
