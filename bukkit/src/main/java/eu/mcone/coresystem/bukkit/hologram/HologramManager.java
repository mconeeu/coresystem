/*
 * Copyright (c) 2017 - 2018 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.coresystem.bukkit.hologram;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import eu.mcone.coresystem.bukkit.CoreSystem;
import eu.mcone.coresystem.bukkit.command.HoloCMD;
import eu.mcone.coresystem.bukkit.util.LocationManager;
import eu.mcone.coresystem.lib.mysql.MySQL;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HologramManager implements Listener {

    private MySQL mysql;
    private String server;
    @Getter
    private Map<String, Hologram> holograms;

    public HologramManager(MySQL mysql, String server) {
        this.mysql = mysql;
        this.server = server;

        CoreSystem.getInstance().getServer().getPluginManager().registerEvents(this, CoreSystem.getInstance());
        CoreSystem.getInstance().getCommand("holo").setExecutor(new HoloCMD(this));

        this.reload();
    }

    @EventHandler
    public void on(PlayerJoinEvent e) {
        setHolograms(e.getPlayer());
    }

    @EventHandler
    public void on(PlayerChangedWorldEvent e) {
        Player p = e.getPlayer();

        this.unsetHolograms(p);
        this.setHolograms(p);
    }

    public void reload() {
        if (this.holograms != null) {
            for (Player p : Bukkit.getOnlinePlayers()) {
                this.unsetHolograms(p);
            }
        }

        this.holograms = new HashMap<>();
        this.mysql.select("SELECT * FROM bukkitsystem_holograms WHERE server='"+this.server+"'", rs -> {
            try {
                while (rs.next()) {
                    String json = rs.getString("lines").replaceAll("&", "§");
                    List<String> lines = new Gson().fromJson(json, new TypeToken<List<String>>() {}.getType());
                    this.holograms.put(rs.getString("name"), new Hologram(lines.toArray(new String[lines.size()]), LocationManager.fromJson(rs.getString("location"))));
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });

        for (Player p : Bukkit.getOnlinePlayers()) {
            this.setHolograms(p);
        }
    }

    public void addHologram(String name, Location loc, String line1) {
        String json = LocationManager.toJson(loc);
        this.mysql.update("INSERT INTO bukkitsystem_holograms (`name`, `location`, `lines`, `server`) VALUES ('"+name+"', '"+json+"', '[\""+line1+"\"]', '"+this.server+"') " +
                "ON DUPLICATE KEY UPDATE `location`='"+json+"'");
        this.holograms.put(name, new Hologram(new String[]{line1.replaceAll("&", "§")}, loc));
        this.updateHolograms();
    }

    public void removeHologram(String name) {
        this.mysql.update("DELETE FROM bukkitsystem_holograms WHERE `name`='"+name+"'");
        if (this.holograms.containsKey(name)) {
            this.holograms.get(name).hideAll();
            this.holograms.remove(name);
        }
    }

    public void updateHolograms() {
        for (Player p : Bukkit.getOnlinePlayers()) {
            this.unsetHolograms(p);
            this.setHolograms(p);
        }
    }

    public void setHolograms(Player p) {
        for (Hologram hologram : holograms.values()) {
            hologram.showPlayer(p);
        }
    }

    private void unsetHolograms(Player p) {
        for (Hologram h : this.holograms.values()) {
            h.hidePlayer(p);
        }
    }

    public void unsetHolograms() {
        for (Player p : Bukkit.getOnlinePlayers()) {
            unsetHolograms(p);
        }
    }

}