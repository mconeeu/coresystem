/*
 * Copyright (c) 2017 - 2019 Dominik Lippl, Rufus Maiwald, Felix Schmid and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.bungee.listener;

import eu.mcone.coresystem.api.bungee.CoreSystem;
import eu.mcone.coresystem.api.bungee.event.MoneyChangeEvent;
import eu.mcone.coresystem.api.bungee.player.CorePlayer;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class MoneyChange implements Listener {

    @EventHandler
    public void on(MoneyChangeEvent e) {
        CorePlayer p = e.getPlayer();
        CoreSystem.getInstance().getChannelHandler().createInfoRequest(p.bungee(), "MONEY", String.valueOf(p.getCoins()), e.getCurrency().toString());
    }

}
