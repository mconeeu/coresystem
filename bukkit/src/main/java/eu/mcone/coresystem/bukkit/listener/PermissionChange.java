/*
 * Copyright (c) 2017 - 2018 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.coresystem.bukkit.listener;

import eu.mcone.coresystem.api.bukkit.event.PermissionChangeEvent;
import eu.mcone.coresystem.api.bukkit.player.CorePlayer;
import eu.mcone.coresystem.api.core.player.Group;
import eu.mcone.coresystem.bukkit.BukkitCoreSystem;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.Set;

public class PermissionChange implements Listener {

    @EventHandler
    public void on(PermissionChangeEvent e) {
        final CorePlayer p = e.getPlayer();

        if (e.getKind() == PermissionChangeEvent.Kind.GROUP_PERMISSION) {
            Bukkit.getScheduler().runTaskAsynchronously(BukkitCoreSystem.getInstance(), () -> {
                BukkitCoreSystem.getInstance().getPermissionManager().reload();
                Set<Group> groups = BukkitCoreSystem.getInstance().getPermissionManager().getChildren((Group) e.getGroups().toArray()[0]);
                for (CorePlayer player : BukkitCoreSystem.getInstance().getOnlineCorePlayers()) {
                    for (Group g : player.getGroups()) {
                        if (groups.contains(g)) {
                            player.reloadPermissions();
                            break;
                        }
                    }
                }
            });
        } else if (e.getKind() == PermissionChangeEvent.Kind.USER_PERMISSION) {
            if (p!=null) {
                Bukkit.getScheduler().runTaskAsynchronously(BukkitCoreSystem.getInstance(), () -> {
                    BukkitCoreSystem.getInstance().getPermissionManager().reload();
                    p.reloadPermissions();
                    BukkitCoreSystem.getInstance().getMessager().send(p.bukkit(), "§7§oDeine Permissions wurden upgedated!");
                });
            }
        } else if (e.getKind() == PermissionChangeEvent.Kind.GROUP_CHANGE) {
            if (p != null) {
                p.setGroups(e.getGroups());
                p.reloadPermissions();

                StringBuilder sb = new StringBuilder();
                e.getGroups().forEach(g -> sb.append(g.getLabel()).append(" "));

                BukkitCoreSystem.getInstance().getMessager().send(p.bukkit(), "§7Deine Permissions wurden geändert! Du besitzt nun folgende Permissions-Gruppen: "+sb.toString());
                for (CorePlayer player : BukkitCoreSystem.getInstance().getOnlineCorePlayers()) {
                    player.getScoreboard().reload();
                }
            }
        }
    }

}
