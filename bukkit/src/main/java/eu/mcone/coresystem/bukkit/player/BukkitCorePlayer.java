/*
 * Copyright (c) 2017 - 2018 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.coresystem.bukkit.player;

import eu.mcone.coresystem.api.bukkit.CoreSystem;
import eu.mcone.coresystem.api.bukkit.event.PlayerSettingsChangeEvent;
import eu.mcone.coresystem.api.bukkit.scoreboard.CoreScoreboard;
import eu.mcone.coresystem.api.bukkit.world.CoreLocation;
import eu.mcone.coresystem.api.bukkit.world.CoreWorld;
import eu.mcone.coresystem.api.core.exception.PlayerNotFoundException;
import eu.mcone.coresystem.api.core.player.PlayerSettings;
import eu.mcone.coresystem.api.core.player.PlayerStatus;
import eu.mcone.coresystem.bukkit.BukkitCoreSystem;
import eu.mcone.coresystem.bukkit.inventory.InteractionInventory;
import eu.mcone.coresystem.bukkit.util.AFKCheck;
import eu.mcone.coresystem.core.CoreModuleCoreSystem;
import eu.mcone.coresystem.core.mysql.Database;
import eu.mcone.coresystem.core.player.GlobalCorePlayer;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class BukkitCorePlayer extends GlobalCorePlayer implements eu.mcone.coresystem.api.bukkit.player.BukkitCorePlayer {

    @Getter
    private PlayerStatus status;
    @Getter @Setter
    private String nickname;
    @Getter
    private CoreScoreboard scoreboard;

    public BukkitCorePlayer(CoreSystem instance, String name) throws PlayerNotFoundException {
        super(instance, name);
        this.status = PlayerStatus.ONLINE;

        ((BukkitCoreSystem) instance).getCorePlayers().put(uuid, this);
        reloadPermissions();

        BukkitCoreSystem.getInstance().sendConsoleMessage("Loaded Player "+name+"!");
    }

    @Override
    public Player bukkit() {
        return Bukkit.getPlayer(uuid);
    }

    @Override
    public void setScoreboard(CoreScoreboard sb) {
        this.scoreboard = sb.set(BukkitCoreSystem.getInstance(), this);
    }

    @Override
    public void setStatus(final PlayerStatus status) {
        this.status = status;
        Bukkit.getScheduler().runTaskAsynchronously(BukkitCoreSystem.getInstance(), () -> ((BukkitCoreSystem) instance).getMySQL(Database.SYSTEM).update("UPDATE userinfo SET status='"+status.toString().toLowerCase()+"' WHERE uuid='"+uuid+"'"));
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
    public void openInteractionInventory(Player p) {
        new InteractionInventory(bukkit(), p);
    }

    @Override
    public void updateSettings(PlayerSettings settings) {
        Bukkit.getPluginManager().callEvent(new PlayerSettingsChangeEvent(this, settings));
        CoreSystem.getInstance().getChannelHandler().sendPluginMessage(bukkit(), "PLAYER_SETTINGS", CoreSystem.getInstance().getGson().toJson(settings, PlayerSettings.class));

        BukkitCoreSystem.getSystem().getMySQL(Database.SYSTEM).update(
                "UPDATE userinfo SET player_settings='"+((CoreModuleCoreSystem) instance).getGson().toJson(settings, PlayerSettings.class)+"' WHERE uuid ='"+uuid+"'"
        );
    }

    @Override
    public void sendMessage(String message) {
        bukkit().sendMessage(message);
    }

    @Override
    public void unregister() {
        BukkitCoreSystem.getSystem().clearPlayerInventories(uuid);
        AFKCheck.players.remove(uuid);
        AFKCheck.afkPlayers.remove(uuid);

        BukkitCoreSystem.getSystem().getCorePlayers().remove(uuid);
        BukkitCoreSystem.getInstance().sendConsoleMessage("Unloaded Player "+name);
    }

}
