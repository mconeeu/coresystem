/*
 * Copyright (c) 2017 - 2018 Dominik L., Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.coresystem.bungee.listener;

import eu.mcone.coresystem.bungee.CoreSystem;
import eu.mcone.coresystem.bungee.event.PermissionChangeEvent;
import eu.mcone.coresystem.bungee.player.CorePlayer;
import eu.mcone.coresystem.bungee.utils.PluginMessage;
import eu.mcone.coresystem.lib.player.Group;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.util.Set;

public class PermissionChange implements Listener {

    @EventHandler
    public void on(PermissionChangeEvent e) {
        if (e.getKind() == PermissionChangeEvent.Kind.GROUP_PERMISSION) {
            ProxyServer.getInstance().getScheduler().runAsync(CoreSystem.getInstance(), () -> {
                CoreSystem.getInstance().getPermissionManager().reload();
                Set<Group> groups = CoreSystem.getInstance().getPermissionManager().getChildren((Group) e.getGroups().toArray()[0]);
                for (CorePlayer player : CoreSystem.getOnlineCorePlayers()) {
                    for (Group g : player.getGroups()) {
                        if (groups.contains(g)) {
                            player.reloadPermissions();
                            break;
                        }
                    }
                }
            });
        } else if (e.getKind() == PermissionChangeEvent.Kind.USER_PERMISSION) {
            final CorePlayer p = e.getPlayer();

            if (p != null) {
                ProxyServer.getInstance().getScheduler().runAsync(CoreSystem.getInstance(), () -> {
                    CoreSystem.getInstance().getPermissionManager().reload();
                    p.reloadPermissions();

                    new PluginMessage("Return", p.bungee().getServer().getInfo(), "EVENT", p.getUuid().toString(), "PermissionChangeEvent", "USER_PERMISSION;");
                });
            }
        } else if (e.getKind() == PermissionChangeEvent.Kind.GROUP_CHANGE) {
            final CorePlayer p = e.getPlayer();

            if (p != null) {
                p.setGroups(e.getGroups());
                p.reloadPermissions();

                new PluginMessage("Return", p.bungee().getServer().getInfo(), "EVENT", p.getUuid().toString(), "PermissionChangeEvent", "GROUP_CHANGE;"+Group.getJson(e.getGroups()));
            }
        }
    }

}
