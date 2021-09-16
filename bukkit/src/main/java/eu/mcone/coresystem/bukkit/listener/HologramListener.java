/*
 * Copyright (c) 2017 - 2020 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.bukkit.listener;

import eu.mcone.coresystem.api.bukkit.event.player.CorePlayerLoadedEvent;
import eu.mcone.coresystem.api.bukkit.event.world.CoreWorldLoadEvent;
import eu.mcone.coresystem.api.bukkit.hologram.HologramData;
import eu.mcone.coresystem.api.bukkit.world.CoreWorld;
import eu.mcone.coresystem.bukkit.BukkitCoreSystem;
import eu.mcone.coresystem.bukkit.hologram.CoreHologram;
import eu.mcone.coresystem.bukkit.hologram.CoreHologramManager;
import eu.mcone.coresystem.bukkit.world.BukkitCoreWorld;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.world.WorldUnloadEvent;

import java.util.concurrent.atomic.AtomicInteger;

@RequiredArgsConstructor
public class HologramListener implements Listener {

    private final BukkitCoreSystem system;
    private final CoreHologramManager manager;

    @EventHandler
    public void on(CorePlayerLoadedEvent e) {
        Player p = e.getBukkitPlayer();

        Bukkit.getScheduler().runTask(system, () -> {
            for (CoreHologram holo : manager.getHologramSet()) {
                holo.playerJoined(p);
            }
        });
    }

    @EventHandler
    public void on(PlayerQuitEvent e) {
        for (CoreHologram holo : manager.getHologramSet()) {
            holo.playerLeaved(e.getPlayer());
        }
    }

    @EventHandler
    public void on(PlayerChangedWorldEvent e) {
        manager.reload(e.getPlayer());
    }

    @EventHandler
    public void onWorldLoad(CoreWorldLoadEvent e) {
        CoreWorld w = e.getWorld();

        int loaded = 0;
        for (HologramData data : ((BukkitCoreWorld) w).getHologramData()) {
            manager.addHologram(data);
            loaded++;
        }

        if (loaded > 0) {
            system.sendConsoleMessage("§2Loaded "+loaded+" Holograms");
        }
    }

    @EventHandler
    public void onWorldUnload(WorldUnloadEvent e) {
        AtomicInteger unloaded = new AtomicInteger();
        manager.getHologramSet().removeIf(holo -> {
            unloaded.incrementAndGet();
            return holo.getData().getLocation().getWorld().equals(e.getWorld().getName());
        });

        if (unloaded.get() > 0) {
            system.sendConsoleMessage("§2Unloaded "+unloaded.get()+" Holograms");
        }
    }

}
