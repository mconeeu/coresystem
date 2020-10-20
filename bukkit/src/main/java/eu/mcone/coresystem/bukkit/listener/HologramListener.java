/*
 * Copyright (c) 2017 - 2020 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.bukkit.listener;

import eu.mcone.coresystem.api.bukkit.event.player.CorePlayerLoadedEvent;
import eu.mcone.coresystem.bukkit.BukkitCoreSystem;
import eu.mcone.coresystem.bukkit.hologram.CoreHologram;
import eu.mcone.coresystem.bukkit.hologram.CoreHologramManager;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerQuitEvent;

@RequiredArgsConstructor
public class HologramListener implements Listener {

    private final CoreHologramManager api;

    @EventHandler
    public void on(CorePlayerLoadedEvent e) {
        Player p = e.getBukkitPlayer();

        Bukkit.getScheduler().runTask(BukkitCoreSystem.getSystem(), () -> {
            for (CoreHologram holo : api.getHologramSet()) {
                holo.playerJoined(p);
            }
        });
    }

    @EventHandler
    public void on(PlayerQuitEvent e) {
        for (CoreHologram holo : api.getHologramSet()) {
            holo.playerLeaved(e.getPlayer());
        }
    }

    @EventHandler
    public void on(PlayerChangedWorldEvent e) {
        api.reload(e.getPlayer());
    }

}
