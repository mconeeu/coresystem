/*
 * Copyright (c) 2017 - 2020 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.bukkit.listener;

import eu.mcone.coresystem.api.bukkit.event.CorePlayerLoadedEvent;
import eu.mcone.coresystem.api.bukkit.player.CorePlayer;
import eu.mcone.coresystem.bukkit.BukkitCoreSystem;
import eu.mcone.coresystem.bukkit.vanish.CoreVanishManager;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

@RequiredArgsConstructor
public class VanishListener implements Listener {

    private final CoreVanishManager manager;

    @EventHandler
    public void onJoin(CorePlayerLoadedEvent e) {
        Player p = e.getBukkitPlayer();

        if (!p.hasPermission("system.bukkit.vanish.see")) {
            for (CorePlayer t : BukkitCoreSystem.getSystem().getOnlineCorePlayers()) {
                if (t.isVanished()) {
                    BukkitCoreSystem.getSystem().getVanishManager().recalculateVanishes();
                    return;
                }
            }
        }
    }

    @EventHandler
    public void on(PlayerQuitEvent e) {
        manager.playerLeaved(e.getPlayer());
    }

}
