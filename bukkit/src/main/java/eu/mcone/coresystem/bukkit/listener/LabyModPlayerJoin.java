/*
 * Copyright (c) 2017 - 2019 Dominik Lippl, Rufus Maiwald, Felix Schmid and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.bukkit.listener;

import eu.mcone.coresystem.api.bukkit.event.LabyModPlayerJoinEvent;
import eu.mcone.coresystem.bukkit.BukkitCoreSystem;
import eu.mcone.coresystem.core.player.GlobalCorePlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class LabyModPlayerJoin implements Listener {

    @EventHandler
    public void on(LabyModPlayerJoinEvent e) {
        ((GlobalCorePlayer) BukkitCoreSystem.getInstance().getCorePlayer(e.getPlayer())).setLabyModConnection(e.getConnection());
    }

}
