/*
 * Copyright (c) 2017 - 2018 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.coresystem.bungee.listener;

import eu.mcone.coresystem.api.bungee.event.PermissionChangeEvent;
import eu.mcone.coresystem.api.bungee.player.BungeeCorePlayer;
import eu.mcone.coresystem.api.core.player.Group;
import eu.mcone.coresystem.bungee.BungeeCoreSystem;
import eu.mcone.coresystem.bungee.utils.PluginMessage;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.util.Set;

public class PermissionChange implements Listener {

    @EventHandler
    public void on(PermissionChangeEvent e) {
        if (e.getKind() == PermissionChangeEvent.Kind.GROUP_PERMISSION) {
            ProxyServer.getInstance().getScheduler().runAsync(BungeeCoreSystem.getInstance(), () -> {
                BungeeCoreSystem.getInstance().getPermissionManager().reload();
                Set<Group> groups = BungeeCoreSystem.getInstance().getPermissionManager().getChildren((Group) e.getGroups().toArray()[0]);
                for (BungeeCorePlayer player : BungeeCoreSystem.getInstance().getOnlineCorePlayers()) {
                    for (Group g : player.getGroups()) {
                        if (groups.contains(g)) {
                            player.reloadPermissions();
                            break;
                        }
                    }
                }
            });
        } else if (e.getKind() == PermissionChangeEvent.Kind.USER_PERMISSION) {
            final BungeeCorePlayer p = e.getPlayer();

            if (p != null) {
                ProxyServer.getInstance().getScheduler().runAsync(BungeeCoreSystem.getInstance(), () -> {
                    BungeeCoreSystem.getInstance().getPermissionManager().reload();
                    p.reloadPermissions();

                    new PluginMessage("Return", p.bungee().getServer().getInfo(), "EVENT", p.getUuid().toString(), "PermissionChangeEvent", "USER_PERMISSION;");
                });
            }
        } else if (e.getKind() == PermissionChangeEvent.Kind.GROUP_CHANGE) {
            final BungeeCorePlayer p = e.getPlayer();

            if (p != null) {
                p.setGroups(e.getGroups());
                p.reloadPermissions();

                new PluginMessage("Return", p.bungee().getServer().getInfo(), "EVENT", p.getUuid().toString(), "PermissionChangeEvent", "GROUP_CHANGE;"+BungeeCoreSystem.getInstance().getPermissionManager().getJson(e.getGroups()));
            }
        }
    }

}
