/*
 * Copyright (c) 2017 Dominik L., Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.bukkit.listener;

import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;

public class SignChange implements Listener {

    @EventHandler
    public void on(SignChangeEvent e) {
        if (e.getPlayer().hasPermission("system.bukkit.sign")) {
            e.setLine(0, ChatColor.translateAlternateColorCodes('&', e.getLine(0)));
            e.setLine(1, ChatColor.translateAlternateColorCodes('&', e.getLine(1)));
            e.setLine(2, ChatColor.translateAlternateColorCodes('&', e.getLine(2)));
            e.setLine(3, ChatColor.translateAlternateColorCodes('&', e.getLine(3)));
        }
    }

}
