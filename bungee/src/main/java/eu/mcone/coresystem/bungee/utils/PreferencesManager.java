/*
 * Copyright (c) 2017 - 2018 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.coresystem.bungee.utils;

import eu.mcone.coresystem.api.bungee.util.Preference;
import eu.mcone.coresystem.api.bungee.util.Preferences;
import eu.mcone.coresystem.api.core.mysql.MySQL;
import eu.mcone.coresystem.bungee.BungeeCoreSystem;
import eu.mcone.networkmanager.core.api.database.Database;
import org.bson.Document;

import java.util.HashMap;
import java.util.Map;

public class PreferencesManager implements Preferences {

    private MySQL mySQL;
    private Map<Preference, String> preferences;

    public PreferencesManager(MySQL mySQL) {
        this.mySQL = mySQL;
        this.preferences = new HashMap<>();

        reload();
    }

    @Override
    public void reload() {
        preferences.clear();

        for (Document preferencesDocument : BungeeCoreSystem.getSystem().getMongoDB(Database.SYSTEM).getDocumentsInCollection("bungeesystem_preferences")) {
            preferences.put(Preference.valueOf(preferencesDocument.getString("key")), preferencesDocument.getString("value"));
        }

        /*
        mySQL.select("SELECT `key`, `value` FROM `bungeesystem_preferences`", rs -> {
            try {
                while (rs.next()) {
                    preferences.put(Preference.valueOf(rs.getString("key")), rs.getString("value"));
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
        */
    }

    @Override
    public void setPreference(Preference preference, String value) {
        if (BungeeCoreSystem.getSystem().getMongoDB(Database.SYSTEM).containsValue("value", value)) {
            System.out.println("preference Problem");
        }

        BungeeCoreSystem.getSystem().getMongoDB(Database.SYSTEM).getCollection("bungeesystem_preferences").insertOne(new Document("key", preference.toString()).append("value", value));
        //mySQL.update("INSERT INTO `bungeesystem_preferences` (`key`, `value`) VALUES ('" + preference.toString() + "', '" + value + "') ON DUPLICATE KEY UPDATE `value`='" + value + "'");
        preferences.put(preference, value);
    }

    @Override
    public String getString(Preference preference) {
        return preferences.get(preference);
    }

    @Override
    public int getInt(Preference preference) {
        return Integer.valueOf(preferences.get(preference));
    }

    @Override
    public boolean getBoolean(Preference preference) {
        return Boolean.valueOf(preferences.get(preference));
    }

    @Override
    public String getLiveString(Preference preference) {
        return (String) BungeeCoreSystem.getSystem().getMongoDB(Database.SYSTEM).getObject("key", preference, "value", "bungeesystem_preferences").get(0);

        /*
        return mySQL.select("SELECT `value` FROM `bungeesystem_preferences` WHERE `key`='" + preference + "'", rs -> {
            try {
                if (rs.next()) {
                    return rs.getString("value");
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return null;
        }, String.class);
        */
    }

    @Override
    public int getLiveInt(Preference preference) {
        return (int) BungeeCoreSystem.getSystem().getMongoDB(Database.SYSTEM).getObject("key", preference, "value", "bungeesystem_preferences").get(0);

        /*
        return mySQL.select("SELECT `value` FROM `bungeesystem_preferences` WHERE `key`='" + preference + "'", rs -> {
            try {
                if (rs.next()) {
                    return Integer.valueOf(rs.getString("value"));
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return null;
        }, int.class);
        */
    }

    @Override
    public boolean getLiveBoolean(Preference preference) {
        return (boolean) BungeeCoreSystem.getSystem().getMongoDB(Database.SYSTEM).getObject("key", preference, "value", "bungeesystem_preferences").get(0);

        /*
        return mySQL.select("SELECT `value` FROM `bungeesystem_preferences` WHERE `key`='" + preference + "'", rs -> {
            try {
                if (rs.next()) {
                    return Boolean.valueOf(rs.getString("value"));
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return null;
        }, boolean.class);
        */
    }
}
