/*
 * Copyright (c) 2017 - 2018 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.coresystem.bungee.player;

import eu.mcone.coresystem.api.bungee.CoreSystem;
import eu.mcone.coresystem.api.bungee.player.FriendData;
import eu.mcone.coresystem.api.bungee.player.OfflineCorePlayer;
import eu.mcone.coresystem.api.core.exception.PlayerNotResolvedException;
import eu.mcone.coresystem.bungee.BungeeCoreSystem;
import eu.mcone.coresystem.core.mysql.Database;
import eu.mcone.coresystem.core.player.GlobalOfflineCorePlayer;
import lombok.Getter;

import java.sql.SQLException;

public class BungeeOfflineCorePlayer extends GlobalOfflineCorePlayer implements OfflineCorePlayer {

    @Getter
    private FriendData friendData;
    @Getter
    private boolean banned, muted;
    @Getter
    private int banPoints, mutePoints;
    @Getter
    private long banTime, muteTime;

    public BungeeOfflineCorePlayer(CoreSystem instance, String name) throws PlayerNotResolvedException {
        super(instance, name);
    }

    public OfflineCorePlayer loadFriendData() {
        this.friendData = BungeeCoreSystem.getInstance().getFriendSystem().getData(uuid);
        return this;
    }

    public OfflineCorePlayer loadPermissions() {
        this.permissions = BungeeCoreSystem.getInstance().getPermissionManager().getPermissions(uuid.toString(), groups);
        return this;
    }

    public boolean hasPermission(String permission) {
        return instance.getPermissionManager().hasPermission(permissions, permission);
    }

    public OfflineCorePlayer loadBanData() {
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

}
