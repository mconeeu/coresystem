/*
 * Copyright (c) 2017 - 2018 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.coresystem.bukkit.player;

import eu.mcone.coresystem.api.bukkit.CoreSystem;
import eu.mcone.coresystem.api.bukkit.scoreboard.CoreScoreboard;
import eu.mcone.coresystem.api.core.exception.PlayerNotFoundException;
import eu.mcone.coresystem.bukkit.BukkitCoreSystem;
import eu.mcone.coresystem.bukkit.inventory.InteractionInventory;
import eu.mcone.coresystem.bukkit.util.AFKCheck;
import eu.mcone.coresystem.core.mysql.Database;
import eu.mcone.coresystem.core.player.GlobalCorePlayer;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class BukkitCorePlayer extends GlobalCorePlayer implements eu.mcone.coresystem.api.bukkit.player.BukkitCorePlayer {

    private String status;
    @Getter @Setter
    private String nickname;
    @Getter
    private CoreScoreboard scoreboard;

    public BukkitCorePlayer(CoreSystem instance, String name) throws PlayerNotFoundException {
        super(instance, name);
        this.status = "online";

        ((BukkitCoreSystem) instance).getCorePlayers().put(uuid, this);
        reloadPermissions();

        System.out.println("Loaded Player "+name+"!");
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
    public void setStatus(final String status) {
        this.status = status;
        Bukkit.getScheduler().runTaskAsynchronously(BukkitCoreSystem.getInstance(), () -> ((BukkitCoreSystem) instance).getMySQL(Database.SYSTEM).update("UPDATE userinfo SET status='"+status+"' WHERE uuid='"+uuid+"'"));
    }

    @Override
    public void openInteractionInventory(Player p) {
        new InteractionInventory(bukkit(), p);
    }

    @Override
    public void sendMessage(String message) {
        bukkit().sendMessage(message);
    }

    @Override
    public void unregister() {
        BukkitCoreSystem.getInstance().clearPlayerInventories(uuid);
        AFKCheck.players.remove(uuid);
        AFKCheck.afkPlayers.remove(uuid);

        BukkitCoreSystem.getSystem().getCorePlayers().remove(uuid);
        System.out.println("Unloaded eu.mcone.coresystem.api.core.player "+name);
    }

    public String getStatus() {
        switch (status) {
            case "online":
                return "§aonline";
            case "afk":
                return "§6AFK";
            default:
                return "§aonline";
        }
    }

}
