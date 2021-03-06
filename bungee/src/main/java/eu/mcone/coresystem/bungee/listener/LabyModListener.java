/*
 * Copyright (c) 2017 - 2020 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.bungee.listener;

import eu.mcone.coresystem.api.bungee.event.LabyModPlayerJoinEvent;
import eu.mcone.coresystem.bungee.BungeeCoreSystem;
import eu.mcone.coresystem.core.player.GlobalCorePlayer;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class LabyModListener implements Listener {

    @EventHandler
    public void onJoin(LabyModPlayerJoinEvent e) {
        ((GlobalCorePlayer) BungeeCoreSystem.getInstance().getCorePlayer(e.getPlayer())).setLabyModConnection(e.getConnection());
    }

}
