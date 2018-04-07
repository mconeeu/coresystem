/*
 * Copyright (c) 2017 - 2018 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.coresystem.lib.player;

import eu.mcone.coresystem.lib.exception.CoreException;
import eu.mcone.coresystem.lib.mysql.MySQL;
import lombok.Getter;

import java.sql.SQLException;

public class Skin {

    private MySQL mySQL;
    @Getter
    private String name, value, signature;

    public Skin(String name, String value, String signature) {
        this.name = name;
        this.value = value;
        this.signature = signature;
    }

    public Skin(MySQL mySQL, String name) {
        this.mySQL = mySQL;
        this.name = name;
    }

    public Skin downloadSkinData() throws CoreException {
        mySQL.select("SELECT * FROM bukkitsystem_textures WHERE name='"+name+"'", rs -> {
            try {
                if (rs.next()) {
                    value = rs.getString("texture_value");
                    signature = rs.getString("texture_signature");
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
        if (value == null || signature == null) throw new CoreException("Skin "+name+" does not exist in the database!");

        return this;
    }

}
