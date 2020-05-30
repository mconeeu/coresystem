/*
 * Copyright (c) 2017 - 2020 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.core.player;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.UpdateOptions;
import eu.mcone.coresystem.api.core.GlobalCoreSystem;
import eu.mcone.coresystem.api.core.exception.SkinNotFoundException;
import eu.mcone.coresystem.api.core.player.SkinInfo;
import eu.mcone.coresystem.core.CoreModuleCoreSystem;
import group.onegaming.networkmanager.core.api.database.Database;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Updates.combine;
import static com.mongodb.client.model.Updates.set;

public class PlayerUtils implements eu.mcone.coresystem.api.core.player.PlayerUtils {

    private final HashMap<String, UUID> uuidCache = new HashMap<>();
    private final HashMap<UUID, SkinInfo> skinCache = new HashMap<>();
    private final Map<String, SkinInfo> dbSkinCache = new HashMap<>();
    private final GlobalCoreSystem instance;
    private final MongoDatabase database;

    public PlayerUtils(CoreModuleCoreSystem instance) {
        this.instance = (GlobalCoreSystem) instance;
        this.database = instance.getMongoDB(Database.SYSTEM);
    }

    @Override
    public SkinInfo constructSkinInfo(String name, String value, String signature) {
        return new SkinInfo(name, value, signature, SkinInfo.SkinType.CUSTOM);
    }

    private SkinInfo fetchSkinFromMojangAPI(UUID uuid) {
        try {
            URL skinURL = new URL("https://sessionserver.mojang.com/session/minecraft/profile/" + toTrimmed(uuid.toString()) + "?unsigned=false");

            InputStream skinIS = skinURL.openStream();
            InputStreamReader skinISR = new InputStreamReader(skinIS);
            BufferedReader skinBR = new BufferedReader(skinISR);

            String sSkin;
            StringBuilder sbSkin = new StringBuilder();
            while ((sSkin = skinBR.readLine()) != null) {
                sbSkin.append(sSkin);
            }

            String skinResult = sbSkin.toString();
            JsonElement jsonElement = new JsonParser().parse(skinResult);

            JsonObject jsonObject = jsonElement.getAsJsonObject();
            JsonArray properties = jsonObject.getAsJsonArray("properties");
            JsonObject objectProperties = properties.get(0).getAsJsonObject();

            String player_name = jsonObject.get("name").toString().replaceAll("\"", "");
            String value = objectProperties.get("value").toString().replaceAll("\"", "");
            String signature = objectProperties.get("signature").toString().replaceAll("\"", "");

            skinIS.close();
            skinISR.close();
            skinBR.close();

            SkinInfo skin = new SkinInfo(player_name, value, signature, SkinInfo.SkinType.PLAYER);
            instance.runAsync(() -> updateDatabase(
                    uuid,
                    set("name", player_name),
                    set("texture_value", value),
                    set("texture_signature", signature)
            ));

            skinCache.put(uuid, skin);
            return skin;
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        return null;
    }

    @Override
    public SkinInfo getSkinInfo(UUID uuid) {
        if (skinCache.containsKey(uuid)) {
            return skinCache.get(uuid);
        } else {
            Document entry = database.getCollection("userinfo").find(eq("uuid", uuid.toString())).first();

            if (entry != null) {
                uuidCache.put(entry.getString("name"), uuid);
                return constructSkinInfo(entry.getString("name"), entry.getString("texture_value"), entry.getString("texture_signature"));
            } else {
                return fetchSkinFromMojangAPI(uuid);
            }
        }
    }

    @Override
    public SkinInfo getSkinInfo(String name) {
        return getSkinInfo(fetchUuid(name));
    }

    @Override
    public SkinInfo getSkinFromSkinDatabase(String name) throws SkinNotFoundException {
        if (dbSkinCache.containsKey(name) && dbSkinCache.get(name) != null) {
            return dbSkinCache.get(name);
        } else {
            Document entry = database.getCollection("bungeesystem_textures").find(eq("name", name)).first();
            if (entry != null) {
                SkinInfo skin = new SkinInfo(name, entry.getString("texture_value"), entry.getString("texture_signature"), SkinInfo.SkinType.DATABASE);

                dbSkinCache.put(name, skin);
                return skin;
            } else {
                throw new SkinNotFoundException("Skin " + name + " does not exist in the database!");
            }
        }
    }

    @Override
    public UUID fetchUuid(String name) {
        UUID uuid = getUuidFromCache(name);
        if (uuid != null) return uuid;

        Document dbEntry = database.getCollection("userinfo").find(eq("name", name)).first();
        if (dbEntry != null) {
            return UUID.fromString(dbEntry.getString("uuid"));
        } else {
            return fetchUuidFromMojangAPI(name);
        }
    }

    @Override
    public String fetchName(UUID uuid) {
        for (HashMap.Entry<String, UUID> entry : uuidCache.entrySet()) {
            if (entry.getValue().equals(uuid)) {
                return entry.getKey();
            }
        }

        Document dbEntry = database.getCollection("userinfo").find(eq("uuid", uuid.toString())).first();
        if (dbEntry != null) {
            return dbEntry.getString("name");
        } else {
            return fetchNameFromMojangAPI(uuid);
        }
    }

    @Override
    public UUID fetchUuidFromMojangAPI(String name) {
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
                String rightName = obj.get("name").getAsString();
                String uuid = obj.get("id").getAsString();

                UUID uuidResult = UUID.fromString(fromTrimmed(uuid));
                uuidCache.put(rightName, uuidResult);
                instance.runAsync(() -> updateDatabase(uuidResult, set("name", rightName)));

                return uuidResult;
            } catch (IllegalStateException e) {
                return null;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public String fetchNameFromMojangAPI(UUID uuid) {
        try {
            URL url = new URL("https://sessionserver.mojang.com/session/minecraft/profile/" + toTrimmed(uuid.toString()));
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
                String name = obj.get("name").getAsString();

                uuidCache.put(name, uuid);
                instance.runAsync(() -> updateDatabase(uuid, set("name", name)));

                return name;
            } catch (IllegalStateException e) {
                return null;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private UUID getUuidFromCache(String name) {
        for (Map.Entry<String, UUID> uuidEntry : uuidCache.entrySet()) {
            if (uuidEntry.getKey().equalsIgnoreCase(name)) {
                return uuidEntry.getValue();
            }
        }

        return null;
    }

    private static String fromTrimmed(String trimmedUUID) throws IllegalArgumentException {
        if (trimmedUUID == null) throw new IllegalArgumentException();

        StringBuilder builder = new StringBuilder(trimmedUUID.trim());
        try {
            builder.insert(20, "-");
            builder.insert(16, "-");
            builder.insert(12, "-");
            builder.insert(8, "-");
        } catch (StringIndexOutOfBoundsException e) {
            throw new IllegalArgumentException();
        }

        return builder.toString();
    }

    private static String toTrimmed(String uuid) {
        return uuid.replace("-", "");
    }

    private void updateDatabase(UUID uuid, Bson... data) {
        this.database.getCollection("userinfo").updateOne(
                eq("uuid", uuid.toString()),
                combine(
                        data
                ),
                new UpdateOptions().upsert(true)
        );
    }

}
