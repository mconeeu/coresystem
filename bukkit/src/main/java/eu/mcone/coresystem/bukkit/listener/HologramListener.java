/*
 * Copyright (c) 2017 - 2019 Dominik Lippl, Rufus Maiwald, Felix Schmid and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.bukkit.listener;

import eu.mcone.coresystem.bukkit.hologram.CoreHologramManager;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;

@RequiredArgsConstructor
public class HologramListener implements Listener {

    private final CoreHologramManager api;

    @EventHandler
    public void on(PlayerJoinEvent e) {
        api.setHolograms(e.getPlayer());
    }

    @EventHandler
    public void on(PlayerChangedWorldEvent e) {
        Player p = e.getPlayer();

        api.unsetHolograms(p);
        api.setHolograms(p);
    }

}
