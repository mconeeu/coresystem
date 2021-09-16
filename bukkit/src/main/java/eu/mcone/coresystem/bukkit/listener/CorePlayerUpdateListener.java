/*
 * Copyright (c) 2017 - 2020 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.bukkit.listener;

import eu.mcone.coresystem.api.bukkit.CoreSystem;
import eu.mcone.coresystem.api.bukkit.event.player.LanguageChangeEvent;
import eu.mcone.coresystem.api.bukkit.event.player.PermissionChangeEvent;
import eu.mcone.coresystem.api.bukkit.event.player.PlayerSettingsChangeEvent;
import eu.mcone.coresystem.api.bukkit.facades.Msg;
import eu.mcone.coresystem.api.bukkit.player.CorePlayer;
import eu.mcone.coresystem.api.core.player.Group;
import eu.mcone.coresystem.api.core.translation.Language;
import eu.mcone.coresystem.bukkit.BukkitCoreSystem;
import eu.mcone.coresystem.bukkit.inventory.PlayerSettingsInventory;
import eu.mcone.coresystem.core.player.GlobalCorePlayer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;

import java.util.HashSet;
import java.util.Set;

public class CorePlayerUpdateListener implements Listener {

    private static final Set<CorePlayer> LANGUAGE_UPDATES = new HashSet<>();

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
                            Msg.send(player.bukkit(), "§7§oDeine Permissions wurden upgedated!");
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
                    Msg.send(p.bukkit(), "§7§oDeine Permissions wurden upgedated!");
                });
            }
        } else if (e.getType() == PermissionChangeEvent.Type.GROUP_CHANGE) {
            if (p != null) {
                ((GlobalCorePlayer) p).setGroupSet(e.getGroups());
                p.reloadPermissions();

                StringBuilder sb = new StringBuilder();
                e.getGroups().forEach(g -> sb.append(g.getLabel()).append(" "));

                Msg.send(p.bukkit(), "§7Deine Permissions wurden geändert! Du besitzt nun folgende Permissions-Gruppen: "+sb.toString());
                for (CorePlayer player : BukkitCoreSystem.getInstance().getOnlineCorePlayers()) {
                    player.getScoreboard().reload();
                }
            }
        }
    }

    @EventHandler
    public void onSettingsChange(PlayerSettingsChangeEvent e) {
        if (!e.getPlayer().getSettings().getLanguage().equals(e.getOldSettings().getLanguage())) {
            LANGUAGE_UPDATES.add(e.getPlayer());
        }
    }

    @EventHandler
    public void onLanguageChange(LanguageChangeEvent e) {
        e.getPlayer().getScoreboard().reload();
        CoreSystem.getInstance().getHologramManager().reload(e.getPlayer().bukkit());
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent e) {
        CorePlayer cp = CoreSystem.getInstance().getCorePlayer((Player) e.getPlayer());

        if (e.getInventory().getTitle().equals(PlayerSettingsInventory.TITLE) && LANGUAGE_UPDATES.contains(cp)) {
            Language language = cp.getSettings().getLanguage();
            Bukkit.getPluginManager().callEvent(new LanguageChangeEvent(cp, language));
            LANGUAGE_UPDATES.remove(cp);

            if (!BukkitCoreSystem.getSystem().getTranslationManager().getLoadedLanguages().contains(language)) {
                BukkitCoreSystem.getSystem().getTranslationManager().loadAdditionalLanguages(language);
            }
        }
    }

}
