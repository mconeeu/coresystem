/*
 * Copyright (c) 2017 - 2018 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.coresystem.api.bukkit.player;

import eu.mcone.coresystem.api.bukkit.scoreboard.CoreScoreboard;
import eu.mcone.coresystem.api.bukkit.world.CoreLocation;
import eu.mcone.coresystem.api.bukkit.world.CoreWorld;
import eu.mcone.coresystem.api.core.player.GlobalCorePlayer;
import eu.mcone.coresystem.api.core.player.PlayerStatus;
import org.bukkit.entity.Player;

public interface BukkitCorePlayer extends GlobalCorePlayer {

    /**
     * returns the formatted status of the player
     * @return formatted status
     */
    PlayerStatus getStatus();

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
     * returns the players current CoreLocation
     * @return CoreLocation
     */
    CoreLocation getLocation();

    /**
     * opens the player an interaction inventory of a specific player
     * @param player target player
     */
    void openInteractionInventory(Player player);

    /**
     * sets the player a custom scoreboard
     * @param scoreboard scoreboard
     */
    void setScoreboard(CoreScoreboard scoreboard);

    /**
     * change the players status
     * @param status status
     */
    void setStatus(PlayerStatus status);

}
