/*
 * Copyright (c) 2017 - 2018 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.coresystem.bukkit.player;

import eu.mcone.coresystem.api.bukkit.event.CoinsChangeEvent;
import eu.mcone.coresystem.bukkit.BukkitCoreSystem;
import eu.mcone.coresystem.core.mysql.Database;
import org.bukkit.Bukkit;

import java.sql.SQLException;
import java.util.UUID;

public class CoinsUtil implements eu.mcone.coresystem.api.core.player.CoinsUtil {

    private BukkitCoreSystem instance;

    public CoinsUtil(BukkitCoreSystem instance) {
        this.instance = instance;
    }

    public int getCoins(UUID uuid){
        return instance.getMySQL(Database.SYSTEM).select("SELECT coins FROM userinfo WHERE uuid='" + uuid.toString() + "'", rs -> {
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

    public int getCoins(String name){
        return instance.getMySQL(Database.SYSTEM).select("SELECT coins FROM userinfo WHERE name='" + name + "'", rs -> {
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
        Bukkit.getScheduler().runTaskAsynchronously(BukkitCoreSystem.getInstance(), () -> {
            instance.getMySQL(Database.SYSTEM).update("UPDATE userinfo SET coins=" + coins + " WHERE uuid='" + uuid.toString() + "'");
            instance.getServer().getPluginManager().callEvent(new CoinsChangeEvent(instance.getCorePlayer(uuid)));
        });
	}

	public void addCoins(final UUID uuid, final int coins){
        Bukkit.getScheduler().runTaskAsynchronously(BukkitCoreSystem.getInstance(), () -> {
            instance.getMySQL(Database.SYSTEM).update("UPDATE userinfo SET coins=coins+" + coins + " WHERE uuid='" + uuid.toString() + "'");
            instance.getServer().getPluginManager().callEvent(new CoinsChangeEvent(instance.getCorePlayer(uuid)));
        });
	}

	public void removeCoins(final UUID uuid, final int coins){
        Bukkit.getScheduler().runTaskAsynchronously(BukkitCoreSystem.getInstance(), () -> {
            instance.getMySQL(Database.SYSTEM).update("UPDATE userinfo SET coins=coins-" + coins + " WHERE uuid='" + uuid.toString() + "'");
            instance.getServer().getPluginManager().callEvent(new CoinsChangeEvent(instance.getCorePlayer(uuid)));
        });
	}

}