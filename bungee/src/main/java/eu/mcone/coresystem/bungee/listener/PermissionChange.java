/*
 * Copyright (c) 2017 Dominik L., Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.bungee.listener;

import eu.mcone.coresystem.bungee.CoreSystem;
import eu.mcone.coresystem.bungee.event.PermissionChangeEvent;
import eu.mcone.coresystem.bungee.player.CorePlayer;
import eu.mcone.coresystem.lib.player.Group;
import eu.mcone.coresystem.bungee.utils.PluginMessage;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.util.List;

public class PermissionChange implements Listener {

    @EventHandler
    public void on(PermissionChangeEvent e) {
        final CorePlayer p = e.getPlayer();

        if (e.getKind() == PermissionChangeEvent.Kind.GROUP_PERMISSION) {
            ProxyServer.getInstance().getScheduler().runAsync(CoreSystem.getInstance(), () -> {
                CoreSystem.getInstance().getPermissionManager().reload();
                List<Group> groups = CoreSystem.getInstance().getPermissionManager().getChildren(e.getGroup());
                for (CorePlayer player : CoreSystem.getOnlineCorePlayers()) {
                    if (groups.contains(player.getGroup())) {
                        player.reloadPermissions();
                    }
                }
                new PluginMessage("Return", p.bungee().getServer().getInfo(), "EVENT", p.getUuid().toString(), "PermissionChangeEvent", "GROUP_PERMISSION;"+e.getGroup()+";");
            });
        } else if (e.getKind() == PermissionChangeEvent.Kind.USER_PERMISSION) {
            if (p != null) {
                ProxyServer.getInstance().getScheduler().runAsync(CoreSystem.getInstance(), () -> {
                    CoreSystem.getInstance().getPermissionManager().reload();
                    p.reloadPermissions();
                    new PluginMessage("Return", p.bungee().getServer().getInfo(), "EVENT", p.getUuid().toString(), "PermissionChangeEvent", "USER_PERMISSION;");
                });
            }
        } else if (e.getKind() == PermissionChangeEvent.Kind.GROUP_CHANGE) {
            if (p != null) {
                p.setGroup(e.getGroup());
                p.reloadPermissions();
                new PluginMessage("Return", p.bungee().getServer().getInfo(), "EVENT", p.getUuid().toString(), "PermissionChangeEvent", "GROUP_CHANGE;"+e.getGroup()+";");
            }
        }
    }

}
