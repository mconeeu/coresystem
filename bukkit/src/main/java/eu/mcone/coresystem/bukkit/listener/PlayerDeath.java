/*
 * Copyright (c) 2017 - 2018 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.coresystem.bukkit.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

import java.util.ArrayList;
import java.util.List;

public class PlayerDeath implements Listener {

    public static List<Player> nicking = new ArrayList<>();

    @EventHandler(priority = EventPriority.HIGHEST)
    public void on(PlayerDeathEvent e) {
        Player p = e.getEntity();

        if (nicking.contains(p)) {
            e.setDeathMessage(null);
            e.setKeepInventory(true);
            e.setKeepLevel(true);
        }
    }

}
