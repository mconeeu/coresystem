/*
 * Copyright (c) 2017 - 2019 Dominik Lippl, Rufus Maiwald, Felix Schmid and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.core.util;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import eu.mcone.coresystem.api.core.GlobalCoreSystem;
import eu.mcone.coresystem.api.core.player.Currency;
import eu.mcone.coresystem.api.core.player.GlobalCorePlayer;
import org.bson.Document;

import java.util.UUID;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Updates.inc;
import static com.mongodb.client.model.Updates.set;

public abstract class MoneyUtil {

    private final GlobalCoreSystem instance;
    private final MongoCollection<Document> collection;

    protected MoneyUtil(GlobalCoreSystem instance, MongoDatabase database) {
        this.instance = instance;
        this.collection = database.getCollection("userinfo");
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

    public void setCoins(UUID uuid, int amount) {
        instance.runAsync(() -> {
            collection.updateOne(eq("uuid", uuid.toString()), set("coins", amount));
            fireEvent(instance.getGlobalCorePlayer(uuid), Currency.COINS);
        });
    }

    public void addCoins(UUID uuid, int amount) {
        instance.runAsync(() -> {
            collection.updateOne(eq("uuid", uuid.toString()), inc("coins", amount));
            fireEvent(instance.getGlobalCorePlayer(uuid), Currency.COINS);
        });
    }

    public void removeCoins(UUID uuid, int amount) {
        instance.runAsync(() -> {
            collection.updateOne(eq("uuid", uuid.toString()), inc("coins", -amount));
            fireEvent(instance.getGlobalCorePlayer(uuid), Currency.COINS);
        });
    }

    public int getEmeralds(UUID uuid) {
        Document user;
        if ((user = collection.find(eq("uuid", uuid.toString())).first()) != null) {
            return user.getInteger("emeralds");
        } else {
            return 0;
        }
    }

    public int getEmeralds(String name) {
        Document user;
        if ((user = collection.find(eq("name", name)).first()) != null) {
            return user.getInteger("emeralds");
        } else {
            return 0;
        }
    }

    public void setEmeralds(UUID uuid, int amount) {
        instance.runAsync(() -> {
            collection.updateOne(eq("uuid", uuid.toString()), set("emeralds", amount));
            fireEvent(instance.getGlobalCorePlayer(uuid), Currency.EMERALDS);
        });
    }

    public void addEmeralds(UUID uuid, int amount) {
        instance.runAsync(() -> {
            collection.updateOne(eq("uuid", uuid.toString()), inc("emeralds", amount));
            fireEvent(instance.getGlobalCorePlayer(uuid), Currency.EMERALDS);
        });
    }

    public void removeEmeralds(UUID uuid, int amount) {
        instance.runAsync(() -> {
            collection.updateOne(eq("uuid", uuid.toString()), inc("emeralds", -amount));
            fireEvent(instance.getGlobalCorePlayer(uuid), Currency.EMERALDS);
        });
    }

    protected abstract void fireEvent(GlobalCorePlayer player, Currency currency);

}
