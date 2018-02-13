/*
 * Copyright (c) 2017 Dominik L., Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.bukkit.listener;

import eu.mcone.coresystem.bukkit.CoreSystem;
import eu.mcone.coresystem.bukkit.event.PermissionChangeEvent;
import eu.mcone.coresystem.bukkit.player.CorePlayer;
import eu.mcone.coresystem.lib.player.Group;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.List;

public class PermissionChange implements Listener {

    @EventHandler
    public void on(PermissionChangeEvent e) {
        final CorePlayer p = e.getPlayer();

        if (e.getKind() == PermissionChangeEvent.Kind.GROUP_PERMISSION) {
            Bukkit.getScheduler().runTaskAsynchronously(CoreSystem.getInstance(), () -> {
                p.setGroup(e.getGroup());
                CoreSystem.getInstance().getPermissionManager().reload();
                List<Group> groups = CoreSystem.getInstance().getPermissionManager().getChildren(e.getGroup());
                for (CorePlayer player : CoreSystem.getOnlineCorePlayers()) {
                    if (groups.contains(player.getGroup())) {
                        player.reloadPermissions();
                        player.bukkit().sendMessage(CoreSystem.config.getConfigValue("Prefix") + "§7§oDeine Permissions wurden upgedated!");
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
                p.setGroup(e.getGroup());
                p.reloadPermissions();
                p.bukkit().sendMessage(CoreSystem.config.getConfigValue("Prefix") + "§7Deine Permission-Gruppe wurde zu §f"+e.getGroup()+" §7geändert!");
                for (CorePlayer player : CoreSystem.getOnlineCorePlayers()) {
                    player.getScoreboard().reload();
                }
            }
        }
    }

}
