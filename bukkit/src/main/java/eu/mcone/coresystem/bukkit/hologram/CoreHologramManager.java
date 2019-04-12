/*
 * Copyright (c) 2017 - 2019 Dominik Lippl, Rufus Maiwald, Felix Schmid and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.bukkit.hologram;

import eu.mcone.coresystem.api.bukkit.CoreSystem;
import eu.mcone.coresystem.api.bukkit.hologram.Hologram;
import eu.mcone.coresystem.api.bukkit.hologram.HologramData;
import eu.mcone.coresystem.api.bukkit.hologram.HologramManager;
import eu.mcone.coresystem.api.bukkit.npc.CoreLocation;
import eu.mcone.coresystem.api.bukkit.world.CoreWorld;
import eu.mcone.coresystem.bukkit.BukkitCoreSystem;
import eu.mcone.coresystem.bukkit.command.HoloCMD;
import eu.mcone.coresystem.bukkit.listener.HologramListener;
import eu.mcone.coresystem.bukkit.world.BukkitCoreWorld;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CoreHologramManager implements HologramManager {

    private Set<CoreHologram> holograms;

    public CoreHologramManager(BukkitCoreSystem instance) {
        instance.registerEvents(new HologramListener(this));
        instance.registerCommands(new HoloCMD(this));

        reload();
    }

    @Override
    public void reload() {
        if (this.holograms != null) {
            for (Player p : Bukkit.getOnlinePlayers()) {
                this.unsetHolograms(p);
            }
        } else {
            holograms = new HashSet<>();
        }

        holograms.clear();
        for (CoreWorld w : CoreSystem.getInstance().getWorldManager().getWorlds()) {
            for (HologramData data : ((BukkitCoreWorld) w).getHologramData()) {
                this.holograms.add(new CoreHologram(data));
            }
        }

        for (Player p : Bukkit.getOnlinePlayers()) {
            this.setHolograms(p);
        }
    }

    @Override
    public List<Hologram> getHolograms() {
        return new ArrayList<>(holograms);
    }

    public void reload(Player p) {
        unsetHolograms(p);
        setHolograms(p);
    }

    public void addHologramAndSave(String name, Location location, String... text) {
        Hologram hologram = addHologram(name, location, text);

        BukkitCoreWorld world = (BukkitCoreWorld) CoreSystem.getInstance().getWorldManager().getWorld(location.getWorld());
        world.getHologramData().add(hologram.getData());
        world.save();
    }

    @Override
    public Hologram addHologram(String name, Location location, String... text) {
        HologramData data = new HologramData(name, text, new CoreLocation(location));
        CoreHologram hologram = new CoreHologram(data);

        this.holograms.add(hologram);
        this.updateHolograms();

        return hologram;
    }

    public void removeHologramAndSave(Hologram hologram) {
        removeHologram(hologram);

        BukkitCoreWorld w = (BukkitCoreWorld) CoreSystem.getInstance().getWorldManager().getWorld(
                hologram.getData().getLocation().getWorld()
        );
        w.getHologramData().remove(hologram.getData());
        w.save();
    }

    @Override
    public void removeHologram(Hologram hologram) {
        hologram.hideAll();
        this.holograms.remove(hologram);
    }

    @Override
    public Hologram getHologram(CoreWorld world, String name) {
        for (CoreHologram hologram : holograms) {
            if (hologram.getData().getLocation().bukkit().getWorld().getName().equals(world.getName()) && hologram.getData().getName().equals(name)) {
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
        for (CoreHologram hologram : holograms) {
            if (hologram.getData().getLocation().bukkit().getWorld().equals(p.getWorld())) {
                hologram.showPlayer(p);
            }
        }
    }

    public void unsetHolograms(Player p) {
        for (CoreHologram h : this.holograms) {
            h.hidePlayer(p);
        }
    }

    public void unsetHolograms() {
        for (Player p : Bukkit.getOnlinePlayers()) {
            unsetHolograms(p);
        }
    }

    public void disable() {
        unsetHolograms();
        holograms.clear();
    }

}