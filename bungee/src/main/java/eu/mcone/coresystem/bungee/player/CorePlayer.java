/*
 * Copyright (c) 2017 - 2018 Dominik L., Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.coresystem.bungee.player;

import eu.mcone.coresystem.bungee.CoreSystem;
import eu.mcone.coresystem.lib.util.UUIDFetcher;
import eu.mcone.coresystem.lib.player.Group;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class CorePlayer {

    private UUID uuid;
    private String name, status;
    private Group group;
    private long joined, onlinetime, mutetime;
    private List<String> permissions;
    private Map<UUID, String> friends;
    private Map<UUID, String> reqests;
    private List<UUID> blocks;
    private boolean requestsToggled, isMuted = false, nicked = false;
    private Nick nick;

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
        this.reqests = (Map<UUID, String>) friendData[1];
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

    public Map<UUID, String> getFriends() {
        return this.friends;
    }

    public Map<UUID, String> getRequests() {
        return this.reqests;
    }

    public List<UUID> getBlocks() {
        return this.blocks;
    }

    public boolean hasRequestsToggled() {
        return requestsToggled;
    }

    public UUID getUuid() {
        return uuid;
    }

    public String getName() {
        return name;
    }

    public Group getGroup() {
        return group;
    }

    public String getStatus() {
        return status;
    }

    public List<String> getPermissions() {
        return permissions;
    }

    public long getOnlinetime() {
        return (((System.currentTimeMillis() / 1000) - joined) / 60) + onlinetime;
    }

    public Nick getNick() {
        return nick;
    }

    public void setGroup(Group group) {
        this.group = group;
        permissions = CoreSystem.getInstance().getPermissionManager().getPermissions(uuid.toString(), group);
    }

    public void setStatus(final String status) {
        this.status = status;
        ProxyServer.getInstance().getScheduler().runAsync(CoreSystem.getInstance(), () -> CoreSystem.mysql1.update("UPDATE userinfo SET status='"+status+"' WHERE uuid='"+uuid+"'"));
    }

    public void setPermissions(List<String> permissions) {
        this.permissions = permissions;
    }

    public void setRequestsToggled(boolean toggled) {
        requestsToggled = toggled;
    }

    public void setNick(Nick nick) {
        this.nick = nick;
    }

    public void setNicked(boolean nicked) {
        this.nicked = nicked;
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

    public boolean isNicked() {
        return nicked;
    }

    public long getMutetime() {
        return mutetime;
    }

    private void register() {
        CoreSystem.getCorePlayers().put(uuid, this);
        if (CoreSystem.getOfflinePlayers().get(uuid) != null) CoreSystem.getOfflinePlayers().remove(uuid);
    }

    public void unregister() {
        if (CoreSystem.getCorePlayers().containsKey(uuid)) CoreSystem.getCorePlayers().remove(uuid);
        System.out.println("Unloaded Player "+name+"!");
    }

}
