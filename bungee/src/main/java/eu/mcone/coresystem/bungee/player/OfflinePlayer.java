/*
 * Copyright (c) 2017 - 2018 Dominik L., Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.coresystem.bungee.player;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import eu.mcone.coresystem.bungee.CoreSystem;
import eu.mcone.coresystem.lib.player.Group;
import lombok.Getter;
import lombok.Setter;

import java.sql.SQLException;
import java.util.*;

public class OfflinePlayer {

    @Getter
    private UUID uuid;
    @Getter
    private String name;
    @Getter
    private Set<Group> groups;
    private long joined, onlinetime;
    @Getter @Setter
    private Set<String> permissions;
    @Getter
    private Map<UUID, String> friends;
    @Getter @Setter
    private Map<UUID, String> requests;
    @Getter
    private List<UUID> blocks;
    @Getter @Setter
    private boolean requestsToggled;

    public OfflinePlayer(String name) {
        CoreSystem.mysql1.select("SELECT uuid, gruppe, onlinetime FROM userinfo WHERE name='"+name+"'", rs -> {
            try {
                if (rs.next()) {
                    this.uuid = UUID.fromString(rs.getString("uuid"));
                    this.onlinetime = rs.getLong("onlinetime");
                    this.joined = System.currentTimeMillis() / 1000;
                    this.groups = new HashSet<>();
                    JsonArray array = new JsonParser().parse(rs.getString("groups")).getAsJsonArray();

                    for (JsonElement e : array) {
                        groups.add(Group.getGroupById(e.getAsInt()));
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });

        this.name = name;
        if (this.uuid == null) return;

        CoreSystem.getOfflinePlayers().put(name, this);
    }

    public OfflinePlayer loadFriendData() {
        Object[] friendData = CoreSystem.getInstance().getFriendSystem().getData(uuid);
        this.friends = (Map<UUID, String>) friendData[0];
        this.requests = (Map<UUID, String>) friendData[1];
        this.blocks = (List<UUID>) friendData[2];
        this.requestsToggled = (boolean) friendData[3];

        return this;
    }

    public OfflinePlayer loadPermissions() {
        this.permissions = CoreSystem.getInstance().getPermissionManager().getPermissions(uuid.toString(), groups);
        return this;
    }

    public boolean hasPermission(String permission) {
        return CoreSystem.getInstance().getPermissionManager().hasPermission(permissions, permission);
    }

    public long getOnlinetime() {
        return (((System.currentTimeMillis() / 1000) - joined) / 60) + onlinetime;
    }

}
