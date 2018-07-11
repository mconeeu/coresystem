/*
 * Copyright (c) 2017 - 2018 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.coresystem.core.player;

import eu.mcone.coresystem.api.core.exception.CoreException;
import eu.mcone.coresystem.api.core.mysql.MySQL;
import lombok.Getter;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class SkinInfo implements eu.mcone.coresystem.api.core.player.SkinInfo {

    private static Map<String, String[]> cachedSkins = new HashMap<>();

    private MySQL mySQL;
    @Getter
    private String name, value, signature;

    public SkinInfo(MySQL mySQL, String name) {
        this.mySQL = mySQL;
        this.name = name;
    }

    SkinInfo(MySQL mySQL, String name, String value, String signature) {
        this.mySQL = mySQL;
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
            mySQL.select("SELECT * FROM bukkitsystem_textures WHERE name='" + name + "'", rs -> {
                try {
                    if (rs.next()) {
                        value = rs.getString("texture_value");
                        signature = rs.getString("texture_signature");

                        cachedSkins.put(name, new String[]{value, signature});
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            });
            if (value == null || signature == null)
                throw new CoreException("Skin " + name + " does not exist in the database!");
        }

        return this;
    }

}
