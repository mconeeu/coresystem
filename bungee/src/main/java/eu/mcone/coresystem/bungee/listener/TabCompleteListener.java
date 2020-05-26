/*
 * Copyright (c) 2017 - 2020 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.bungee.listener;

import net.md_5.bungee.api.event.TabCompleteEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class TabCompleteListener implements Listener {

    @EventHandler
    public void on(TabCompleteEvent e) {
        if (e.getCursor().equals("/") || e.getCursor().equals("")) {
            e.setCancelled(true);
        }
    }

}
