package eu.mcone.coresystem.api.core.player;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bson.codecs.pojo.annotations.BsonCreator;
import org.bson.codecs.pojo.annotations.BsonDiscriminator;
import org.bson.codecs.pojo.annotations.BsonProperty;

import java.io.Serializable;
import java.util.Random;

@BsonDiscriminator
@AllArgsConstructor
@Getter
public class Nick implements Serializable {

    private static final Random NICK_RANDOM = new Random();

    private final String name;
    private final Group group;
    private final SkinInfo skinInfo;
    private final int coins;
    private final long onlineTime;

    public Nick(String name, SkinInfo skin) {
        this(
                name,
                NICK_RANDOM.nextInt(1) == 0 ? Group.SPIELER : Group.PREMIUM,
                skin,
                NICK_RANDOM.nextInt(1000),
                NICK_RANDOM.nextInt(100)
        );
    }

    @BsonCreator
    public Nick(@BsonProperty("name") String name,  @BsonProperty("group") Group group, @BsonProperty("texture_value") String textureValue,
                @BsonProperty("texture_signature") String textureSignature, @BsonProperty("coins") int coins, @BsonProperty("onlineTime") long onlineTime) {
        this(
                name,
                group,
                new SkinInfo("nick_" + name, textureValue, textureSignature, SkinInfo.SkinType.CUSTOM),
                coins,
                onlineTime
        );
    }

    @Override
    public String toString() {
        return "Nick{" +
                "name='" + name + '\'' +
                ", group=" + group +
                ", skinInfo=" + skinInfo +
                ", coins=" + coins +
                ", onlineTime=" + onlineTime +
                '}';
    }
}
