/*
 * Copyright (c) 2017 - 2018 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.coresystem.core.player;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import eu.mcone.coresystem.api.core.mysql.MySQL;
import eu.mcone.coresystem.api.core.player.SkinInfo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.UUID;

public class PlayerUtils implements eu.mcone.coresystem.api.core.player.PlayerUtils {

    private HashMap<String, UUID> uuidCache = new HashMap<>();
    private final MySQL mySQL;

    public PlayerUtils(MySQL mySQL) {
        this.mySQL = mySQL;
    }

    @Override
    public SkinInfo constructSkinInfo(String name, String value, String signature) {
        return new eu.mcone.coresystem.core.player.SkinInfo(mySQL, name, value, signature);
    }

    @Override
    public SkinInfo constructSkinInfo(String databaseName) {
        return new eu.mcone.coresystem.core.player.SkinInfo(mySQL, databaseName);
    }

    @Override
    public UUID fetchUuid(final String name) {
        if (uuidCache.containsKey(name)) return uuidCache.get(name);

        UUID dbUuid = (UUID) mySQL.select("SELECT uuid FROM userinfo WHERE name='" + name + "'", rs -> {
            try {
                if (rs.next()) {
                    return UUID.fromString(rs.getString("uuid"));
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return null;
        });
        if (dbUuid != null) return dbUuid;

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

    @Override
    public String fetchName(final UUID uuid) {
        return (String) mySQL.select("SELECT name FROM userinfo WHERE uuid='" + uuid.toString() + "'", rs -> {
            try {
                if (rs.next()) {
                    return rs.getString("uuid");
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return null;
        });
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
