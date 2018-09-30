/*
 * Copyright (c) 2017 - 2018 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.coresystem.bukkit.util;

import eu.mcone.coresystem.api.bukkit.util.ItemBuilder;
import eu.mcone.coresystem.api.core.exception.SkinNotFoundException;
import eu.mcone.coresystem.api.core.player.SkinInfo;
import eu.mcone.networkmanager.core.api.database.MongoDatabase;
import org.bson.Document;

import java.util.HashMap;
import java.util.Map;

import static com.mongodb.client.model.Filters.eq;

public class DatabaseSkinManager implements eu.mcone.coresystem.api.bukkit.util.DatabaseSkinManager {

    private Map<String, SkinInfo> skinCache;
    private final MongoDatabase database;

    public DatabaseSkinManager(MongoDatabase database) {
        this.skinCache = new HashMap<>();
        this.database = database;
    }

    @Override
    public eu.mcone.coresystem.api.core.player.SkinInfo getSkin(String name) throws SkinNotFoundException {
        if (skinCache.containsKey(name) && skinCache.get(name) != null) {
            return skinCache.get(name);
        } else {
            Document entry = database.getCollection("bungeesystem_textures").find(eq("name", name)).first();
            if (entry != null) {
                SkinInfo skin = new SkinInfo(name, entry.getString("texture_value"), entry.getString("texture_signature"));

                skinCache.put(name, skin);
                return skin;
            } else {
                throw new SkinNotFoundException("Skin " + name + " does not exist in the database!");
            }
        }
    }

    @Override
    public ItemBuilder getHead(String name, int amount) throws SkinNotFoundException {
        return ItemBuilder.createSkullItem(getSkin(name), amount);
    }

    @Override
    public ItemBuilder getHead(String name) throws SkinNotFoundException {
        return ItemBuilder.createSkullItem(getSkin(name), 1);
    }

}
