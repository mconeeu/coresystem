/*
 * Copyright (c) 2017 - 2018 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.coresystem.bukkit.hologram;

import eu.mcone.coresystem.api.bukkit.CoreSystem;
import eu.mcone.coresystem.api.bukkit.hologram.HologramData;
import eu.mcone.coresystem.api.bukkit.world.CoreLocation;
import eu.mcone.coresystem.api.bukkit.world.CoreWorld;
import eu.mcone.coresystem.bukkit.BukkitCoreSystem;
import eu.mcone.coresystem.bukkit.command.HoloCMD;
import eu.mcone.coresystem.bukkit.world.BukkitCoreWorld;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;

public class HologramManager implements Listener, eu.mcone.coresystem.api.bukkit.hologram.HologramManager {

    @Getter
    private Map<String, Hologram> holograms;

    public HologramManager(JavaPlugin plugin) {
        plugin.getServer().getPluginManager().registerEvents(this, BukkitCoreSystem.getInstance());
        plugin.getCommand("holo").setExecutor(new HoloCMD(this));

        reload();
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
        for (CoreWorld w : CoreSystem.getInstance().getWorldManager().getWorlds()) {
            for (HologramData data : ((BukkitCoreWorld) w).getHolograms()) {
                this.holograms.put(data.getName(), new Hologram(data));
            }
        }

        for (Player p : Bukkit.getOnlinePlayers()) {
            this.setHolograms(p);
        }
    }

    public void addHologram(String name, Location loc, String line1) {
        addHologram(new HologramData(name, new CoreLocation(loc), new String[]{line1}));
    }

    public Hologram addHologram(HologramData data) {
        BukkitCoreWorld w = (BukkitCoreWorld) CoreSystem.getInstance().getWorldManager().getWorld(data.getLocation().getWorldName());
        w.getHolograms().add(data);
        w.save();

        Hologram hologram = new Hologram(data);

        this.holograms.put(data.getName(), hologram);
        this.updateHolograms();

        return hologram;
    }

    public void removeHologram(String name) {
        Hologram hologram = holograms.get(name);

        BukkitCoreWorld w = (BukkitCoreWorld) CoreSystem.getInstance().getWorldManager().getWorld(hologram.getData().getLocation().getWorldName());
        w.getHolograms().remove(hologram.getData());
        w.save();

        if (this.holograms.containsKey(name)) {
            hologram.hideAll();
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
            if (hologram.getData().getLocation().bukkit().getWorld().equals(p.getWorld())) {
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