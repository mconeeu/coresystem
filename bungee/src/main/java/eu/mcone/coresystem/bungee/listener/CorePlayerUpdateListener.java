/*
 * Copyright (c) 2017 - 2020 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.bungee.listener;

import eu.mcone.coresystem.api.bungee.CoreSystem;
import eu.mcone.coresystem.api.bungee.event.LanguageChangeEvent;
import eu.mcone.coresystem.api.bungee.event.MoneyChangeEvent;
import eu.mcone.coresystem.api.bungee.event.PermissionChangeEvent;
import eu.mcone.coresystem.api.bungee.event.PlayerSettingsChangeEvent;
import eu.mcone.coresystem.api.bungee.player.CorePlayer;
import eu.mcone.coresystem.api.core.player.Currency;
import eu.mcone.coresystem.api.core.player.Group;
import eu.mcone.coresystem.bungee.BungeeCoreSystem;
import eu.mcone.coresystem.core.player.GlobalOfflineCorePlayer;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class CorePlayerUpdateListener implements Listener {

    @EventHandler
    public void onPermissionChange(PermissionChangeEvent e) {
        if (e.getType() == PermissionChangeEvent.Type.GROUP_PERMISSION) {
            ProxyServer.getInstance().getScheduler().runAsync(BungeeCoreSystem.getInstance(), () -> {
                BungeeCoreSystem.getInstance().getPermissionManager().reload();

                Group target = e.getGroups().iterator().next();

                Map<ServerInfo, ProxiedPlayer> servers = new HashMap<>();
                Set<Group> groups = BungeeCoreSystem.getInstance().getPermissionManager().getChildren(target);
                groups.add(target);
                for (CorePlayer player : BungeeCoreSystem.getInstance().getOnlineCorePlayers()) {
                    ProxiedPlayer p = player.bungee();
                    if (!servers.containsKey(p.getServer().getInfo())) {
                        servers.put(p.getServer().getInfo(), p);
                    }

                    for (Group g : player.getGroups()) {
                        if (groups.contains(g)) {
                            player.reloadPermissions();
                            break;
                        }
                    }
                }

                for (ProxiedPlayer p : servers.values()) {
                    CoreSystem.getInstance().getChannelHandler().createInfoRequest(p, "EVENT", "PermissionChangeEvent", "GROUP_PERMISSION;[" + target.getId() + "]");
                }
            });
        } else if (e.getType() == PermissionChangeEvent.Type.USER_PERMISSION) {
            final CorePlayer p = e.getPlayer();

            if (p != null) {
                ProxyServer.getInstance().getScheduler().runAsync(BungeeCoreSystem.getInstance(), () -> {
                    BungeeCoreSystem.getInstance().getPermissionManager().reload();
                    p.reloadPermissions();

                    CoreSystem.getInstance().getChannelHandler().createInfoRequest(p.bungee(), "EVENT", "PermissionChangeEvent", "USER_PERMISSION;");
                });
            }
        } else if (e.getType() == PermissionChangeEvent.Type.GROUP_CHANGE) {
            final CorePlayer p = e.getPlayer();

            if (p != null) {
                ((GlobalOfflineCorePlayer) p).setGroupSet(e.getGroups());
                p.reloadPermissions();

                CoreSystem.getInstance().getChannelHandler().createInfoRequest(
                        p.bungee(),
                        "EVENT",
                        "PermissionChangeEvent",
                        "GROUP_CHANGE;" + CoreSystem.getInstance().getGson().toJson(
                                CoreSystem.getInstance().getPermissionManager().getGroupIDs(e.getGroups())
                        )
                );
            }
        }
    }

    @EventHandler
    public void onMoneyChange(MoneyChangeEvent e) {
        CorePlayer p = e.getPlayer();
        CoreSystem.getInstance().getChannelHandler().createInfoRequest(p.bungee(), "EVENT", "MoneyChangeEvent", e.getCurrency().toString(), String.valueOf(e.getCurrency().equals(Currency.COINS) ? p.getCoins() : p.getEmeralds()));
    }

    @EventHandler
    public void onSettingsChange(PlayerSettingsChangeEvent e) {
        if (!e.getPlayer().getSettings().getLanguage().equals(e.getSettings().getLanguage())) {
            ProxyServer.getInstance().getPluginManager().callEvent(new LanguageChangeEvent(e.getPlayer(), e.getSettings().getLanguage()));
        }

        ((GlobalOfflineCorePlayer) e.getPlayer()).setSettings(e.getSettings());
    }

}
