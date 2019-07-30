/*
 * Copyright (c) 2017 - 2019 Dominik Lippl, Rufus Maiwald, Felix Schmid and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.bukkit.listener;

import eu.mcone.coresystem.api.bukkit.player.CorePlayer;
import eu.mcone.coresystem.bukkit.BukkitCoreSystem;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class VanishListener implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Player p = e.getPlayer();

        if (!p.hasPermission("system.bukkit.vanish.see")) {
            for (CorePlayer t : BukkitCoreSystem.getSystem().getOnlineCorePlayers()) {
                if (t.isVanished()) {
                    p.hidePlayer(BukkitCoreSystem.getInstance(), t.bukkit());
                }
            }
        }
    }

}
