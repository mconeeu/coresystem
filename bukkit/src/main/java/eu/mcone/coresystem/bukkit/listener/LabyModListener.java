/*
 * Copyright (c) 2017 - 2020 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.bukkit.listener;

import eu.mcone.coresystem.api.bukkit.event.labymod.LabyModPlayerJoinEvent;
import eu.mcone.coresystem.bukkit.BukkitCoreSystem;
import eu.mcone.coresystem.core.player.GlobalCorePlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class LabyModListener implements Listener {

    @EventHandler
    public void onLabyModPlayerJoin(LabyModPlayerJoinEvent e) {
        ((GlobalCorePlayer) BukkitCoreSystem.getInstance().getCorePlayer(e.getPlayer())).setLabyModConnection(e.getConnection());
    }

}
