/*
 * Copyright (c) 2017 - 2020 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.bungee.player;

import eu.mcone.coresystem.api.bungee.CoreSystem;
import eu.mcone.coresystem.api.bungee.event.PermissionChangeEvent;
import eu.mcone.coresystem.api.bungee.event.PlayerSettingsChangeEvent;
import eu.mcone.coresystem.api.bungee.overwatch.punish.Punish;
import eu.mcone.coresystem.api.bungee.player.CorePlayer;
import eu.mcone.coresystem.api.bungee.player.FriendData;
import eu.mcone.coresystem.api.bungee.player.OfflineCorePlayer;
import eu.mcone.coresystem.api.core.overwatch.trust.TrustedUser;
import eu.mcone.coresystem.api.core.player.Group;
import eu.mcone.coresystem.api.core.player.Nick;
import eu.mcone.coresystem.api.core.player.PlayerSettings;
import eu.mcone.coresystem.bungee.BungeeCoreSystem;
import eu.mcone.coresystem.bungee.overwatch.punish.PunishManager;
import eu.mcone.coresystem.core.CoreModuleCoreSystem;
import eu.mcone.coresystem.core.player.GlobalCorePlayer;
import group.onegaming.networkmanager.core.api.database.Database;
import lombok.Getter;
import lombok.Setter;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import org.bson.Document;

import java.net.InetAddress;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Updates.set;

public class BungeeCorePlayer extends GlobalCorePlayer implements CorePlayer, OfflineCorePlayer {

    @Getter
    private final FriendData friendData;
    @Getter
    @Setter
    private long banTime, muteTime;
    @Getter
    private int banPoints = 0, mutePoints = 0;
    @Setter
    private boolean banned = false, muted = false;
    @Getter
    @Setter
    private Nick currentNick;

    public BungeeCorePlayer(CoreSystem instance, InetAddress address, UUID uuid, String name) {
        super(instance, address, uuid, name);

        Punish punish = BungeeCoreSystem.getSystem().getOverwatch().getPunishManager().getPunish(uuid);
        if (punish != null) {
            if (punish.isBanned()) {
                this.banned = true;
                this.banTime = punish.getBanEntry().getEnd();
            }

            if (punish.isMuted()) {
                this.muted = true;
                this.muteTime = punish.getMuteEntry().getEnd();
            }
        }

        Document pointsEntry = PunishManager.PUNISH_POINTS_COLLECTION.find(eq("uuid", uuid.toString())).first();
        if (pointsEntry != null) {
            this.banPoints = pointsEntry.getInteger("banpoints");
            this.mutePoints = pointsEntry.getInteger("mutepoints");
        }

        this.friendData = BungeeCoreSystem.getInstance().getFriendSystem().getData(uuid);

        ((BungeeCoreSystem) instance).getCorePlayers().put(uuid, this);
        CoreSystem.getInstance().sendConsoleMessage("Loaded Player " + name + "!");
    }

    @Override
    public ProxiedPlayer bungee() {
        return ProxyServer.getInstance().getPlayer(uuid);
    }

    @Override
    public boolean isMuted() {
        long millis = System.currentTimeMillis() / 1000;
        if (muted && muteTime <= millis) {
            muted = false;
            ProxyServer.getInstance().getScheduler().runAsync(BungeeCoreSystem.getInstance(), () ->
                    BungeeCoreSystem.getSystem().getOverwatch().getPunishManager().unMute(uuid)
            );
        }

        return muted;
    }

    @Override
    public boolean isBanned() {
        long millis = System.currentTimeMillis() / 1000;
        if (banned && banTime <= millis) {
            banned = false;
            ProxyServer.getInstance().getScheduler().runAsync(BungeeCoreSystem.getInstance(), () ->
                    BungeeCoreSystem.getSystem().getOverwatch().getPunishManager().unBan(uuid)
            );
        }

        return banned;
    }

    @Override
    public void updateSettings(PlayerSettings settings) {
        PlayerSettings oldSettings = this.settings;
        this.settings = settings;

        ProxyServer.getInstance().getPluginManager().callEvent(new PlayerSettingsChangeEvent(this, oldSettings));
        CoreSystem.getInstance().getChannelHandler().createInfoRequest(bungee(), "PLAYER_SETTINGS", CoreSystem.getInstance().getGson().toJson(settings, PlayerSettings.class));

        BungeeCoreSystem.getSystem().getMongoDB(Database.SYSTEM).getCollection("userinfo").updateOne(eq("uuid", uuid.toString()), set("player_settings", Document.parse(instance.getGson().toJson(settings, PlayerSettings.class))));
    }

    @Override
    public void updateTrust() {
        BungeeCoreSystem.getSystem().getMongoDB(Database.SYSTEM).getCollection("userinfo").updateOne(eq("uuid", this.uuid.toString()), set("trustedUser", new TrustedUser()));
    }

    @Override
    public boolean isNew() {
        return isNew;
    }

    @Override
    public void sendMessage(String message) {
        bungee().sendMessage(TextComponent.fromLegacyText(message));
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
        instance.runAsync(() -> {
            ProxyServer.getInstance().getPluginManager().callEvent(new PermissionChangeEvent(PermissionChangeEvent.Type.GROUP_CHANGE, this, groupSet));
            ((CoreModuleCoreSystem) instance).getMongoDB(Database.SYSTEM).getCollection("userinfo").updateOne(
                    eq("uuid", uuid.toString()),
                    set("groups", instance.getPermissionManager().getGroupIDs(groupSet))
            );
        });
    }

    public void unregister() {
        BungeeCoreSystem.getSystem().getCorePlayers().remove(uuid);
        CoreSystem.getInstance().sendConsoleMessage("Unloaded Player " + name + "!");
    }
}
