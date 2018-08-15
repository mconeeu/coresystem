/*
 * Copyright (c) 2017 - 2018 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.coresystem.core.player;

import eu.mcone.coresystem.api.core.GlobalCoreSystem;
import eu.mcone.coresystem.api.core.exception.PlayerNotResolvedException;
import eu.mcone.coresystem.api.core.exception.RuntimeCoreException;
import eu.mcone.coresystem.api.core.player.Group;
import eu.mcone.coresystem.api.core.player.PlayerSettings;
import eu.mcone.coresystem.api.core.player.PlayerState;
import eu.mcone.coresystem.core.CoreModuleCoreSystem;
import eu.mcone.networkmanager.core.api.database.Database;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.bson.Document;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Updates.set;

public abstract class GlobalOfflineCorePlayer implements eu.mcone.coresystem.api.core.player.GlobalOfflineCorePlayer {

    protected final GlobalCoreSystem instance;

    @Getter
    protected UUID uuid;
    @Getter
    private String name;
    @Getter
    private int coins;
    @Getter
    private PlayerState state;
    @Getter
    protected Set<Group> groupSet;
    @Getter
    private long onlinetime;
    @Getter @Setter
    protected Set<String> permissions;
    @Getter
    private PlayerSettings settings;

    public GlobalOfflineCorePlayer(final GlobalCoreSystem instance, String name) throws PlayerNotResolvedException {
        this.name = name;
        this.instance = instance;

        Document entry = ((CoreModuleCoreSystem) instance).getMongoDB(Database.SYSTEM).getCollection("userinfo").find(eq("name", name)).first();
        if (entry != null) {
            this.uuid = UUID.fromString(entry.getString("uuid"));
            this.groupSet = instance.getPermissionManager().getGroups(entry.get("groups", new ArrayList<>()));
            this.coins = entry.getInteger("coins");
            this.state = PlayerState.getPlayerStateById(entry.getInteger("state"));
            this.onlinetime = entry.getLong("online_time");
            this.settings = ((CoreModuleCoreSystem) instance).getGson().fromJson(entry.get("player_settings", Document.class).toJson(), PlayerSettings.class);
        }

        if (this.uuid == null) throw new PlayerNotResolvedException("Database does not contain player "+name+"!");
    }

    @Override
    public Set<Group> getGroups() {
        return groupSet;
    }

    @Override
    public Set<Group> updateGroupsFromDatabase() {
        @NonNull Document entry = ((CoreModuleCoreSystem) instance).getMongoDB(Database.SYSTEM).getCollection("userinfo").find(eq("uuid", uuid.toString())).first();
        return groupSet = instance.getPermissionManager().getGroups(entry.get("groups", new ArrayList<>()));
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

    private void updateDatabaseGroupsAsync(Set<Group> groupSet) {
        instance.runAsync(() ->
                ((CoreModuleCoreSystem) instance).getMongoDB(Database.SYSTEM).getCollection("userinfo").updateOne(eq("uuid", uuid.toString()), set("groups", ((CoreModuleCoreSystem) instance).getGson().toJson(groupSet)))
        );
    }

    @Override
    public void setCoins(int coins) {
        if (coins < 0) {
            throw new RuntimeCoreException("Cannot set negative coin amount!");
        } else {
            this.coins = coins;
            instance.getCoinsUtil().setCoins(uuid, coins);
        }
    }

    @Override
    public void addCoins(int amount) {
        this.coins += amount;
        instance.getCoinsUtil().addCoins(uuid, amount);
    }

    @Override
    public void removeCoins(int amount) {
        if (coins-amount < 0) {
            amount = coins;
            ((CoreModuleCoreSystem) instance).sendConsoleMessage("§7Tried to remove more coins than Player §f"+name+"§7 has! ("+coins+"-"+amount+")");
        }

        this.coins -= amount;
        instance.getCoinsUtil().removeCoins(uuid, amount);
    }

}
