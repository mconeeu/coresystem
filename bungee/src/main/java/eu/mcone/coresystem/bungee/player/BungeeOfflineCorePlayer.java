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
import eu.mcone.coresystem.core.player.GlobalOfflineCorePlayer;
import eu.mcone.networkmanager.core.api.database.Database;
import lombok.Getter;
import org.bson.Document;

import static com.mongodb.client.model.Filters.eq;

public class BungeeOfflineCorePlayer extends GlobalOfflineCorePlayer implements OfflineCorePlayer {

    @Getter
    private FriendData friendData;
    @Getter
    private boolean banned, muted;
    @Getter
    private int banPoints = 0, mutePoints = 0;
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
        this.permissions = BungeeCoreSystem.getInstance().getPermissionManager().getPermissions(uuid.toString(), groupSet);
        return this;
    }

    public boolean hasPermission(String permission) {
        return instance.getPermissionManager().hasPermission(permissions, permission);
    }

    public OfflineCorePlayer loadBanData() {
        Document muteEntry = BungeeCoreSystem.getSystem().getMongoDB(Database.SYSTEM).getCollection("bungeesystem_bansystem_mute").find(eq("uuid", uuid.toString())).first();
        if (muteEntry != null) {
            this.muted = true;
            this.muteTime = muteEntry.getLong("end");
        } else {
            this.muted = false;
        }

        Document banEntry = BungeeCoreSystem.getSystem().getMongoDB(Database.SYSTEM).getCollection("bungeesystem_bansystem_ban").find(eq("uuid", uuid.toString())).first();
        if (banEntry != null) {
            this.banned = true;
            this.banTime = banEntry.getLong("end");
        } else {
            this.banned = false;
        }

        Document pointsEntry = BungeeCoreSystem.getSystem().getMongoDB(Database.SYSTEM).getCollection("bungeesystem_bansystem_points").find(eq("uuid", uuid.toString())).first();
        if (pointsEntry != null) {
            this.banPoints = pointsEntry.getInteger("banpoints");
            this.mutePoints = pointsEntry.getInteger("mutepoints");
        }

        return this;
    }

}
