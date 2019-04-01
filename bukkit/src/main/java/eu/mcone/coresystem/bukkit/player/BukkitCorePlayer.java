/*
 * Copyright (c) 2017 - 2019 Dominik Lippl, Rufus Maiwald, Felix Schmid and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.bukkit.player;

import eu.mcone.coresystem.api.bukkit.CoreSystem;
import eu.mcone.coresystem.api.bukkit.event.PlayerSettingsChangeEvent;
import eu.mcone.coresystem.api.bukkit.player.CorePlayer;
import eu.mcone.coresystem.api.bukkit.player.OfflineCorePlayer;
import eu.mcone.coresystem.api.bukkit.player.Stats;
import eu.mcone.coresystem.api.bukkit.scoreboard.CoreScoreboard;
import eu.mcone.coresystem.api.bukkit.world.CoreLocation;
import eu.mcone.coresystem.api.bukkit.world.CoreWorld;
import eu.mcone.coresystem.api.core.gamemode.Gamemode;
import eu.mcone.coresystem.api.core.player.PlayerSettings;
import eu.mcone.coresystem.api.core.player.SkinInfo;
import eu.mcone.coresystem.bukkit.BukkitCoreSystem;
import eu.mcone.coresystem.core.CoreModuleCoreSystem;
import eu.mcone.coresystem.core.player.GlobalCorePlayer;
import eu.mcone.networkmanager.core.api.database.Database;
import lombok.Getter;
import lombok.Setter;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachment;

import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Updates.combine;
import static com.mongodb.client.model.Updates.set;

public class BukkitCorePlayer extends GlobalCorePlayer implements CorePlayer, OfflineCorePlayer {

    @Getter @Setter
    private String nickname;
    @Getter
    private CoreScoreboard scoreboard;
    private Map<Gamemode, StatsAPI> stats;
    @Getter
    private SkinInfo skin;
    @Getter
    private final PermissionAttachment permissionAttachment;

    public BukkitCorePlayer(CoreSystem instance, InetAddress address, SkinInfo skinInfo, Player p) {
        super(instance, address, p.getUniqueId(), p.getName());
        this.stats = new HashMap<>();
        this.skin = skinInfo;

        permissionAttachment = p.addAttachment(BukkitCoreSystem.getSystem());
        for (String permission : permissions) {
            permissionAttachment.setPermission(permission.startsWith("-") ? permission.substring(1) : permission, !permission.startsWith("-"));
        }

        instance.runAsync(() -> ((BukkitCoreSystem) instance).getMongoDB(Database.SYSTEM).getCollection("userinfo").updateOne(
                eq("uuid", uuid.toString()),
                combine(
                        set("texture_value", skinInfo.getValue()),
                        set("texture_signature", skinInfo.getSignature())
                )
        ));

        ((BukkitCoreSystem) instance).getCorePlayers().put(uuid, this);
        BukkitCoreSystem.getInstance().sendConsoleMessage("Loaded Player " + name + "!");
    }

    @Override
    public Player bukkit() {
        return Bukkit.getPlayer(uuid);
    }

    @Override
    public void setScoreboard(CoreScoreboard sb) {
        if (this.scoreboard != null) scoreboard.unregister();
        this.scoreboard = sb.set(this);
    }

    @Override
    public CoreWorld getWorld() {
        return BukkitCoreSystem.getInstance().getWorldManager().getWorld(bukkit().getWorld());
    }

    @Deprecated
    @Override
    public CoreLocation getLocation() {
        return new CoreLocation(
                bukkit().getLocation().getX(),
                bukkit().getLocation().getY(),
                bukkit().getLocation().getZ(),
                bukkit().getLocation().getYaw(),
                bukkit().getLocation().getPitch()
        );
    }

    @Override
    public Stats getStats(Gamemode gamemode) {
        if (stats.containsKey(gamemode)) {
            return stats.get(gamemode);
        } else {
            StatsAPI api = new StatsAPI((BukkitCoreSystem) instance, this, gamemode);
            stats.put(gamemode, api);
            return api;
        }
    }

    @Override
    public boolean isAfk() {
        return BukkitCoreSystem.getSystem().getAfkManager().isAfk(uuid);
    }

    @Override
    public long getAfkTime() {
        return BukkitCoreSystem.getSystem().getAfkManager().getAfkTime(uuid);
    }

    @Override
    public void updateSettings() {
        Bukkit.getPluginManager().callEvent(new PlayerSettingsChangeEvent(this, settings));
        CoreSystem.getInstance().getChannelHandler().createSetRequest(bukkit(), "PLAYER_SETTINGS", CoreSystem.getInstance().getGson().toJson(settings, PlayerSettings.class));
        BukkitCoreSystem.getSystem().getMongoDB(Database.SYSTEM).getCollection("userinfo").updateOne(eq("uuid", uuid.toString()), set("player_settings", Document.parse(((CoreModuleCoreSystem) instance).getGson().toJson(settings, PlayerSettings.class))));
    }

    @Override
    public void sendMessage(String message) {
        bukkit().sendMessage(message);
    }

    @Override
    public void unregister() {
        scoreboard.unregister();
        if (permissionAttachment != null) bukkit().removeAttachment(permissionAttachment);
        BukkitCoreSystem.getSystem().getAfkManager().unregisterPlayer(uuid);
        BukkitCoreSystem.getSystem().getCorePlayers().remove(uuid);

        BukkitCoreSystem.getInstance().sendConsoleMessage("Unloaded Player " + name);
    }

}
