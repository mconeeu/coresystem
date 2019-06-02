/*
 * Copyright (c) 2017 - 2019 Dominik Lippl, Rufus Maiwald, Felix Schmid and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.bukkit.listener;

import eu.mcone.coresystem.api.bukkit.CoreSystem;
import eu.mcone.coresystem.api.bukkit.event.LanguageChangeEvent;
import eu.mcone.coresystem.api.bukkit.event.PermissionChangeEvent;
import eu.mcone.coresystem.api.bukkit.event.PlayerSettingsChangeEvent;
import eu.mcone.coresystem.api.bukkit.player.CorePlayer;
import eu.mcone.coresystem.api.core.player.Group;
import eu.mcone.coresystem.bukkit.BukkitCoreSystem;
import eu.mcone.coresystem.bukkit.hologram.CoreHologramManager;
import eu.mcone.coresystem.bukkit.player.BukkitCorePlayer;
import eu.mcone.coresystem.core.player.GlobalCorePlayer;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.Set;

public class CorePlayerUpdateListener implements Listener {

    @EventHandler
    public void onPermissionChange(PermissionChangeEvent e) {
        final CorePlayer p = e.getPlayer();

        if (e.getType() == PermissionChangeEvent.Type.GROUP_PERMISSION) {
            Bukkit.getScheduler().runTaskAsynchronously(BukkitCoreSystem.getInstance(), () -> {
                BukkitCoreSystem.getInstance().getPermissionManager().reload();

                Group target = e.getGroups().iterator().next();
                Set<Group> groups = BukkitCoreSystem.getInstance().getPermissionManager().getChildren(target);
                groups.add(target);

                for (CorePlayer player : BukkitCoreSystem.getInstance().getOnlineCorePlayers()) {
                    for (Group g : player.getGroups()) {
                        if (groups.contains(g)) {
                            player.reloadPermissions();
                            BukkitCoreSystem.getInstance().getMessager().send(player.bukkit(), "§7§oDeine Permissions wurden upgedated!");
                            break;
                        }
                    }
                }
            });
        } else if (e.getType() == PermissionChangeEvent.Type.USER_PERMISSION) {
            if (p!=null) {
                Bukkit.getScheduler().runTaskAsynchronously(BukkitCoreSystem.getInstance(), () -> {
                    BukkitCoreSystem.getInstance().getPermissionManager().reload();
                    p.reloadPermissions();
                    BukkitCoreSystem.getInstance().getMessager().send(p.bukkit(), "§7§oDeine Permissions wurden upgedated!");
                });
            }
        } else if (e.getType() == PermissionChangeEvent.Type.GROUP_CHANGE) {
            if (p != null) {
                ((GlobalCorePlayer) p).setGroupSet(e.getGroups());
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

    @EventHandler
    public void onSettingsChange(PlayerSettingsChangeEvent e) {
        if (!e.getPlayer().getSettings().getLanguage().equals(e.getSettings().getLanguage())) {
            Bukkit.getPluginManager().callEvent(new LanguageChangeEvent(e.getPlayer(), e.getSettings().getLanguage()));
        }

        ((BukkitCorePlayer) e.getPlayer()).setSettings(e.getSettings());
    }

    @EventHandler
    public void onLanguageChange(LanguageChangeEvent e) {
        e.getPlayer().getScoreboard().reload();
        ((CoreHologramManager) CoreSystem.getInstance().getHologramManager()).reload(e.getPlayer().bukkit());
    }

}
