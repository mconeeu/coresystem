/*
 * Copyright (c) 2017 - 2018 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.coresystem.bukkit.player;

import eu.mcone.coresystem.api.bukkit.CoreSystem;
import eu.mcone.coresystem.api.bukkit.event.CoinsChangeEvent;
import eu.mcone.coresystem.api.bukkit.event.PlayerSettingsChangeEvent;
import eu.mcone.coresystem.api.bukkit.player.CorePlayer;
import eu.mcone.coresystem.api.bukkit.scoreboard.CoreScoreboard;
import eu.mcone.coresystem.api.bukkit.world.CoreLocation;
import eu.mcone.coresystem.api.bukkit.world.CoreWorld;
import eu.mcone.coresystem.api.core.exception.PlayerNotResolvedException;
import eu.mcone.coresystem.api.core.player.PlayerSettings;
import eu.mcone.coresystem.api.core.player.PlayerState;
import eu.mcone.coresystem.bukkit.BukkitCoreSystem;
import eu.mcone.coresystem.core.CoreModuleCoreSystem;
import eu.mcone.coresystem.core.player.GlobalCorePlayer;
import eu.mcone.networkmanager.core.api.database.Database;
import lombok.Getter;
import lombok.Setter;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.net.InetAddress;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Updates.set;

public class BukkitCorePlayer extends GlobalCorePlayer implements CorePlayer {

    @Getter
    private PlayerState status;
    @Getter @Setter
    private String nickname;
    @Getter
    private CoreScoreboard scoreboard;

    public BukkitCorePlayer(CoreSystem instance, InetAddress address, String name) throws PlayerNotResolvedException {
        super(instance, address, name);
        this.status = PlayerState.ONLINE;

        ((BukkitCoreSystem) instance).getCorePlayers().put(uuid, this);
        reloadPermissions();

        BukkitCoreSystem.getInstance().sendConsoleMessage("Loaded Player "+name+"!");
    }

    @Override
    public Player bukkit() {
        return Bukkit.getPlayer(uuid);
    }

    @Override
    public void addCoins(int amount) {
        this.coins += amount;
        Bukkit.getScheduler().runTaskAsynchronously((BukkitCoreSystem) instance, () -> {
            ((BukkitCoreSystem) instance).getMongoDB(Database.SYSTEM).getCollection("userinfo").updateOne(eq("uuid", uuid.toString()), set("coins", coins));
            Bukkit.getServer().getPluginManager().callEvent(new CoinsChangeEvent(this));
        });
    }

    @Override
    public void removeCoins(int amount) {
        this.coins -= amount;
        Bukkit.getScheduler().runTaskAsynchronously((BukkitCoreSystem) instance, () -> {
            ((BukkitCoreSystem) instance).getMongoDB(eu.mcone.networkmanager.core.api.database.Database.SYSTEM).getCollection("userinfo").updateOne(eq("uuid", uuid.toString()), set("coins", coins));
            Bukkit.getPluginManager().callEvent(new CoinsChangeEvent(this));
        });
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

    @Override
    public CoreLocation getLocation() {
        return new CoreLocation(
                getWorld().getName(),
                bukkit().getLocation().getX(),
                bukkit().getLocation().getY(),
                bukkit().getLocation().getZ(),
                bukkit().getLocation().getYaw(),
                bukkit().getLocation().getPitch()
        );
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
        BukkitCoreSystem.getSystem().getMongoDB(Database.SYSTEM).getCollection("userinfo").updateOne(eq("uuid", uuid), set("player_settings", Document.parse(((CoreModuleCoreSystem) instance).getGson().toJson(settings, PlayerSettings.class))));
    }

    @Override
    public void sendMessage(String message) {
        bukkit().sendMessage(message);
    }

    @Override
    public void unregister() {
        scoreboard.unregister();
        BukkitCoreSystem.getSystem().getAfkManager().unregisterPlayer(uuid);
        BukkitCoreSystem.getSystem().getCorePlayers().remove(uuid);

        BukkitCoreSystem.getInstance().sendConsoleMessage("Unloaded Player "+name);
    }

}
