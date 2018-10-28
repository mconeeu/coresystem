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

import java.util.*;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Updates.combine;
import static com.mongodb.client.model.Updates.set;

public abstract class GlobalOfflineCorePlayer implements eu.mcone.coresystem.api.core.player.GlobalOfflineCorePlayer {

    protected final GlobalCoreSystem instance;
    protected boolean isNew = false;

    @Getter
    protected UUID uuid;
    @Getter
    protected String name;
    @Getter
    @Setter
    private String teamspeakUid, discordUid;
    @Getter
    private int coins;
    @Getter
    private PlayerState state;
    @Getter
    @Setter
    protected Set<Group> groupSet;
    @Getter
    protected long onlinetime;
    @Getter
    @Setter
    protected Set<String> permissions;
    @Getter
    @Setter
    protected PlayerSettings settings;

    public GlobalOfflineCorePlayer(final GlobalCoreSystem instance, UUID uuid, String name, boolean online) {
        this.instance = instance;

        Document entry = ((CoreModuleCoreSystem) instance).getMongoDB(Database.SYSTEM).getCollection("userinfo").find(eq("uuid", uuid.toString())).first();
        if (entry != null) {
            setDatabaseValues(entry, online);
        } else {
            setDefaultValuesAndRegister(uuid, name, online);
        }

        reloadPermissions();
    }

    public GlobalOfflineCorePlayer(final GlobalCoreSystem instance, UUID uuid, boolean online) throws PlayerNotResolvedException {
        this.instance = instance;

        Document entry = ((CoreModuleCoreSystem) instance).getMongoDB(Database.SYSTEM).getCollection("userinfo").find(eq("uuid", uuid.toString())).first();
        if (entry != null) {
            setDatabaseValues(entry, online);
        } else {
            String name = instance.getPlayerUtils().fetchNameFromMojangAPI(uuid);

            if (name != null) {
                setDefaultValuesAndRegister(uuid, name, online);
            } else {
                throw new PlayerNotResolvedException("Could not fetch Player name for uuid " + uuid + "!");
            }
        }

        reloadPermissions();
    }

    public GlobalOfflineCorePlayer(final GlobalCoreSystem instance, String name, boolean online) throws PlayerNotResolvedException {
        this.instance = instance;
        UUID uuid = instance.getPlayerUtils().fetchUuidFromMojangAPI(name);

        if (uuid != null) {
            Document entry = ((CoreModuleCoreSystem) instance).getMongoDB(Database.SYSTEM).getCollection("userinfo").find(eq("uuid", uuid.toString())).first();
            if (entry != null) {
                setDatabaseValues(entry, online);
            } else {
                setDefaultValuesAndRegister(uuid, name, online);
            }
        } else {
            throw new PlayerNotResolvedException("Could not fetch Player uuid for name " + name + "!");
        }

        reloadPermissions();
    }

    private void setDefaultValuesAndRegister(UUID uuid, String name, boolean online) {
        this.uuid = uuid;
        this.name = name;
        this.groupSet = new HashSet<>(Collections.singletonList(Group.SPIELER));
        this.onlinetime = 0;
        this.coins = 20;
        this.settings = new PlayerSettings();
        this.state = online ? PlayerState.ONLINE : PlayerState.OFFLINE;
        this.isNew = true;

        instance.runAsync(() -> {
            ((CoreModuleCoreSystem) instance).sendConsoleMessage("§2Player §a" + name + "§2 is new! Registering in Database...");
            ((CoreModuleCoreSystem) instance).getMongoDB(Database.SYSTEM).getCollection("userinfo")
                    .insertOne(new Document("uuid", uuid.toString())
                            .append("name", name)
                            .append("groups", new ArrayList<>(Collections.singletonList(11)))
                            .append("coins", coins)
                            .append("ip", null)
                            .append("timestamp", System.currentTimeMillis() / 1000)
                            .append("player_settings", settings)
                            .append("state", online ? PlayerState.ONLINE.getId() : PlayerState.OFFLINE.getId())
                            .append("online_time", onlinetime)
                    );
        });
    }

    private void setDatabaseValues(Document entry, boolean online) {
        this.uuid = UUID.fromString(entry.getString("uuid"));
        this.name = entry.getString("name");
        this.groupSet = instance.getPermissionManager().getGroups(entry.get("groups", new ArrayList<>()));
        this.coins = entry.getInteger("coins");
        this.teamspeakUid = entry.getString("teamspeak_uid");
        this.discordUid = entry.getString("discord_uid");

        this.state = online ? PlayerState.ONLINE : PlayerState.getPlayerStateById(entry.getInteger("state"));
        this.onlinetime = entry.getLong("online_time");
        this.settings = ((CoreModuleCoreSystem) instance).getGson().fromJson(entry.get("player_settings", Document.class).toJson(), PlayerSettings.class);
    }

    @Override
    public Set<Group> getGroups() {
        return groupSet;
    }

    @Override
    public Group getMainGroup() {
        HashMap<Integer, Group> groups = new HashMap<>();
        this.groupSet.forEach(g -> groups.put(g.getId(), g));

        return Collections.min(groups.entrySet(), HashMap.Entry.comparingByValue()).getValue();
    }

    @Override
    public boolean hasPermission(String permission) {
        return instance.getPermissionManager().hasPermission(permissions, permission);
    }

    @Override
    public void reloadPermissions() {
        this.permissions = instance.getPermissionManager().getPermissions(uuid.toString(), groupSet);
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
                ((CoreModuleCoreSystem) instance).getMongoDB(Database.SYSTEM).getCollection("userinfo").updateOne(
                        eq("uuid", uuid.toString()),
                        set("groups", instance.getPermissionManager().getGroupIDs(groupSet))
                )
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
        if (coins - amount < 0) {
            amount = coins;
            ((CoreModuleCoreSystem) instance).sendConsoleMessage("§7Tried to remove more coins than Player §f" + name + "§7 has! (" + coins + "-" + amount + ")");
        }

        this.coins -= amount;
        instance.getCoinsUtil().removeCoins(uuid, amount);
    }

    @Override
    public boolean isTeamspeakIdLinked() {
        return teamspeakUid != null;
    }

    @Override
    public boolean isDiscordIdLinked() {
        return discordUid != null;
    }

    public void setState(PlayerState state) {
        this.state = state;
        ((CoreModuleCoreSystem) instance).getMongoDB(Database.SYSTEM).getCollection("userinfo").updateOne(eq("uuid", uuid.toString()), combine(set("state", state.getId())));
    }

    public void setCoinsAmount(int amount) {
        this.coins = amount;
    }

}
