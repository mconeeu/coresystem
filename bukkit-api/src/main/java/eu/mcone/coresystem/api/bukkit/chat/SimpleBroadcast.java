package eu.mcone.coresystem.api.bukkit.chat;

import lombok.Getter;
import lombok.Setter;
import org.bson.codecs.pojo.annotations.BsonDiscriminator;
import org.bukkit.entity.Player;

@BsonDiscriminator
@Getter @Setter
public class SimpleBroadcast extends Broadcast {

    public SimpleBroadcast(String messageKey) {
        super(new BroadcastMessage(messageKey));
    }

    public SimpleBroadcast(String messageKey, Player... players) {
        super(new BroadcastMessage(messageKey, players));
    }

}
