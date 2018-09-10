/*
 * Copyright (c) 2017 - 2018 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.coresystem.bungee.listener;

import eu.mcone.coresystem.api.bungee.CoreSystem;
import eu.mcone.coresystem.api.bungee.event.PermissionChangeEvent;
import eu.mcone.coresystem.api.bungee.player.CorePlayer;
import eu.mcone.coresystem.api.core.player.Group;
import eu.mcone.coresystem.bungee.BungeeCoreSystem;
import eu.mcone.coresystem.bungee.utils.TeamspeakVerifier;
import eu.mcone.coresystem.core.player.GlobalOfflineCorePlayer;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.util.ArrayList;
import java.util.Set;

public class PermissionChange implements Listener {

    @EventHandler
    public void on(PermissionChangeEvent e) {
        if (e.getKind() == PermissionChangeEvent.Kind.GROUP_PERMISSION) {
            ProxyServer.getInstance().getScheduler().runAsync(BungeeCoreSystem.getInstance(), () -> {
                BungeeCoreSystem.getInstance().getPermissionManager().reload();

                Set<Group> groups = BungeeCoreSystem.getInstance().getPermissionManager().getChildren(new ArrayList<>(e.getGroups()).get(0));
                for (CorePlayer player : BungeeCoreSystem.getInstance().getOnlineCorePlayers()) {
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
                ProxyServer.getInstance().getScheduler().runAsync(BungeeCoreSystem.getInstance(), () -> {
                    BungeeCoreSystem.getInstance().getPermissionManager().reload();
                    p.reloadPermissions();

                    CoreSystem.getInstance().getChannelHandler().createInfoRequest(p.bungee(), "EVENT", "PermissionChangeEvent", "USER_PERMISSION;");
                });
            }
        } else if (e.getKind() == PermissionChangeEvent.Kind.GROUP_CHANGE) {
            final CorePlayer p = e.getPlayer();

            if (p != null) {
                ((GlobalOfflineCorePlayer) p).setGroupSet(e.getGroups());
                p.reloadPermissions();

                TeamspeakVerifier tsv = BungeeCoreSystem.getSystem().getTeamspeakVerifier();
                if (tsv != null) tsv.updateLink(p, null);

                CoreSystem.getInstance().getChannelHandler().createInfoRequest(
                        p.bungee(),
                        "EVENT",
                        "PermissionChangeEvent",
                        "GROUP_CHANGE;"+CoreSystem.getInstance().getGson().toJson(
                                CoreSystem.getInstance().getPermissionManager().getGroupIDs(e.getGroups())
                        )
                );
            }
        }
    }

}
