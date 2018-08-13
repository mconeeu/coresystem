/*
 * Copyright (c) 2017 - 2018 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.coresystem.bungee.player;

import com.mongodb.client.MongoCollection;
import eu.mcone.coresystem.api.bungee.event.CoinsChangeEvent;
import eu.mcone.coresystem.bungee.BungeeCoreSystem;
import eu.mcone.networkmanager.core.api.database.Database;
import net.md_5.bungee.api.ProxyServer;
import org.bson.Document;

import java.util.UUID;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Updates.inc;
import static com.mongodb.client.model.Updates.set;

public class CoinsUtil implements eu.mcone.coresystem.api.core.player.CoinsUtil {

    private final BungeeCoreSystem instance;
    private final MongoCollection<Document> collection;

    public CoinsUtil(BungeeCoreSystem instance) {
        this.instance = instance;
        this.collection = instance.getMongoDB(Database.SYSTEM).getCollection("userinfo");
    }

    public int getCoins(UUID uuid) {
        Document user;
        if ((user = collection.find(eq("uuid", uuid.toString())).first()) != null) {
            return user.getInteger("coins");
        } else {
            return 0;
        }
    }

    public int getCoins(String name) {
        Document user;
        if ((user = collection.find(eq("name", name)).first()) != null) {
            return user.getInteger("coins");
        } else {
            return 0;
        }
    }

    public void setCoins(final UUID uuid, final int coins) {
        instance.runAsync(() -> {
            collection.updateOne(eq("uuid", uuid.toString()), set("coins", coins));
            ProxyServer.getInstance().getPluginManager().callEvent(new CoinsChangeEvent(instance.getCorePlayer(uuid)));
        });
    }

    public void addCoins(final UUID uuid, final int coins) {
        instance.runAsync(() -> {
            collection.updateOne(eq("uuid", uuid.toString()), inc("coins", coins));
            ProxyServer.getInstance().getPluginManager().callEvent(new CoinsChangeEvent(instance.getCorePlayer(uuid)));
        });
    }

    public void removeCoins(final UUID uuid, final int coins) {
        instance.runAsync(() -> {
            collection.updateOne(eq("uuid", uuid.toString()), inc("coins", -coins));
            ProxyServer.getInstance().getPluginManager().callEvent(new CoinsChangeEvent(instance.getCorePlayer(uuid)));
        });
    }
}