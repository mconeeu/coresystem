/*
 * Copyright (c) 2017 - 2020 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.api.core.player;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bson.codecs.pojo.annotations.BsonCreator;
import org.bson.codecs.pojo.annotations.BsonDiscriminator;
import org.bson.codecs.pojo.annotations.BsonProperty;

import java.io.Serializable;
import java.util.Objects;
import java.util.Random;
import java.util.UUID;

@BsonDiscriminator
@AllArgsConstructor
@Getter
public class Nick implements Serializable {

    private static final Random NICK_RANDOM = new Random();

    private final UUID uuid;
    private final String name;
    private final Group group;
    private final SkinInfo skinInfo;
    private final int coins;
    private final long onlineTime;

    public Nick(UUID uuid, String name, SkinInfo skin) {
        this(
                uuid,
                name,
                NICK_RANDOM.nextBoolean() ? Group.SPIELER : Group.PREMIUM,
                skin,
                NICK_RANDOM.nextInt(1000),
                NICK_RANDOM.nextInt(100)
        );
    }

    @BsonCreator
    public Nick(@BsonProperty("nick_uuid") String uuid, @BsonProperty("name") String name, @BsonProperty("group") Group group, @BsonProperty("texture_value") String textureValue,
                @BsonProperty("texture_signature") String textureSignature, @BsonProperty("coins") int coins, @BsonProperty("online_time") long onlineTime) {
        this(
                UUID.fromString(uuid),
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Nick nick = (Nick) o;
        return coins == nick.coins &&
                onlineTime == nick.onlineTime &&
                uuid.equals(nick.uuid) &&
                name.equals(nick.name) &&
                group == nick.group &&
                skinInfo.equals(nick.skinInfo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(uuid, name, group, skinInfo, coins, onlineTime);
    }
}
