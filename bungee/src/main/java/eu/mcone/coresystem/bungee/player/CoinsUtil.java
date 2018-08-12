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

public class CoinsUtil implements eu.mcone.coresystem.api.core.player.CoinsUtil {

    private BungeeCoreSystem instance;

    public CoinsUtil(BungeeCoreSystem instance) {
        this.instance = instance;
    }

    public int getCoins(UUID uuid){
        return instance.getMySQL(MySQLDatabase.SYSTEM).select("SELECT coins FROM userinfo WHERE uuid='" + uuid.toString() + "'", rs -> {
            try {
                if (rs.next()) {
                    return rs.getInt("coins");
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return 0;
        }, int.class);
    }

    public int getCoins(String name) {
        return instance.getMySQL(MySQLDatabase.SYSTEM).select("SELECT coins FROM userinfo WHERE name='" + name + "'", rs -> {
            try {
                if (rs.next()) {
                    return rs.getInt("coins");
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return 0;
        }, int.class);
    }

	public void setCoins(final UUID uuid, final int coins){
        instance.runAsync(() -> {
            instance.getMySQL(MySQLDatabase.SYSTEM).update("UPDATE userinfo SET coins=" + coins + " WHERE uuid='" + uuid.toString() + "'");

            CorePlayer cp = instance.getCorePlayer(uuid);
            if (cp != null) {
                ((GlobalCorePlayer) cp).updateCoinsAmount(coins);
                ProxyServer.getInstance().getPluginManager().callEvent(new CoinsChangeEvent(cp));
            }
        });
	}

	public void addCoins(final UUID uuid, final int coins){
        instance.runAsync(() -> {
            instance.getMySQL(MySQLDatabase.SYSTEM).update("UPDATE userinfo SET coins=coins+" + coins + " WHERE uuid='" + uuid.toString() + "'");

            CorePlayer cp = instance.getCorePlayer(uuid);
            if (cp != null) {
                ((GlobalCorePlayer) cp).updateCoinsAmount(cp.getCoins()+coins);
                ProxyServer.getInstance().getPluginManager().callEvent(new CoinsChangeEvent(cp));
            }
        });
	}

	public void removeCoins(final UUID uuid, final int coins){
        instance.runAsync(() -> {
            instance.getMySQL(MySQLDatabase.SYSTEM).update("UPDATE userinfo SET coins=coins-" + coins + " WHERE uuid='" + uuid.toString() + "'");

            CorePlayer cp = instance.getCorePlayer(uuid);
            if (cp != null) {
                ((GlobalCorePlayer) cp).updateCoinsAmount(cp.getCoins()-coins);
                ProxyServer.getInstance().getPluginManager().callEvent(new CoinsChangeEvent(cp));
            }
        });
	}
}