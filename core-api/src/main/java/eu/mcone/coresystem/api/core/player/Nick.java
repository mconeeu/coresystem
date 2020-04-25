package eu.mcone.coresystem.api.core.player;

import lombok.Getter;
import lombok.Setter;
import org.bson.codecs.pojo.annotations.BsonCreator;
import org.bson.codecs.pojo.annotations.BsonDiscriminator;
import org.bson.codecs.pojo.annotations.BsonProperty;

import java.util.Random;

@BsonDiscriminator
@Getter
public class Nick {

    @Setter
    private SkinInfo skinInfo;

    private final String name;
    private final String texture;
    private Group group;
    private int coins;
    private int onlineTime;

    @BsonCreator
    public Nick(String name) {
        this.name = name;
        this.texture = "";

        switch (new Random(1).nextInt(3)) {
            case 1:
                this.group = Group.SPIELER;
                break;
            case 2:
                this.group = Group.PREMIUM;
                break;
            case 3:
                this.group = Group.PREMIUMPLUS;
                break;
        }

        this.coins = new Random(1000).nextInt(10000);
        this.onlineTime = new Random(180).nextInt(9000);
    }

    @BsonCreator
    public Nick(@BsonProperty("name") String name, @BsonProperty("texture") String texture, @BsonProperty("group") Group group,
                @BsonProperty("coins") int coins, @BsonProperty("onlineTime") int onlineTime) {
        this.name = name;
        this.texture = texture;
        this.group = group;
        this.coins = coins;
        this.onlineTime = onlineTime;

        if (this.group == null) {
            switch (new Random(1).nextInt(3)) {
                case 1:
                    this.group = Group.SPIELER;
                    break;
                case 2:
                    this.group = Group.PREMIUM;
                    break;
                case 3:
                    this.group = Group.PREMIUMPLUS;
                    break;
            }

            this.group = Group.SPIELER;
        }

        if (coins == 0) {
            this.coins = new Random(1000).nextInt(10000);
        }

        if (onlineTime == 0) {
            this.onlineTime = new Random(180).nextInt(9000);
        }
    }
}
