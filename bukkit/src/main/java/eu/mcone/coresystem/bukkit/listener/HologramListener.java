/*
 * Copyright (c) 2017 - 2020 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.bukkit.listener;

import eu.mcone.coresystem.bukkit.hologram.CoreHologram;
import eu.mcone.coresystem.bukkit.hologram.CoreHologramManager;
import lombok.RequiredArgsConstructor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

@RequiredArgsConstructor
public class HologramListener implements Listener {

    private final CoreHologramManager api;

    @EventHandler
    public void on(PlayerJoinEvent e) {
        for (CoreHologram holo : api.getHologramSet()) {
            holo.playerJoined(e.getPlayer());
        }
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
