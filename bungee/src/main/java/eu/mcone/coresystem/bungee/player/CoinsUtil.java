/*
 * Copyright (c) 2017 - 2018 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.coresystem.bungee.player;

import eu.mcone.coresystem.api.bungee.event.CoinsChangeEvent;
import eu.mcone.coresystem.api.bungee.player.CorePlayer;
import eu.mcone.coresystem.bungee.BungeeCoreSystem;
import eu.mcone.coresystem.core.mysql.MySQLDatabase;
import eu.mcone.coresystem.core.player.GlobalCorePlayer;
import net.md_5.bungee.api.ProxyServer;

import java.sql.SQLException;
import java.util.UUID;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Updates.*;

public class CoinsUtil implements eu.mcone.coresystem.api.core.player.CoinsUtil {

    private BungeeCoreSystem instance;

    public CoinsUtil(BungeeCoreSystem instance) {
        this.instance = instance;
    }

    public int getCoins(UUID uuid) {
        return (int) instance.getMongoDatabase(eu.mcone.networkmanager.core.api.database.Database.SYSTEM).getObject("uuid", uuid.toString(), "coins", "userinfo").get(0);
    }

    public int getCoins(String name) {
        return (int) instance.getMongoDatabase(eu.mcone.networkmanager.core.api.database.Database.SYSTEM).getObject("name", name, "coins", "userinfo").get(0);
    }

    public void setCoins(final UUID uuid, final int coins) {
        instance.runAsync(() -> {
            instance.getMongoDatabase(eu.mcone.networkmanager.core.api.database.Database.SYSTEM).getCollection("userinfo").updateOne(eq("uuid", uuid.toString()), combine(set("coins", coins)));

            CorePlayer cp = instance.getCorePlayer(uuid);
            if (cp != null) {
                ((GlobalCorePlayer) cp).updateCoinsAmount(coins);
                ProxyServer.getInstance().getPluginManager().callEvent(new CoinsChangeEvent(cp));
            }
        });
    }

    public void addCoins(final UUID uuid, final int coins) {
        instance.runAsync(() -> {
            instance.getMongoDatabase(eu.mcone.networkmanager.core.api.database.Database.SYSTEM).getCollection("userinfo").updateOne(eq("uuid", uuid.toString()), inc("coins", coins));

            CorePlayer cp = instance.getCorePlayer(uuid);
            if (cp != null) {
                ((GlobalCorePlayer) cp).updateCoinsAmount(cp.getCoins() + coins);
                ProxyServer.getInstance().getPluginManager().callEvent(new CoinsChangeEvent(cp));
            }
        });
    }

    public void removeCoins(final UUID uuid, final int coins) {
        instance.runAsync(() -> {
            instance.getMongoDatabase(eu.mcone.networkmanager.core.api.database.Database.SYSTEM).getCollection("userinfo").updateOne(eq("uuid", uuid.toString()), inc("coins", -coins));

            CorePlayer cp = instance.getCorePlayer(uuid);
            if (cp != null) {
                ((GlobalCorePlayer) cp).updateCoinsAmount(cp.getCoins() - coins);
                ProxyServer.getInstance().getPluginManager().callEvent(new CoinsChangeEvent(cp));
            }
        });
    }
}