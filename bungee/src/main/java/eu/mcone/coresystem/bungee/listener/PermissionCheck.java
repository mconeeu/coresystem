/*
 * Copyright (c) 2017 Dominik L., Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.bungee.listener;

import eu.mcone.coresystem.bungee.CoreSystem;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PermissionCheckEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class PermissionCheck implements Listener {

    @EventHandler(priority = 64)
    public void on(PermissionCheckEvent e) {
        final ProxiedPlayer p = (ProxiedPlayer) e.getSender();
        final String permission = e.getPermission();

        if (p != null) {
            CoreSystem.getCorePlayer(p).hasPermission(permission);
        } else {
            System.out.println("p == null");
        }
    }

}
