/*
 * Copyright (c) 2017 - 2018 Dominik L., Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.coresystem.bukkit.listener;

import eu.mcone.coresystem.bukkit.CoreSystem;
import eu.mcone.coresystem.bukkit.event.PermissionChangeEvent;
import eu.mcone.coresystem.bukkit.player.CorePlayer;
import eu.mcone.coresystem.lib.player.Group;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.Set;

public class PermissionChange implements Listener {

    @EventHandler
    public void on(PermissionChangeEvent e) {
        final CorePlayer p = e.getPlayer();

        if (e.getKind() == PermissionChangeEvent.Kind.GROUP_PERMISSION) {
            Bukkit.getScheduler().runTaskAsynchronously(CoreSystem.getInstance(), () -> {
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
            if (p!=null) {
                Bukkit.getScheduler().runTaskAsynchronously(CoreSystem.getInstance(), () -> {
                    CoreSystem.getInstance().getPermissionManager().reload();
                    p.reloadPermissions();
                    p.bukkit().sendMessage(CoreSystem.config.getConfigValue("Prefix") + "§7§oDeine Permissions wurden upgedated!");
                });
            }
        } else if (e.getKind() == PermissionChangeEvent.Kind.GROUP_CHANGE) {
            if (p != null) {
                p.setGroups(e.getGroups());
                p.reloadPermissions();
                p.bukkit().sendMessage(CoreSystem.config.getConfigValue("Prefix") + "§7Deine Permission-Gruppe wurde zu §f"+e.getGroups()+" §7geändert!");
                for (CorePlayer player : CoreSystem.getOnlineCorePlayers()) {
                    player.getScoreboard().reload();
                }
            }
        }
    }

}
