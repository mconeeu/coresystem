/*
 * Copyright (c) 2017 - 2018 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.coresystem.bungee.listener;

import eu.mcone.coresystem.api.bungee.CoreSystem;
import eu.mcone.coresystem.api.bungee.event.CoinsChangeEvent;
import eu.mcone.coresystem.api.bungee.player.CorePlayer;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class CoinsChange implements Listener {

    @EventHandler
    public void on(CoinsChangeEvent e) {
        CorePlayer p = e.getPlayer();
        CoreSystem.getInstance().getChannelHandler().createInfoRequest(p.bungee(), "COINS", String.valueOf(p.getCoins()));
    }

}
