/*
 * Copyright (c) 2017 - 2018 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.coresystem.core.player;

import eu.mcone.coresystem.api.core.exception.CoreException;
import eu.mcone.networkmanager.core.api.database.MongoDatabase;
import lombok.Getter;
import org.bson.Document;

import java.util.HashMap;
import java.util.Map;

import static com.mongodb.client.model.Filters.eq;

public class SkinInfo implements eu.mcone.coresystem.api.core.player.SkinInfo {

    private static Map<String, String[]> cachedSkins = new HashMap<>();
    private MongoDatabase database;
    @Getter
    private String name, value, signature;

    public SkinInfo(MongoDatabase database, String name) {
        this.database = database;
        this.name = name;
    }

    SkinInfo(MongoDatabase database, String name, String value, String signature) {
        this.database = database;
        this.name = name;
        this.value = value;
        this.signature = signature;
    }

    @Override
    public eu.mcone.coresystem.api.core.player.SkinInfo downloadSkinData() throws CoreException {
        if (cachedSkins.containsKey(name) && cachedSkins.get(name) != null) {
            value = cachedSkins.get(name)[0];
            signature = cachedSkins.get(name)[1];
        } else {
            Document entry = database.getCollection("bungeesystem_textures").find(eq("name", name)).first();
            if (entry != null) {
                value = entry.getString("texture_value");
                signature = entry.getString("texture_signature");

                cachedSkins.put(name, new String[]{value, signature});
            }

            if (value == null || signature == null)
                throw new CoreException("Skin " + name + " does not exist in the database!");
        }

        return this;
    }

}
