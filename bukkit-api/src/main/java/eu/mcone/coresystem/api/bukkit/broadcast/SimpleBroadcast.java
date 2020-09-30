package eu.mcone.coresystem.api.bukkit.broadcast;

import lombok.Getter;
import lombok.Setter;
import org.bson.codecs.pojo.annotations.BsonDiscriminator;
import org.bukkit.entity.Player;

@BsonDiscriminator
@Getter @Setter
public class SimpleBroadcast extends Broadcast {

    public SimpleBroadcast(String messageKey) {
        super(messageKey);
    }

    public SimpleBroadcast(String messageKey, Player... players) {
        super(messageKey, players);
    }

    @Override
    public Object[] getTranslationReplacements() {
        return null;
    }

}
