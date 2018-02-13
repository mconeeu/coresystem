/*
 * Copyright (c) 2017 Dominik L., Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.bungee.player;

import eu.mcone.coresystem.bungee.CoreSystem;
import eu.mcone.coresystem.lib.player.Group;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class OfflinePlayer {

    private UUID uuid;
    private String name;
    private Group group;
    private long joined, onlinetime;
    private List<String> permissions;
    private Map<UUID, String> friends;
    private Map<UUID, String> reqests;
    private List<UUID> blocks;
    private boolean requestsToggled;

    public OfflinePlayer(UUID uuid) {
        CoreSystem.mysql1.select("SELECT name, gruppe, onlinetime FROM userinfo WHERE uuid='"+uuid.toString()+"'", rs -> {
            try {
                if (rs.next()) {
                    this.name = rs.getString("name");
                    this.onlinetime = rs.getLong("onlinetime");
                    this.joined = System.currentTimeMillis() / 1000;
                    this.group = Group.getGroupbyName(rs.getString("gruppe"));
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });

        if (this.name == null) return;

        CoreSystem.getOfflinePlayers().put(uuid, this);
        this.uuid = uuid;
        this.permissions = CoreSystem.getInstance().getPermissionManager().getPermissions(uuid.toString(), group);

        Object[] friendData = CoreSystem.getInstance().getFriendSystem().getData(uuid);
        this.friends = (Map<UUID, String>) friendData[0];
        this.reqests = (Map<UUID, String>) friendData[1];
        this.blocks = (List<UUID>) friendData[2];
        this.requestsToggled = (boolean) friendData[3];
    }

    public boolean hasPermission(String permission) {
        return CoreSystem.getInstance().getPermissionManager().hasPermission(permissions, permission);
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

    public List<String> getPermissions() {
        return permissions;
    }

    public long getOnlinetime() {
        return (((System.currentTimeMillis() / 1000) - joined) / 60) + onlinetime;
    }

    public void setGroup(Group group) {
        this.group = group;
        permissions = CoreSystem.getInstance().getPermissionManager().getPermissions(uuid.toString(), group);
    }

    public void setPermissions(List<String> permissions) {
        this.permissions = permissions;
    }

    public void setRequestsToggled(boolean toggled) {
        requestsToggled = toggled;
    }

}
