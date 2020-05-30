/*
 * Copyright (c) 2017 - 2020 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.core.player;

import eu.mcone.coresystem.api.core.GlobalCoreSystem;
import eu.mcone.coresystem.api.core.exception.PlayerNotResolvedException;
import eu.mcone.coresystem.api.core.exception.RuntimeCoreException;
import eu.mcone.coresystem.api.core.overwatch.trust.TrustedUser;
import eu.mcone.coresystem.api.core.player.Group;
import eu.mcone.coresystem.api.core.player.PlayerSettings;
import eu.mcone.coresystem.api.core.player.PlayerState;
import eu.mcone.coresystem.core.CoreModuleCoreSystem;
import group.onegaming.networkmanager.core.api.database.Database;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.bson.Document;

import java.text.NumberFormat;
import java.util.*;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Updates.*;

public abstract class GlobalOfflineCorePlayer implements eu.mcone.coresystem.api.core.player.GlobalOfflineCorePlayer {

    private static final NumberFormat NUMBERFORMAT = NumberFormat.getInstance();

    static {
        NUMBERFORMAT.setGroupingUsed(true);
    }

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
    private int coins, emeralds;
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
    @Getter
    protected transient TrustedUser trust;

    GlobalOfflineCorePlayer(final GlobalCoreSystem instance, UUID uuid, String name, boolean online) {
        this.instance = instance;

        Document entry = ((CoreModuleCoreSystem) instance).getMongoDB(Database.SYSTEM).getCollection("userinfo").find(eq("uuid", uuid.toString())).first();
        if (entry != null) {
            if (entry.get("groups") != null) {
                setDatabaseValues(entry, online);
            } else {
                setDefaultValuesAndRegister(uuid, name, online);
            }
        } else {
            setDefaultValuesAndRegister(uuid, name, online);
        }

        reloadPermissions();
    }

    public GlobalOfflineCorePlayer(final GlobalCoreSystem instance, UUID uuid, boolean online) throws PlayerNotResolvedException {
        this.instance = instance;

        Document entry = ((CoreModuleCoreSystem) instance).getMongoDB(Database.SYSTEM).getCollection("userinfo").find(eq("uuid", uuid.toString())).first();
        if (entry != null) {
            if (entry.get("groups") != null) {
                setDatabaseValues(entry, online);
            } else {
                setDefaultValuesAndRegister(uuid, entry.getString("name"), online);
            }
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
                if (entry.get("groups") != null) {
                    setDatabaseValues(entry, online);
                } else {
                    setDefaultValuesAndRegister(uuid, name, online);
                }
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
        this.coins = 1000;
        this.emeralds = 0;
        this.settings = new PlayerSettings();
        this.state = online ? PlayerState.ONLINE : PlayerState.OFFLINE;
        this.isNew = true;
        this.trust = new TrustedUser();

        instance.runAsync(() -> {
            ((CoreModuleCoreSystem) instance).sendConsoleMessage("§2Player §a" + name + "§2 is new! Registering in Database...");
            ((CoreModuleCoreSystem) instance).getMongoDB(Database.SYSTEM).getCollection("userinfo")
                    .insertOne(new Document("uuid", uuid.toString())
                            .append("name", name)
                            .append("groups", Collections.singletonList(Group.SPIELER.getId()))
                            .append("coins", coins)
                            .append("emeralds", emeralds)
                            .append("ip", null)
                            .append("timestamp", System.currentTimeMillis() / 1000)
                            .append("player_settings", settings)
                            .append("state", online ? PlayerState.ONLINE.getId() : PlayerState.OFFLINE.getId())
                            .append("online_time", onlinetime)
                            .append("trust", trust)
                    );
        });
    }

    private void setDatabaseValues(Document entry, boolean online) {
        this.uuid = UUID.fromString(entry.getString("uuid"));
        this.name = entry.getString("name");
        this.groupSet = instance.getPermissionManager().getGroups(entry.get("groups", new ArrayList<>()));
        this.coins = entry.getInteger("coins") != null ? entry.getInteger("coins") : 1000;
        this.emeralds = entry.getInteger("emeralds") != null ? entry.getInteger("emeralds") : 0;
        this.teamspeakUid = entry.getString("teamspeak_uid");
        this.discordUid = entry.getString("discord_uid");
        this.state = online ? PlayerState.ONLINE : entry.getInteger("state") != null ? PlayerState.getPlayerStateById(entry.getInteger("state")) : PlayerState.OFFLINE;
        this.onlinetime = entry.getLong("online_time") != null ? entry.getLong("online_time") : 0;
        this.settings = entry.get("player_settings") != null ? ((CoreModuleCoreSystem) instance).getGson().fromJson(entry.get("player_settings", Document.class).toJson(), PlayerSettings.class) : new PlayerSettings();
        this.trust = new TrustedUser(entry.get("trust", Document.class));
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
        this.permissions = instance.getPermissionManager().getPermissions(uuid, groupSet);
    }

    @Override
    public Set<Group> updateGroupsFromDatabase() {
        @NonNull Document entry = ((CoreModuleCoreSystem) instance).getMongoDB(Database.SYSTEM).getCollection("userinfo").find(eq("uuid", uuid.toString())).first();
        return groupSet = instance.getPermissionManager().getGroups(entry.get("groups", new ArrayList<>()));
    }

    @Override
    public void updateTrust() {
        ((CoreModuleCoreSystem) instance).getMongoDB(Database.SYSTEM).getCollection("userinfo").updateOne(eq("uuid", this.uuid.toString()), set("trust", trust));
    }

    @Override
    public void increaseCorrectReports() {
        trust.setCorrectReports(trust.getCorrectReports() + 1);
        ((CoreModuleCoreSystem) instance).getMongoDB(Database.SYSTEM).getCollection("userinfo").updateOne(eq("uuid", this.uuid.toString()), inc("correctReports", 1));
    }

    @Override
    public void increaseWrongReports() {
        trust.setWrongReports(trust.getWrongReports() + 1);
        ((CoreModuleCoreSystem) instance).getMongoDB(Database.SYSTEM).getCollection("userinfo").updateOne(eq("uuid", this.uuid.toString()), inc("wrongReports", 1));
    }

    @Override
    public String getFormattedCoins() {
        return NUMBERFORMAT.format(coins);
    }

    @Override
    public void setCoins(int coins) {
        if (coins < 0) {
            throw new RuntimeCoreException("Cannot set negative coin amount!");
        } else {
            this.coins = coins;
            ((CoreModuleCoreSystem) instance).getMoneyUtil().setCoins(uuid, coins);
        }
    }

    @Override
    public void addCoins(int amount) {
        this.coins += amount;
        ((CoreModuleCoreSystem) instance).getMoneyUtil().addCoins(uuid, amount);
    }

    @Override
    public void removeCoins(int amount) {
        if (coins - amount < 0) {
            amount = coins;
            ((CoreModuleCoreSystem) instance).sendConsoleMessage("§7Tried to remove more coins than Player §f" + name + "§7 has! (" + coins + "-" + amount + ")");
        }

        this.coins -= amount;
        ((CoreModuleCoreSystem) instance).getMoneyUtil().removeCoins(uuid, amount);
    }

    @Override
    public String getFormattedEmeralds() {
        return NUMBERFORMAT.format(emeralds);
    }

    @Override
    public void setEmeralds(int coins) {
        if (coins < 0) {
            throw new RuntimeCoreException("Cannot set negative coin amount!");
        } else {
            this.emeralds = coins;
            ((CoreModuleCoreSystem) instance).getMoneyUtil().setEmeralds(uuid, coins);
        }
    }

    @Override
    public void addEmeralds(int amount) {
        this.emeralds += amount;
        ((CoreModuleCoreSystem) instance).getMoneyUtil().addEmeralds(uuid, amount);
    }

    @Override
    public void removeEmeralds(int amount) {
        if (emeralds - amount < 0) {
            amount = emeralds;
            ((CoreModuleCoreSystem) instance).sendConsoleMessage("§7Tried to remove more emeralds than Player §f" + name + "§7 has! (" + coins + "-" + amount + ")");
        }

        this.emeralds -= amount;
        ((CoreModuleCoreSystem) instance).getMoneyUtil().removeEmeralds(uuid, amount);
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

    public void setEmeraldsAmount(int amount) {
        this.emeralds = amount;
    }

}
