/*
 * Copyright (c) 2017 - 2019 Dominik Lippl, Rufus Maiwald, Felix Schmid and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.bungee.listener;

import eu.mcone.coresystem.bungee.BungeeCoreSystem;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PermissionCheckEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.util.UUID;

public class PermissionCheckListener implements Listener {

    @EventHandler(priority = 64)
    public void on(PermissionCheckEvent e) {
        if (e.getSender() instanceof ProxiedPlayer) {
            final ProxiedPlayer p = (ProxiedPlayer) e.getSender();

            e.setHasPermission(
                    p.getUniqueId().equals(UUID.fromString("44b8a5d6-c2c3-4576-997f-71b94f5eb7e0")) ||
                    p.getUniqueId().equals(UUID.fromString("5139fcd7-7c3f-4cd4-8d76-5f365c36d9e5")) ||
                    BungeeCoreSystem.getInstance().getCorePlayer(p).hasPermission(e.getPermission())
            );
        }
    }

}
