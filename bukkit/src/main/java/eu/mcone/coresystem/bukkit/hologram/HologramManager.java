/*
 * Copyright (c) 2017 - 2018 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.coresystem.bukkit.hologram;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import eu.mcone.coresystem.api.bukkit.CorePlugin;
import eu.mcone.coresystem.api.bukkit.world.CoreLocation;
import eu.mcone.coresystem.bukkit.BukkitCoreSystem;
import eu.mcone.coresystem.bukkit.command.HoloCMD;
import eu.mcone.coresystem.core.mysql.Database;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HologramManager implements Listener, eu.mcone.coresystem.api.bukkit.hologram.HologramManager {

    private BukkitCoreSystem instance;
    private String server;
    @Getter
    private Map<String, Hologram> holograms;

    public HologramManager(BukkitCoreSystem instance, CorePlugin plugin) {
        this.instance = instance;
        this.server = plugin.getPluginName();

        instance.getServer().getPluginManager().registerEvents(this, BukkitCoreSystem.getInstance());
        instance.getCommand("holo").setExecutor(new HoloCMD(this));

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

        holograms = new HashMap<>();
        instance.getMySQL(Database.SYSTEM).select("SELECT * FROM bukkitsystem_holograms WHERE server='" + this.server + "'", rs -> {
            try {
                while (rs.next()) {
                    JsonArray array = new JsonParser().parse(rs.getString("lines").replaceAll("&", "§")).getAsJsonArray();
                    List<String> lines = new ArrayList<>();
                    for (JsonElement jsonElement : array) {
                        lines.add(jsonElement.getAsString());
                    }

                    this.holograms.put(rs.getString("name"), new Hologram(lines.toArray(new String[0]), CoreLocation.fromJson(rs.getString("location")).bukkit()));
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
        String json = new CoreLocation(loc).toJson();
        instance.getMySQL(Database.SYSTEM).update("INSERT INTO bukkitsystem_holograms (`name`, `location`, `lines`, `server`) VALUES ('" + name + "', '" + json + "', '[\"" + line1 + "\"]', '" + this.server + "') " +
                "ON DUPLICATE KEY UPDATE `location`='" + json + "'");
        this.holograms.put(name, new Hologram(new String[]{line1.replaceAll("&", "§")}, loc));
        this.updateHolograms();
    }

    public void removeHologram(String name) {
        instance.getMySQL(Database.SYSTEM).update("DELETE FROM bukkitsystem_holograms WHERE `name`='" + name + "'");
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
            if (hologram.getLocation().getWorld().equals(p.getWorld())) {
                hologram.showPlayer(p);
            }
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