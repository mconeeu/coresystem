/*
 * Copyright (c) 2017 - 2019 Dominik Lippl, Rufus Maiwald, Felix Schmid and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.api.core.player;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.bson.codecs.pojo.annotations.BsonCreator;
import org.bson.codecs.pojo.annotations.BsonDiscriminator;
import org.bson.codecs.pojo.annotations.BsonProperty;

@NoArgsConstructor
@BsonDiscriminator
public class SkinInfo {

    public enum SkinType {
        DATABASE, PLAYER, CUSTOM
    }

    @Getter
    private String name, value, signature;
    @Getter
    private SkinType type;

    @BsonCreator
    public SkinInfo(@BsonProperty("name") String name, @BsonProperty("value") String value,
                    @BsonProperty("signature") String signature, @BsonProperty("typ") SkinType typ) {
        this.name = name;
        this.value = value;
        this.signature = signature;
        this.type = typ;
    }
}
