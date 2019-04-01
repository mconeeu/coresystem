/*
 * Copyright (c) 2017 - 2019 Dominik Lippl, Rufus Maiwald, Felix Schmid and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.core.util;

import com.google.gson.internal.Primitives;
import com.mongodb.client.MongoDatabase;
import eu.mcone.coresystem.api.core.util.Preferences;
import org.bson.Document;

import java.util.HashMap;
import java.util.Map;

import static com.mongodb.client.model.Filters.eq;

public class PreferencesManager implements Preferences {

    private final MongoDatabase database;

    private final Map<String, Object> preferences;
    private final Map<String, Object> defaultValues;

    public PreferencesManager(MongoDatabase database, Map<String, Object> defaultValues) {
        this.database = database;
        this.preferences = new HashMap<>();
        this.defaultValues = defaultValues;

        reload();
    }

    @Override
    public void reload() {
        preferences.clear();
        defaultValues.forEach(preferences::put);

        for (Document preferencesDocument : database.getCollection("bungeesystem_preferences").find()) {
            preferences.put(preferencesDocument.getString("key"), preferencesDocument.get("value"));
        }
    }

    @Override
    public void setPreference(String preference, Object value) {
        database.getCollection("bungeesystem_preferences").insertOne(new Document("key", preference).append("value", value));
        preferences.put(preference, value);
    }

    public <T> T get(String preference, Class<T> typeClass) {
         return Primitives.wrap(typeClass).cast(preferences.getOrDefault(preference, null));
    }

    public <T> T getLive(String preference, Class<T> typeClass) {
        Document document = database.getCollection("bungeesystem_preferences").find(eq("key", preference)).first();

        if (document != null) {
            return document.get("value", typeClass);
        } else {
            return null;
        }
    }

}
