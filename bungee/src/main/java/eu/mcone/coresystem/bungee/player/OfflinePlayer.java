/*
 * Copyright (c) 2017 - 2018 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.coresystem.bungee.player;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import eu.mcone.coresystem.api.core.exception.CoreException;
import eu.mcone.coresystem.api.core.player.Group;
import eu.mcone.coresystem.bungee.BungeeCoreSystem;
import lombok.Getter;
import lombok.Setter;

import java.sql.SQLException;
import java.util.*;

public class OfflinePlayer {

    @Getter
    private UUID uuid;
    @Getter
    private String name, status;
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
    @Getter
    private boolean banned, muted;
    @Getter
    private int banPoints, mutePoints;
    @Getter
    private long banTime, muteTime;

    public OfflinePlayer(String name) throws CoreException {
        BungeeCoreSystem.getSystem().getMySQL(Database.SYSTEM).select("SELECT uuid, groups, status, onlinetime FROM userinfo WHERE name='"+name+"'", rs -> {
            try {
                if (rs.next()) {
                    this.uuid = UUID.fromString(rs.getString("uuid"));
                    this.status = rs.getString("status");
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

        if (this.uuid == null) throw new CoreException("Database does not contain eu.mcone.coresystem.api.core.player "+name+"!");
        this.name = name;
    }

    public OfflinePlayer loadFriendData() {
        Object[] friendData = BungeeCoreSystem.getInstance().getFriendSystem().getData(uuid);
        this.friends = (Map<UUID, String>) friendData[0];
        this.requests = (Map<UUID, String>) friendData[1];
        this.blocks = (List<UUID>) friendData[2];
        this.requestsToggled = (boolean) friendData[3];

        return this;
    }

    public OfflinePlayer loadPermissions() {
        this.permissions = BungeeCoreSystem.getInstance().getPermissionManager().getPermissions(uuid.toString(), groups);
        return this;
    }

    public OfflinePlayer loadBanData() {
        BungeeCoreSystem.getSystem().getMySQL(Database.SYSTEM).select("SELECT `end` FROM `bungeesystem_bansystem_mute` WHERE `uuid`='"+getUuid()+"'", rs -> {
            try {
                if (rs.next()) {
                    this.muted = true;
                    this.muteTime = rs.getLong("end");
                } else {
                    this.muted = false;
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });

        BungeeCoreSystem.getSystem().getMySQL(Database.SYSTEM).select("SELECT `end` FROM `bungeesystem_bansystem_ban` WHERE `uuid`='"+getUuid()+"'", rs -> {
            try {
                if (rs.next()) {
                    this.banned = true;
                    this.banTime = rs.getLong("end");
                } else {
                    this.banned = false;
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });

        BungeeCoreSystem.getSystem().getMySQL(Database.SYSTEM).select("SELECT `banpoints`, `mutepoints` FROM `bungeesystem_bansystem_points` WHERE `uuid`='"+getUuid()+"'", rs -> {
            try {
                if (rs.next()) {
                    this.banPoints = rs.getInt("banpoints");
                    this.mutePoints = rs.getInt("mutepoints");
                } else {
                    this.banPoints = 0;
                    this.mutePoints = 0;
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });

        return this;
    }

    public boolean hasPermission(String permission) {
        return BungeeCoreSystem.getInstance().getPermissionManager().hasPermission(permissions, permission);
    }

    public long getOnlinetime() {
        return (((System.currentTimeMillis() / 1000) - joined) / 60) + onlinetime;
    }

}
