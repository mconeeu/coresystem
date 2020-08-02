package eu.mcone.coresystem.api.bungee.overwatch.punish;

import lombok.Getter;
import org.bson.codecs.pojo.annotations.BsonCreator;
import org.bson.codecs.pojo.annotations.BsonDiscriminator;
import org.bson.codecs.pojo.annotations.BsonProperty;

@BsonDiscriminator
public class BanEntry extends PunishEntry {

    @Getter
    private final String replayID;

    public BanEntry(long end, String replayID) {
        super(end);
        this.replayID = replayID;
    }

    @BsonCreator
    public BanEntry(@BsonProperty("end") long end, @BsonProperty("timestamp") long timestamp, @BsonProperty("unPunished") long unPunished, @BsonProperty("replayID") String replayID) {
        super(end, timestamp, unPunished);
        this.replayID = replayID;
    }
}
