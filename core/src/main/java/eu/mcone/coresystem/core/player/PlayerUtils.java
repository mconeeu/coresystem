/*
 * Copyright (c) 2017 - 2018 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.coresystem.core.player;

import com.google.gson.JsonArray;
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
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.UUID;

import static com.mongodb.client.model.Filters.eq;

public class PlayerUtils implements eu.mcone.coresystem.api.core.player.PlayerUtils {

    private HashMap<String, UUID> cache = new HashMap<>();
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
        if (cache.containsKey(name)) return cache.get(name);

        Document dbEntry = database.getCollection("userinfo").find(eq("name", name)).first();
        if (dbEntry != null) {
            return UUID.fromString(dbEntry.getString("uuid"));
        } else {
            return fetchUuidFromMojangAPI(name);
        }
    }

    @Override
    public String fetchName(final UUID uuid) {
        for (HashMap.Entry<String, UUID> entry : cache.entrySet()) {
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
    public eu.mcone.coresystem.core.player.SkinInfo getSkinInfo(String name, String category) {
        try {
            Document entry = database.getCollection("bungeesystem_textures").find(eq("name", name)).first();

            if (entry == null) {
               String UUIDString = fetchUuid(name).toString().replaceAll("-", "");
               UUID playerUUID = UUID.fromString(UUIDString);

                //SKIN
                URL skinURL = new URL("https://sessionserver.mojang.com/session/minecraft/profile/" + playerUUID + "?unsigned=false");

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

                System.out.println("Return minecraft skinData");
                return new eu.mcone.coresystem.core.player.SkinInfo(database, player_name, value, signature);
            } else {
                if (entry.getString("category").equalsIgnoreCase(category)) {
                    System.out.println("Return database skinData");
                    return new eu.mcone.coresystem.core.player.SkinInfo(database, entry.getString("name"), entry.getString("texture_value"), entry.getString("texture_signature"));
                } else {
                    System.out.println("Cannot find the skin data with the category '" + category +"'");
                    return null;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void uploadSkinInfo(UUID uuid, String category) {
        try {
            String name = fetchName(uuid);

            Document entry = database.getCollection("bungeesystem_textures").find(eq("name", name)).first();
            if (entry == null) {
                //SKIN
                URL skinURL = new URL("https://sessionserver.mojang.com/session/minecraft/profile/" + uuid.toString().replaceAll("-", "") + "?unsigned=false");

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

                database.getCollection("bungeesystem_textures").insertOne
                        (
                                new Document("name", player_name)
                                .append("category", category)
                                .append("texture_value", value)
                                .append("texture_signature", signature)
                        );

            }
        } catch (IOException e) {
            e.printStackTrace();
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
                String uuid = obj.get("id").getAsString();

                UUID uuidResult = UUID.fromString(fromTrimmed(uuid));
                cache.put(name, uuidResult);

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
    public String fetchNameFromMojangAPI(final UUID uuid) {
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

                cache.put(name, uuid);

                return name;
            } catch (IllegalStateException e) {
                return null;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static String fromTrimmed(final String trimmedUUID) throws IllegalArgumentException {
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

    private static String toTrimmed(final String uuid) {
        return uuid.replace("-", "");
    }

}
