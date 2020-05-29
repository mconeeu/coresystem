package eu.mcone.coresystem.api.bungee.overwatch.punish;

import lombok.Getter;
import org.bson.codecs.pojo.annotations.BsonCreator;
import org.bson.codecs.pojo.annotations.BsonDiscriminator;
import org.bson.codecs.pojo.annotations.BsonProperty;

@Getter
@BsonDiscriminator
public class MuteEntry extends PunishEntry {

    private final String chatLogID;

    public MuteEntry(long end, String chatLogID) {
        super(end);
        this.chatLogID = chatLogID;
    }

    @BsonCreator
    public MuteEntry(@BsonProperty("chatLogID") String chatLogID, @BsonProperty("end") long end, @BsonProperty("timestamp") long timestamp, @BsonProperty("unPunished") long unPunished) {
        super(end, timestamp, unPunished);
        this.chatLogID = chatLogID;
    }
}
