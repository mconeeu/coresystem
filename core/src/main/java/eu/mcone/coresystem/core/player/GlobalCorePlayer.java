/*
 * Copyright (c) 2017 - 2018 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.coresystem.core.player;

import com.mongodb.client.MongoCollection;
import eu.mcone.coresystem.api.core.GlobalCoreSystem;
import eu.mcone.coresystem.api.core.exception.PlayerNotResolvedException;
import eu.mcone.coresystem.api.core.exception.RuntimeCoreException;
import eu.mcone.coresystem.api.core.player.GlobalOfflineCorePlayer;
import eu.mcone.coresystem.api.core.player.Group;
import eu.mcone.coresystem.api.core.player.PlayerSettings;
import eu.mcone.coresystem.api.core.player.PlayerState;
import eu.mcone.coresystem.core.CoreModuleCoreSystem;
import eu.mcone.networkmanager.core.api.database.Database;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import net.labymod.serverapi.LabyModConnection;
import org.bson.Document;

import java.net.InetAddress;
import java.util.*;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Updates.combine;
import static com.mongodb.client.model.Updates.set;

public abstract class GlobalCorePlayer implements eu.mcone.coresystem.api.core.player.GlobalCorePlayer, GlobalOfflineCorePlayer {

    protected final GlobalCoreSystem instance;
    protected boolean isNew = false;

    @Getter
    protected final String name, ipAdress;
    @Getter
    protected UUID uuid;
    private long onlinetime, joined;
    @Getter
    private PlayerState state;
    @Getter
    protected int coins;
    @Getter
    @Setter
    private boolean nicked = false;
    @Getter @Setter
    private Set<Group> groupSet;
    @Getter
    @Setter
    private Set<String> permissions;
    @Getter
    @Setter
    private String teamspeakUid;
    @Getter
    @Setter
    private LabyModConnection labyModConnection;
    @Getter
    @Setter
    protected PlayerSettings settings;

    protected GlobalCorePlayer(final GlobalCoreSystem instance, final InetAddress address, String name) throws PlayerNotResolvedException {
        this.instance = instance;
        this.name = name;
        this.ipAdress = address.toString().split("/")[1];
        this.uuid = instance.getPlayerUtils().fetchUuidFromMojangAPI(name);

        final MongoCollection<Document> collection = ((CoreModuleCoreSystem) instance).getMongoDB(Database.SYSTEM).getCollection("userinfo");
        Document nameEntry = collection.find(eq("name", name)).first();

        if (nameEntry != null) {
            setDatabaseValues(nameEntry);
        } else {
            Document uuidEntry = collection.find(eq("uuid", this.uuid)).first();

            if (uuidEntry != null) {
                ((CoreModuleCoreSystem) instance).sendConsoleMessage("§2Player §a" + name + "§2 changed his name from §f" + uuidEntry.getString("name") + "§2. Applying to database...");
                collection.updateOne(eq("uuid", this.uuid.toString()), combine(set("name", name)));
                setDatabaseValues(uuidEntry);
            } else {
                this.groupSet = new HashSet<>(Collections.singletonList(Group.SPIELER));
                this.onlinetime = 0;
                this.coins = 20;
                this.settings = new PlayerSettings();
                this.state = PlayerState.ONLINE;
                this.joined = System.currentTimeMillis() / 1000;
                this.isNew = true;

                ((CoreModuleCoreSystem) instance).sendConsoleMessage("§2Player §a" + name + "§2 is new! Registering in Database...");
                collection.insertOne(new Document("uuid", uuid.toString())
                        .append("name", name)
                        .append("groups", new ArrayList<>(Collections.singletonList(11)))
                        .append("coins", coins)
                        .append("ip", ipAdress)
                        .append("timestamp", System.currentTimeMillis() / 1000)
                        .append("player_settings", Document.parse(((CoreModuleCoreSystem) instance).getSimpleGson().toJson(new PlayerSettings(), PlayerSettings.class)))
                        .append("state", PlayerState.ONLINE.getId())
                        .append("online_time", onlinetime)
                );
            }
        }

        if (uuid == null) throw new PlayerNotResolvedException("Player uuid could not be resolved! (isNew = " + isNew + ")");
    }

    private void setDatabaseValues(Document userinfoDocument) {
        this.uuid = UUID.fromString(userinfoDocument.getString("uuid"));
        this.groupSet = instance.getPermissionManager().getGroups(userinfoDocument.get("groups", new ArrayList<>()));
        this.onlinetime = userinfoDocument.getLong("online_time");
        this.coins = userinfoDocument.getInteger("coins");
        this.teamspeakUid = userinfoDocument.getString("teamspeak_uid");
        this.settings = ((CoreModuleCoreSystem) instance).getGson().fromJson(userinfoDocument.get("player_settings", Document.class).toJson(), PlayerSettings.class);
        this.state = PlayerState.getPlayerStateById(userinfoDocument.getInteger("state"));
        this.joined = System.currentTimeMillis() / 1000;
    }

    @Override
    public long getOnlinetime() {
        return (((System.currentTimeMillis() / 1000) - joined) / 60) + onlinetime;
    }

    @Override
    public boolean hasPermission(String permission) {
        return instance.getPermissionManager().hasPermission(permissions, permission);
    }

    @Override
    public void reloadPermissions() {
        permissions = instance.getPermissionManager().getPermissions(uuid.toString(), groupSet);
    }

    @Override
    public void addSemiPermission(String permission) {
        permissions.add(permission);
    }

    @Override
    public void removeSemiPermission(String permission) {
        permissions.remove(permission);
    }

    @Override
    public Group getMainGroup() {
        HashMap<Integer, Group> groups = new HashMap<>();
        this.groupSet.forEach(g -> groups.put(g.getId(), g));

        return Collections.min(groups.entrySet(), HashMap.Entry.comparingByValue()).getValue();
    }

    @Override
    public Set<Group> getGroups() {
        return groupSet;
    }

    @Override
    public Set<Group> updateGroupsFromDatabase() {
        @NonNull Document entry = ((CoreModuleCoreSystem) instance).getMongoDB(Database.SYSTEM).getCollection("userinfo").find(eq("uuid", uuid.toString())).first();
        groupSet = instance.getPermissionManager().getGroups(entry.get("groups", new ArrayList<>()));
        reloadPermissions();

        return groupSet;
    }

    @Override
    public void setGroups(Set<Group> groupList) {
        this.groupSet = new HashSet<>(groupList);
        reloadPermissions();
        updateDatabaseGroupsAsync(groupSet);
    }

    @Override
    public void addGroup(Group group) {
        this.groupSet.add(group);
        reloadPermissions();
        updateDatabaseGroupsAsync(groupSet);
    }

    @Override
    public void removeGroup(Group group) {
        this.groupSet.remove(group);
        reloadPermissions();
        updateDatabaseGroupsAsync(groupSet);
    }

    private void updateDatabaseGroupsAsync(Set<Group> groupSet) {
        instance.runAsync(() ->
                ((CoreModuleCoreSystem) instance).getMongoDB(Database.SYSTEM).getCollection("userinfo").updateOne(eq("uuid", uuid.toString()), set("groups", ((CoreModuleCoreSystem) instance).getSimpleGson().toJson(groupSet)))
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

    public void setState(PlayerState state) {
        this.state = state;
        ((CoreModuleCoreSystem) instance).getMongoDB(Database.SYSTEM).getCollection("userinfo").updateOne(eq("uuid", uuid.toString()), combine(set("state", state.getId())));
    }

    public void updateCoinsAmount(int amount) {
        this.coins = amount;
    }

}
