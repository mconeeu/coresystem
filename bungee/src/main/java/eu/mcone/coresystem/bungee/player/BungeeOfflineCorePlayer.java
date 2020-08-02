/*
 * Copyright (c) 2017 - 2020 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.bungee.player;

import eu.mcone.coresystem.api.bungee.CoreSystem;
import eu.mcone.coresystem.api.bungee.overwatch.punish.Punish;
import eu.mcone.coresystem.api.bungee.player.FriendData;
import eu.mcone.coresystem.api.bungee.player.OfflineCorePlayer;
import eu.mcone.coresystem.api.core.exception.PlayerNotResolvedException;
import eu.mcone.coresystem.api.core.player.Group;
import eu.mcone.coresystem.bungee.BungeeCoreSystem;
import eu.mcone.coresystem.bungee.overwatch.punish.PunishManager;
import eu.mcone.coresystem.core.CoreModuleCoreSystem;
import eu.mcone.coresystem.core.player.GlobalOfflineCorePlayer;
import group.onegaming.networkmanager.core.api.database.Database;
import lombok.Getter;
import org.bson.Document;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Updates.combine;
import static com.mongodb.client.model.Updates.set;

public class BungeeOfflineCorePlayer extends GlobalOfflineCorePlayer implements OfflineCorePlayer {

    @Getter
    private FriendData friendData;
    @Getter
    private boolean banned, muted;
    @Getter
    private int banPoints = 0, mutePoints = 0;
    @Getter
    private long banTime, muteTime;

    public BungeeOfflineCorePlayer(CoreSystem instance, UUID uuid) throws PlayerNotResolvedException {
        super(instance, uuid, false);
        loadData();
    }

    public BungeeOfflineCorePlayer(CoreSystem instance, String name) throws PlayerNotResolvedException {
        super(instance, name, false);
        loadData();
    }

    private void loadData() {
        this.friendData = BungeeCoreSystem.getInstance().getFriendSystem().getData(uuid);

        Punish punish = PunishManager.PUNISH_COLLECTION.find(eq("punished", uuid)).first();

        if (punish != null) {
            if (punish.isBanned()) {
                banned = true;
                banTime = punish.getBanEntry().getEnd();
            }

            if (punish.isMuted()) {
                muted = true;
                muteTime = punish.getMuteEntry().getEnd();
            }
        }

        Document pointsEntry = PunishManager.PUNISH_POINTS_COLLECTION.find(eq("uuid", uuid.toString())).first();
        if (pointsEntry != null) {
            this.banPoints = pointsEntry.getInteger("banpoints");
            this.mutePoints = pointsEntry.getInteger("mutepoints");
        }
    }

    public void setMuted(boolean muted) {
        this.muted = muted;
    }

    public void setMuteTime(long time) {
        this.muteTime = time;
    }

    public void setBanned(boolean banned) {
        this.banned = banned;
    }

    public void setBanTime(long time) {
        this.banTime = time;
    }

    @Override
    public void setGroups(Set<Group> groupList) {
        this.groupSet = new HashSet<>(groupList);
        updateDatabaseGroupsAsync(groupSet);
    }

    @Override
    public void addGroup(Group group) {
        this.groupSet.add(group);
        updateDatabaseGroupsAsync(groupSet);
    }

    @Override
    public void removeGroup(Group group) {
        this.groupSet.remove(group);
        updateDatabaseGroupsAsync(groupSet);
    }

    public void updatePunishmentAsync() {
        instance.runAsync(() -> {
            ((CoreModuleCoreSystem) instance).getMongoDB(Database.SYSTEM).getCollection("userinfo").updateOne(
                    eq("uuid", uuid.toString()),
                    combine(
                            set("banned", banned),
                            set("banTime", banTime),
                            set("muted", muted),
                            set("muteTime", muteTime)
                    )
            );
        });
    }

    private void updateDatabaseGroupsAsync(Set<Group> groupSet) {
        instance.runAsync(() ->
                ((CoreModuleCoreSystem) instance).getMongoDB(Database.SYSTEM).getCollection("userinfo").updateOne(
                        eq("uuid", uuid.toString()),
                        set("groups", instance.getPermissionManager().getGroupIDs(groupSet))
                )
        );
    }

}
