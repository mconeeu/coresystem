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

import static com.mongodb.client.model.Filters.eq;

public class PreferencesManager implements Preferences {

    private Map<Preference, String> preferences;

    public PreferencesManager() {
        this.preferences = new HashMap<>();

        reload();
    }

    @Override
    public void reload() {
        preferences.clear();

        for (Document preferencesDocument : BungeeCoreSystem.getSystem().getMongoDB(Database.SYSTEM).getDocumentsInCollection("bungeesystem_preferences")) {
            preferences.put(Preference.valueOf(preferencesDocument.getString("key")), preferencesDocument.getString("value"));
        }
    }

    @Override
    public void setPreference(Preference preference, String value) {

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
        Document document = BungeeCoreSystem.getSystem().getMongoDB(Database.SYSTEM).getCollection("bungeesystem_preferences").find(eq("key", preference.toString())).first();
        if (document != null) {
            return document.getString("value");
        }
        return null;
    }

    @Override
    public int getLiveInt(Preference preference) {
        Document document = BungeeCoreSystem.getSystem().getMongoDB(Database.SYSTEM).getCollection("bungeesystem_preferences").find(eq("key", preference.toString())).first();
        if (document != null) {
            return document.getInteger("value");
        }
        return 0;
    }

    @Override
    public boolean getLiveBoolean(Preference preference) {
        Document document = BungeeCoreSystem.getSystem().getMongoDB(Database.SYSTEM).getCollection("bungeesystem_preferences").find(eq("key", preference.toString())).first();
        if (document != null) {
            return document.getBoolean("value");
        }
        return false;
    }
}
