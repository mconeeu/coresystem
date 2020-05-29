package eu.mcone.coresystem.api.bungee.overwatch.punish;

import lombok.Getter;
import lombok.Setter;
import org.bson.codecs.pojo.annotations.BsonCreator;
import org.bson.codecs.pojo.annotations.BsonDiscriminator;
import org.bson.codecs.pojo.annotations.BsonProperty;

@Getter
@BsonDiscriminator
public abstract class PunishEntry {

    private final long end, timestamp;
    @Setter
    private long unPunished;

    protected PunishEntry(long end) {
        this.end = end;
        this.timestamp = System.currentTimeMillis() / 1000;
    }

    @BsonCreator
    public PunishEntry(@BsonProperty("end") long end, @BsonProperty("timestamp") long timestamp, @BsonProperty("unPunish") long unPunished) {
        this.end = end;
        this.timestamp = timestamp;
        this.unPunished = unPunished;
    }
}
