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
import eu.mcone.coresystem.api.core.exception.RuntimeCoreException;
import eu.mcone.coresystem.bukkit.BukkitCoreSystem;
import eu.mcone.coresystem.bukkit.command.HoloCMD;
import eu.mcone.coresystem.bukkit.world.BukkitCoreWorld;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

public class HologramManager implements Listener, eu.mcone.coresystem.api.bukkit.hologram.HologramManager {

    private List<Hologram> holograms;

    public HologramManager(JavaPlugin plugin) {
        plugin.getServer().getPluginManager().registerEvents(this, BukkitCoreSystem.getInstance());
        new HoloCMD(this);

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

    @Override
    public void reload() {
        if (this.holograms != null) {
            for (Player p : Bukkit.getOnlinePlayers()) {
                this.unsetHolograms(p);
            }
        }

        holograms = new ArrayList<>();
        for (CoreWorld w : CoreSystem.getInstance().getWorldManager().getWorlds()) {
            for (HologramData data : ((BukkitCoreWorld) w).getHologramData()) {
                this.holograms.add(new Hologram(w, data));
            }
        }

        for (Player p : Bukkit.getOnlinePlayers()) {
            this.setHolograms(p);
        }
    }

    @Override
    public List<eu.mcone.coresystem.api.bukkit.hologram.Hologram> getHolograms() {
        return new ArrayList<>(holograms);
    }

    public void reload(Player p) {
        unsetHolograms(p);
        setHolograms(p);
    }
    
    public void addHologram(String name, Location loc, String line1) {
        addHologram(new HologramData(name, new CoreLocation(loc), new String[]{line1}), CoreSystem.getInstance().getWorldManager().getWorld(loc.getWorld()));
    }

    @Override
    public Hologram addHologram(HologramData data, CoreWorld world) {
        BukkitCoreWorld w = (BukkitCoreWorld) CoreSystem.getInstance().getWorldManager().getWorld(data.getLocation().getWorldName());
        w.getHologramData().add(data);
        w.save();

        Hologram hologram = new Hologram(world, data);

        this.holograms.add(hologram);
        this.updateHolograms();

        return hologram;
    }

    public void removeHologram(CoreWorld w, String name) {
        Hologram hologram = getHologram(w, name);

        if (hologram != null) {
            removeHologram(hologram);
        } else {
            throw new RuntimeCoreException("Tried to remove Hologram "+name+" on world "+w.getName()+", but npcs list in HologramManager does not cointain it!");
        }
    }

    @Override
    public void removeHologram(eu.mcone.coresystem.api.bukkit.hologram.Hologram hologram) {
        BukkitCoreWorld w = (BukkitCoreWorld) CoreSystem.getInstance().getWorldManager().getWorld(hologram.getData().getLocation().getWorldName());
        w.getHologramData().remove(hologram.getData());
        w.save();

        hologram.hideAll();
        this.holograms.remove(hologram);
    }

    public Hologram getHologram(CoreWorld world, String name) {
        for (Hologram hologram : holograms) {
            if (hologram.getWorld().equals(world) && hologram.getData().getName().equals(name)) {
                return hologram;
            }
        }
        return null;
    }

    @Override
    public void updateHolograms() {
        for (Player p : Bukkit.getOnlinePlayers()) {
            this.unsetHolograms(p);
            this.setHolograms(p);
        }
    }

    public void setHolograms(Player p) {
        for (Hologram hologram : holograms) {
            if (hologram.getData().getLocation().bukkit().getWorld().equals(p.getWorld())) {
                hologram.showPlayer(p);
            }
        }
    }

    private void unsetHolograms(Player p) {
        for (Hologram h : this.holograms) {
            h.hidePlayer(p);
        }
    }

    public void unsetHolograms() {
        for (Player p : Bukkit.getOnlinePlayers()) {
            unsetHolograms(p);
        }
    }

}