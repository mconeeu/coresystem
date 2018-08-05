/*
 * Copyright (c) 2017 - 2018 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.coresystem.core.player;

import com.mongodb.MongoException;
import eu.mcone.coresystem.api.core.GlobalCoreSystem;
import eu.mcone.coresystem.api.core.exception.PlayerNotResolvedException;
import eu.mcone.coresystem.api.core.exception.RuntimeCoreException;
import eu.mcone.coresystem.api.core.player.GlobalOfflineCorePlayer;
import eu.mcone.coresystem.api.core.player.Group;
import eu.mcone.coresystem.api.core.player.PlayerSettings;
import eu.mcone.coresystem.api.core.player.PlayerState;
import eu.mcone.coresystem.core.CoreModuleCoreSystem;
import lombok.Getter;
import lombok.Setter;
import net.labymod.serverapi.LabyModConnection;
import org.bson.Document;

import java.net.InetAddress;
import java.util.*;

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
    @Getter
    private Set<Group> groups;
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

        try {
            for (Document userinfoDocument : instance.getMongoDBManager().getDocumentsInCollection("userinfo")) {
                if (userinfoDocument.containsValue(name)) {
                    setDatabaseValues(userinfoDocument);
                } else {
                    if (userinfoDocument.containsValue(this.uuid)) {
                        ((CoreModuleCoreSystem) instance).sendConsoleMessage("§2Player §a" + name + "§2 changed his name from §f" + userinfoDocument.getString("name") + "§2. Applying to database...");
                        instance.getMongoDBManager().updateDocument("uuid", this.uuid, "name", name, "userinfo");
                        setDatabaseValues(userinfoDocument);
                    } else {
                        ((CoreModuleCoreSystem) instance).sendConsoleMessage("§2Player §a" + name + "§2 is new! Registering in Database...");
                        instance.getMongoDBManager().insertDocument(new Document("uuid", instance.getPlayerUtils().fetchUuidFromMojangAPI(name))
                                .append("name", name)
                                .append("groups", "[11]")
                                .append("coins", "20")
                                .append("stats", PlayerState.ONLINE.getId())
                                .append("ip", ipAdress)
                                .append("timestamp", System.currentTimeMillis() / 1000), "userinfo");

                        this.groups = new HashSet<>(Collections.singletonList(Group.SPIELER));
                        this.groups = new HashSet<>(Collections.singletonList(Group.SPIELER));
                        this.onlinetime = 0;
                        this.coins = 20;
                        this.settings = new PlayerSettings();
                        this.state = PlayerState.ONLINE;
                        this.joined = System.currentTimeMillis() / 1000;
                        this.isNew = true;
                    }
                }
            }

            if (uuid == null) {
                throw new PlayerNotResolvedException("Player uuid could not be resolved! (isNew = " + isNew + ")");
            }
        } catch (MongoException e) {
            e.printStackTrace();
        }

        /* Deprecated Mysql
        ((CoreModuleCoreSystem) instance).getMySQL(Database.SYSTEM).select("SELECT uuid, groups, coins, state, onlinetime, teamspeak_uid, player_settings FROM userinfo WHERE name='" + name + "'", rs -> {
            try {
                if (rs.next()) {
                    setDatabaseValues(rs);
                } else {
                    this.uuid = instance.getPlayerUtils().fetchUuidFromMojangAPI(name);
                    ((CoreModuleCoreSystem) instance).getMySQL(Database.SYSTEM).select("SELECT uuid, name, groups, coins, state, onlinetime, teamspeak_uid, player_settings FROM userinfo WHERE uuid='" + this.uuid.toString() + "'", rs1 -> {
                        try {
                            if (rs1.next()) {
                                ((CoreModuleCoreSystem) instance).sendConsoleMessage("§2Player §a" + name + "§2 changed his name from §f" + rs1.getString("name") + "§2. Applying to database...");
                                ((CoreModuleCoreSystem) instance).getMySQL(Database.SYSTEM).update("UPDATE userinfo SET name='" + name + "' WHERE uuid='" + this.uuid.toString() + "'");
                                setDatabaseValues(rs1);
                            } else {
                                ((CoreModuleCoreSystem) instance).sendConsoleMessage("§2Player §a" + name + "§2 is new! Registering in Database...");
                                ((CoreModuleCoreSystem) instance).getMySQL(Database.SYSTEM).update("INSERT INTO `userinfo` (`uuid`, `name`, `groups`, `coins`, `state`, `ip`, `timestamp`) VALUES ('" + instance.getPlayerUtils().fetchUuidFromMojangAPI(name) + "', '" + name + "', '[11]', 20, '" + PlayerState.ONLINE.getId() + "', '" + ipAdress + "', '" + System.currentTimeMillis() / 1000 + "')");

                                this.groups = new HashSet<>(Collections.singletonList(Group.SPIELER));
                                this.onlinetime = 0;
                                this.coins = 20;
                                this.settings = new PlayerSettings();
                                this.state = PlayerState.ONLINE;
                                this.joined = System.currentTimeMillis() / 1000;
                                this.isNew = true;
                            }
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    });
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });

        if (uuid == null) {
            throw new PlayerNotResolvedException("Player uuid could not be resolved! (isNew = " + isNew + ")");
        }
        */
    }

    private void setDatabaseValues(Document userinfoDocument) {
        try {
            this.uuid = (UUID) userinfoDocument.get("uuid");
            this.groups = instance.getPermissionManager().getGroups(userinfoDocument.getString("groups"));
            this.onlinetime = userinfoDocument.getLong("onlinetime");
            this.coins = userinfoDocument.getInteger("coins");
            this.teamspeakUid = userinfoDocument.getString("teamspeak_uid");
            this.settings = ((CoreModuleCoreSystem) instance).getGson().fromJson(userinfoDocument.getString("player_settings"), PlayerSettings.class);
            this.state = PlayerState.getPlayerStateById(userinfoDocument.getInteger("state"));
            this.joined = System.currentTimeMillis() / 1000;
        } catch (MongoException e) {
            e.printStackTrace();
        }
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
        permissions = instance.getPermissionManager().getPermissions(uuid.toString(), groups);
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
        this.groups.forEach(g -> groups.put(g.getId(), g));

        return Collections.min(groups.entrySet(), HashMap.Entry.comparingByValue()).getValue();
    }

    @Override
    public void setGroups(Set<Group> groups) {
        this.groups = groups;
        reloadPermissions();
    }

    @Override
    public void addGroup(Group group) {
        this.groups.add(group);
        reloadPermissions();
    }

    @Override
    public void removeGroup(Group group) {
        this.groups.remove(group);
        reloadPermissions();
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

        instance.getMongoDBManager().updateDocument("uuid", uuid.toString(), "state", state.getId(), "userinfo");
        //instance.runAsync(() -> ((CoreModuleCoreSystem) instance).getMySQL(Database.SYSTEM).update("UPDATE userinfo SET state='" + state.getId() + "' WHERE uuid='" + uuid + "'"));
    }

    public void updateCoinsAmount(int amount) {
        this.coins = amount;
    }

}
