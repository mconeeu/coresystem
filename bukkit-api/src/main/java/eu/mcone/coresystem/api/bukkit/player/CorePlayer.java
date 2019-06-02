/*
 * Copyright (c) 2017 - 2019 Dominik Lippl, Rufus Maiwald, Felix Schmid and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.api.bukkit.player;

import eu.mcone.coresystem.api.bukkit.scoreboard.CoreScoreboard;
import eu.mcone.coresystem.api.bukkit.world.CoreWorld;
import eu.mcone.coresystem.api.core.gamemode.Gamemode;
import eu.mcone.coresystem.api.core.player.GlobalCorePlayer;
import eu.mcone.coresystem.api.core.player.SkinInfo;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public interface CorePlayer extends GlobalCorePlayer {

    /**
     * returns the current nickname. null if player is not nicked
     * @return nickname
     */
    String getNickname();

    /**
     * returns the players CoreScoreboard. null if player has no scoreboard
     * @return CoreScoreboard
     */
    CoreScoreboard getScoreboard();

    /**
     * returns the bukkit Player object
     * @return bukkit player object
     */
    Player bukkit();

    /**
     * returns the player current CoreWorld
     * @return CoreWorld
     */
    CoreWorld getWorld();

    /**
     * returns the players stats info object for the specific gamemode
     * @param gamemode wished Gamemode
     * @return StatsAPI object
     */
    Stats getStats(Gamemode gamemode);

    /**
     * returns the players skin, given in Base64 encoded value and signature
     * @return SkinInfo object
     */
    SkinInfo getSkin();

    /**
     * teleports a player to a specific location with a waiting cooldown where the player is not allowed to move
     * @param location location to where the player should be teleported
     * @param cooldown the cooldown the player must not move in seconds
     */
    void teleportWithCooldown(Location location, int cooldown);

    /**
     * returns if the player afk
     * @return player is afk
     */
    boolean isAfk();

    /**
     * returns the current players afk time in seconds
     * @return afk time in seconds
     */
    long getAfkTime();

    /**
     * hides/unhides the player from all other players and from tablist
     * @param vanish if the player should be vanished or unvanished
     * @return boolean: true if the vanish status was changed, false if the player had already this vanish status
     */
    boolean setVanished(boolean vanish);

    /**
     * checks if the player is currently vanished
     * @return boolean: true if he is vanished, false otherwise
     */
    boolean isVanished();

    /**
     * sets the player a custom scoreboard
     * @param scoreboard scoreboard
     */
    void setScoreboard(CoreScoreboard scoreboard);

}
