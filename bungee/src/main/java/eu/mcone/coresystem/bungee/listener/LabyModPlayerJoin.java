/*
 * Copyright (c) 2017 - 2019 Dominik Lippl, Rufus Maiwald, Felix Schmid and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.bungee.listener;

import eu.mcone.coresystem.api.bungee.event.LabyModPlayerJoinEvent;
import eu.mcone.coresystem.bungee.BungeeCoreSystem;
import eu.mcone.coresystem.core.player.GlobalCorePlayer;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class LabyModPlayerJoin implements Listener {

    @EventHandler
    public void on(LabyModPlayerJoinEvent e) {
        ((GlobalCorePlayer) BungeeCoreSystem.getInstance().getCorePlayer(e.getPlayer())).setLabyModConnection(e.getConnection());
    }

}
