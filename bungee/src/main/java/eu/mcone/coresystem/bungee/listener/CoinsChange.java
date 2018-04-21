/*
 * Copyright (c) 2017 - 2018 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.coresystem.bungee.listener;

import eu.mcone.coresystem.api.bungee.event.CoinsChangeEvent;
import eu.mcone.coresystem.api.bungee.player.BungeeCorePlayer;
import eu.mcone.coresystem.bungee.utils.PluginMessage;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class CoinsChange implements Listener {

    @EventHandler
    public void on(CoinsChangeEvent e) {
        BungeeCorePlayer p = e.getPlayer();
        new PluginMessage("Return", p.bungee().getServer().getInfo(), "COINS", p.getUuid().toString());
    }

}
