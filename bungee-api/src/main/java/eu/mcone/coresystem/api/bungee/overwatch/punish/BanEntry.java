package eu.mcone.coresystem.api.bungee.overwatch.punish;

import org.bson.codecs.pojo.annotations.BsonCreator;
import org.bson.codecs.pojo.annotations.BsonDiscriminator;
import org.bson.codecs.pojo.annotations.BsonProperty;

@BsonDiscriminator
public class BanEntry extends PunishEntry {

    public BanEntry(long end) {
        super(end);
    }

    @BsonCreator
    public BanEntry(@BsonProperty("end") long end, @BsonProperty("timestamp") long timestamp, @BsonProperty("unPunished") long unPunished) {
        super(end, timestamp, unPunished);
    }
}
