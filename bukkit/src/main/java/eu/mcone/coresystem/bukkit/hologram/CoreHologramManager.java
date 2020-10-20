/*
 * Copyright (c) 2017 - 2020 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.bukkit.hologram;

import eu.mcone.coresystem.api.bukkit.CoreSystem;
import eu.mcone.coresystem.api.bukkit.event.hologram.HologramManagerReloadedEvent;
import eu.mcone.coresystem.api.bukkit.hologram.Hologram;
import eu.mcone.coresystem.api.bukkit.hologram.HologramData;
import eu.mcone.coresystem.api.bukkit.hologram.HologramManager;
import eu.mcone.coresystem.api.bukkit.spawnable.ListMode;
import eu.mcone.coresystem.api.bukkit.world.CoreLocation;
import eu.mcone.coresystem.api.bukkit.world.CoreWorld;
import eu.mcone.coresystem.bukkit.BukkitCoreSystem;
import eu.mcone.coresystem.bukkit.command.HoloCMD;
import eu.mcone.coresystem.bukkit.listener.HologramListener;
import eu.mcone.coresystem.bukkit.world.BukkitCoreWorld;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CoreHologramManager implements HologramManager {

    @Getter
    private Set<CoreHologram> hologramSet;

    public CoreHologramManager(BukkitCoreSystem instance) {
        instance.registerEvents(new HologramListener(this));
        instance.registerCommands(new HoloCMD(this));

        reload();
    }

    @Override
    public void reload() {
        if (this.hologramSet != null) {
            for (Hologram holo : hologramSet) {
                holo.togglePlayerVisibility(ListMode.WHITELIST);
            }
        } else {
            hologramSet = new HashSet<>();
        }

        hologramSet.clear();
        for (CoreWorld w : CoreSystem.getInstance().getWorldManager().getWorlds()) {
            for (HologramData data : ((BukkitCoreWorld) w).getHologramData()) {
                addHologram(data);
            }
        }
    }

    @Override
    public void reload(Player p) {
        for (CoreHologram holo : hologramSet) {
            holo.despawn(p);
            if (holo.canBeSeenBy(p)) {
                holo.spawn(p);
            }
        }

        Bukkit.getPluginManager().callEvent(new HologramManagerReloadedEvent(this));
    }

    public void addHologramAndSave(String name, Location location, String... text) {
        Hologram hologram = addHologram(new HologramData(name, text, new CoreLocation(location)));

        BukkitCoreWorld world = (BukkitCoreWorld) CoreSystem.getInstance().getWorldManager().getWorld(location.getWorld());
        world.getHologramData().add(hologram.getData());
        world.save();
    }

    @Override
    public Hologram addHologram(HologramData data) {
        return addHologram(data, ListMode.BLACKLIST);
    }

    @Override
    public Hologram addHologram(HologramData data, ListMode listMode, Player... players) {
        CoreHologram hologram = new CoreHologram(data, listMode, players);
        this.hologramSet.add(hologram);

        return hologram;
    }

    public void updateAndSave(Hologram holo, String[] text, Location location) {
        holo.update(new HologramData(
                holo.getData().getName(),
                text,
                new CoreLocation(location)
        ));
        BukkitCoreSystem.getInstance().getWorldManager().getWorld(holo.getData().getLocation().getWorld()).save();
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
        hologram.togglePlayerVisibility(ListMode.WHITELIST);
        this.hologramSet.remove(hologram);
    }

    @Override
    public Hologram getHologram(CoreWorld world, String name) {
        for (CoreHologram hologram : hologramSet) {
            if (hologram.getData().getLocation().bukkit().getWorld().getName().equals(world.getName()) && hologram.getData().getName().equals(name)) {
                return hologram;
            }
        }
        return null;
    }

    @Override
    public List<Hologram> getHolograms() {
        return new ArrayList<>(hologramSet);
    }

    public void disable() {
        for (CoreHologram holo : hologramSet) {
            holo.togglePlayerVisibility(ListMode.WHITELIST);
        }

        hologramSet.clear();
    }

}