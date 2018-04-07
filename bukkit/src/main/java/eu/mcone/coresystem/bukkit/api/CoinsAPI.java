/*
 * Copyright (c) 2017 - 2018 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.coresystem.bukkit.api;

import eu.mcone.coresystem.bukkit.CoreSystem;
import eu.mcone.coresystem.bukkit.event.CoinsChangeEvent;
import org.bukkit.Bukkit;

import java.sql.SQLException;
import java.util.UUID;

public class CoinsAPI {
	
	public static boolean isRegistered(String name){
        return (boolean) CoreSystem.mysql1.select("SELECT coins FROM userinfo WHERE name='" + name + "'", rs -> {
            try {
                return rs.next();
            } catch (SQLException e) {
                e.printStackTrace();
                return false;
            }
        });
    }

	public static int getCoins(UUID uuid){
        return (int) CoreSystem.mysql1.select("SELECT coins FROM userinfo WHERE uuid='" + uuid.toString() + "'", rs -> {
            try {
                if (rs.next()) {
                    return rs.getInt("coins");
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return -1;
        });
	}

    public static int getCoins(String name){
	    return (int) CoreSystem.mysql1.select("SELECT coins FROM userinfo WHERE name='" + name + "'", rs -> {
            try {
                if (rs.next()) {
                    return rs.getInt("coins");
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return -1;
        });
    }



	public static void setCoins(final UUID uuid, final int coins){
        Bukkit.getScheduler().runTaskAsynchronously(CoreSystem.getInstance(), () -> {
            CoreSystem.mysql1.update("UPDATE userinfo SET coins=" + coins + " WHERE uuid='" + uuid.toString() + "'");
            Bukkit.getPluginManager().callEvent(new CoinsChangeEvent(CoreSystem.getCorePlayer(uuid)));
        });
	}

    public static void setCoins(final String name, final int coins){
        Bukkit.getScheduler().runTaskAsynchronously(CoreSystem.getInstance(), () -> {
            CoreSystem.mysql1.update("UPDATE userinfo SET coins=" + coins + " WHERE name='" + name + "'");
            Bukkit.getPluginManager().callEvent(new CoinsChangeEvent(CoreSystem.getCorePlayer(name)));
        });
    }



	public static void addCoins(final UUID uuid, final int coins){
        Bukkit.getScheduler().runTaskAsynchronously(CoreSystem.getInstance(), () -> {
            CoreSystem.mysql1.update("UPDATE userinfo SET coins=coins+" + coins + " WHERE uuid='" + uuid.toString() + "'");
            Bukkit.getPluginManager().callEvent(new CoinsChangeEvent(CoreSystem.getCorePlayer(uuid)));
        });
	}

    public static void addCoins(final String name, final int coins){
        Bukkit.getScheduler().runTaskAsynchronously(CoreSystem.getInstance(), () -> {
            CoreSystem.mysql1.update("UPDATE userinfo SET coins=coins+" + coins + " WHERE name='" + name + "'");
            Bukkit.getPluginManager().callEvent(new CoinsChangeEvent(CoreSystem.getCorePlayer(name)));
        });
    }



	public static void removeCoins(final UUID uuid, final int coins){
        Bukkit.getScheduler().runTaskAsynchronously(CoreSystem.getInstance(), () -> {
            CoreSystem.mysql1.update("UPDATE userinfo SET coins=coins-" + coins + " WHERE uuid='" + uuid.toString() + "'");
            Bukkit.getPluginManager().callEvent(new CoinsChangeEvent(CoreSystem.getCorePlayer(uuid)));
        });
	}

	public static void removeCoins(final String name, int coins){
        Bukkit.getScheduler().runTaskAsynchronously(CoreSystem.getInstance(), () -> {
            CoreSystem.mysql1.update("UPDATE userinfo SET coins=coins-" + coins + " WHERE name='" + name + "'");
            Bukkit.getPluginManager().callEvent(new CoinsChangeEvent(CoreSystem.getCorePlayer(name)));
        });
	}

}