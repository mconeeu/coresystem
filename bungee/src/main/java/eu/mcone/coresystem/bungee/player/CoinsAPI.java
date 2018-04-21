/*
 * Copyright (c) 2017 - 2018 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.coresystem.bungee.player;

import eu.mcone.coresystem.api.bungee.event.CoinsChangeEvent;
import eu.mcone.coresystem.bungee.BungeeCoreSystem;
import net.md_5.bungee.api.ProxyServer;

import java.sql.SQLException;
import java.util.UUID;

public class CoinsAPI implements eu.mcone.coresystem.api.core.player.CoinsAPI {
	
    private BungeeCoreSystem instance;
    
    public CoinsAPI(BungeeCoreSystem instance) {
        this.instance = instance;
    }
    
	public boolean isRegistered(String name){
        return (boolean) instance.getMySQL(1).select("SELECT coins FROM userinfo WHERE name='" + name + "'", rs -> {
            try {
                return rs.next();
            } catch (SQLException e) {
                e.printStackTrace();
                return false;
            }
        });
    }

	public int getCoins(UUID uuid){
        return (int) instance.getMySQL(1).select("SELECT coins FROM userinfo WHERE uuid='" + uuid.toString() + "'", rs -> {
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

    public int getCoins(String name){
	    return (int) instance.getMySQL(1).select("SELECT coins FROM userinfo WHERE name='" + name + "'", rs -> {
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



	public void setCoins(final UUID uuid, final int coins){
        instance.runAsync(() -> {
            instance.getMySQL(1).update("UPDATE userinfo SET coins=" + coins + " WHERE uuid='" + uuid.toString() + "'");
            ProxyServer.getInstance().getPluginManager().callEvent(new CoinsChangeEvent(instance.getCorePlayer(uuid)));
        });
	}

    public void setCoins(final String name, final int coins){
        instance.runAsync(() -> {
            instance.getMySQL(1).update("UPDATE userinfo SET coins=" + coins + " WHERE name='" + name + "'");
            ProxyServer.getInstance().getPluginManager().callEvent(new CoinsChangeEvent(instance.getCorePlayer(name)));
        });
    }



	public void addCoins(final UUID uuid, final int coins){
        instance.runAsync(() -> {
            instance.getMySQL(1).update("UPDATE userinfo SET coins=coins+" + coins + " WHERE uuid='" + uuid.toString() + "'");
            ProxyServer.getInstance().getPluginManager().callEvent(new CoinsChangeEvent(instance.getCorePlayer(uuid)));
        });
	}

    public void addCoins(final String name, final int coins){
        instance.runAsync(() -> {
            instance.getMySQL(1).update("UPDATE userinfo SET coins=coins+" + coins + " WHERE name='" + name + "'");
            ProxyServer.getInstance().getPluginManager().callEvent(new CoinsChangeEvent(instance.getCorePlayer(name)));
        });
    }



	public void removeCoins(final UUID uuid, final int coins){
        instance.runAsync(() -> {
            instance.getMySQL(1).update("UPDATE userinfo SET coins=coins-" + coins + " WHERE uuid='" + uuid.toString() + "'");
            ProxyServer.getInstance().getPluginManager().callEvent(new CoinsChangeEvent(instance.getCorePlayer(uuid)));
        });
	}

	public void removeCoins(final String name, int coins){
        instance.runAsync(() -> {
            instance.getMySQL(1).update("UPDATE userinfo SET coins=coins-" + coins + " WHERE name='" + name + "'");
            ProxyServer.getInstance().getPluginManager().callEvent(new CoinsChangeEvent(instance.getCorePlayer(name)));
        });
	}
}