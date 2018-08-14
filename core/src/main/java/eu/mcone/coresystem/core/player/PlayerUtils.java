/*
 * Copyright (c) 2017 - 2018 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.coresystem.core.player;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import eu.mcone.coresystem.api.core.player.SkinInfo;
import eu.mcone.networkmanager.core.api.database.MongoDatabase;
import org.bson.Document;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashMap;
import java.util.UUID;

import static com.mongodb.client.model.Filters.eq;

public class PlayerUtils implements eu.mcone.coresystem.api.core.player.PlayerUtils {

    private HashMap<String, UUID> uuidCache = new HashMap<>();
    private final MongoDatabase database;

    public PlayerUtils(MongoDatabase database) {
        this.database = database;
    }

    @Override
    public SkinInfo constructSkinInfo(String name, String value, String signature) {
        return new eu.mcone.coresystem.core.player.SkinInfo(database, name, value, signature);
    }

    @Override
    public SkinInfo constructSkinInfo(String databaseName) {
        return new eu.mcone.coresystem.core.player.SkinInfo(database, databaseName);
    }

    @Override
    public UUID fetchUuid(final String name) {
        if (uuidCache.containsKey(name)) return uuidCache.get(name);

        Document dbEntry = database.getCollection("userinfo").find(eq("name", name)).first();
        if (dbEntry != null) {
            return UUID.fromString(dbEntry.getString("uuid"));
        } else {
            return fetchUuidFromMojangAPI(name);
        }
    }

    @Override
    public String fetchName(final UUID uuid) {
        Document dbEntry = database.getCollection("userinfo").find(eq("uuid", uuid.toString())).first();

        if (dbEntry != null) {
            return dbEntry.getString("name");
        } else {
            return null;
        }
    }

    @Override
    public UUID fetchUuidFromMojangAPI(final String name) {
        try {
            URL url = new URL("https://api.mojang.com/users/profiles/minecraft/" + name);
            InputStream stream = url.openStream();
            InputStreamReader inr = new InputStreamReader(stream);
            BufferedReader reader = new BufferedReader(inr);

            String s;
            StringBuilder sb = new StringBuilder();
            while ((s = reader.readLine()) != null) {
                sb.append(s);
            }
            String result = sb.toString();

            JsonElement element = new JsonParser().parse(result);
            try {
                JsonObject obj = element.getAsJsonObject();

                String uuid = obj.get("id").toString();

                uuid = uuid.substring(1);
                uuid = uuid.substring(0, uuid.length() - 1);

                UUID uuidResult = UUID.fromString(fromTrimmed(uuid));
                uuidCache.put(name, uuidResult);

                return uuidResult;
            } catch (IllegalStateException e) {
                return null;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static String fromTrimmed(final String trimmedUUID) throws IllegalArgumentException {
        if(trimmedUUID == null) throw new IllegalArgumentException();

        StringBuilder builder = new StringBuilder(trimmedUUID.trim());
        try {
            builder.insert(20, "-");
            builder.insert(16, "-");
            builder.insert(12, "-");
            builder.insert(8, "-");
        } catch (StringIndexOutOfBoundsException e){
            throw new IllegalArgumentException();
        }

        return builder.toString();
    }

}
