/*
 * Copyright (c) 2017 - 2018 Dominik L., Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.coresystem.bungee.player;

import eu.mcone.coresystem.bungee.CoreSystem;
import eu.mcone.coresystem.lib.player.Skin;
import eu.mcone.coresystem.lib.util.UUIDFetcher;
import eu.mcone.coresystem.lib.player.Group;
import lombok.Getter;
import lombok.Setter;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class CorePlayer {

    @Getter
    private UUID uuid;
    @Getter
    private String name, status;
    @Getter
    private Group group;
    private long joined, onlinetime;
    @Getter
    private long mutetime;
    @Getter @Setter
    private List<String> permissions;
    @Getter
    private Map<UUID, String> friends;
    @Getter
    private Map<UUID, String> requests;
    @Getter
    private List<UUID> blocks;
    @Getter @Setter
    private boolean requestsToggled;
    private boolean isMuted = false;
    @Getter @Setter
    private Skin nick;
    @Getter @Setter
    private boolean nicked = false;

    public CorePlayer(String name) {
        CoreSystem.mysql1.select("SELECT uuid, gruppe, onlinetime FROM userinfo WHERE name='"+name+"'", rs -> {
            try {
                if (rs.next()) {
                    this.uuid = UUID.fromString(rs.getString("uuid"));
                    this.onlinetime = rs.getLong("onlinetime");
                    this.joined = System.currentTimeMillis() / 1000;
                    this.group = Group.getGroupbyName(rs.getString("gruppe"));
                } else {
                    this.uuid = UUIDFetcher.getUuid(name);
                    this.onlinetime = 0;
                    this.joined = System.currentTimeMillis() / 1000;
                    this.group = Group.SPIELER;
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });

        CoreSystem.mysql1.select("SELECT `end` FROM `bungeesystem_bansystem_mute` WHERE `uuid`='"+getUuid()+"'", rs -> {
            try {
                if (rs.next()) {
                    this.isMuted = true;
                    this.mutetime = rs.getInt("end");
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });

        this.name = name;
        this.status = "online";

        register();
        reloadPermissions();

        Object[] friendData = CoreSystem.getInstance().getFriendSystem().getData(uuid);
        this.friends = (Map<UUID, String>) friendData[0];
        this.requests = (Map<UUID, String>) friendData[1];
        this.blocks = (List<UUID>) friendData[2];
        this.requestsToggled = (boolean) friendData[3];

        System.out.println("Loaded Player "+name+"!");
    }

    public ProxiedPlayer bungee() {
        return ProxyServer.getInstance().getPlayer(uuid);
    }

    public boolean hasPermission(String permission) {
        return CoreSystem.getInstance().getPermissionManager().hasPermission(permissions, permission);
    }

    public void reloadPermissions() {
        this.permissions = CoreSystem.getInstance().getPermissionManager().getPermissions(uuid.toString(), group);
    }


    public long getOnlinetime() {
        return (((System.currentTimeMillis() / 1000) - joined) / 60) + onlinetime;
    }

    public void setGroup(Group group) {
        this.group = group;
        permissions = CoreSystem.getInstance().getPermissionManager().getPermissions(uuid.toString(), group);
    }

    public void setStatus(final String status) {
        this.status = status;
        ProxyServer.getInstance().getScheduler().runAsync(CoreSystem.getInstance(), () -> CoreSystem.mysql1.update("UPDATE userinfo SET status='"+status+"' WHERE uuid='"+uuid+"'"));
    }

    public boolean isMuted() {
        long millis = System.currentTimeMillis() / 1000;
        if (isMuted && mutetime < millis) {
            isMuted = false;
            ProxyServer.getInstance().getScheduler().runAsync(CoreSystem.getInstance(), () -> {
                CoreSystem.mysql1.update("DELETE FROM `bungeesystem_bansystem_mute` WHERE end<"+millis);
            });
        }

        return isMuted;
    }

    private void register() {
        CoreSystem.getCorePlayers().put(uuid, this);
        if (CoreSystem.getOfflinePlayers().get(name) != null) CoreSystem.getOfflinePlayers().remove(name);
    }

    public void unregister() {
        if (CoreSystem.getCorePlayers().containsKey(uuid)) CoreSystem.getCorePlayers().remove(uuid);
        System.out.println("Unloaded Player "+name+"!");
    }

}
