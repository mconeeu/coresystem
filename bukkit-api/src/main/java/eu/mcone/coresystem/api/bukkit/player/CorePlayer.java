/*
 * Copyright (c) 2017 - 2018 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.coresystem.api.bukkit.player;

import eu.mcone.coresystem.api.bukkit.scoreboard.CoreScoreboard;
import eu.mcone.coresystem.api.bukkit.world.CoreLocation;
import eu.mcone.coresystem.api.bukkit.world.CoreWorld;
import eu.mcone.coresystem.api.core.gamemode.Gamemode;
import eu.mcone.coresystem.api.core.player.GlobalCorePlayer;
import eu.mcone.coresystem.api.core.player.SkinInfo;
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
     * returns the players current CoreLocation
     * @return CoreLocation
     */
    CoreLocation getLocation();

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
     * sets the player a custom scoreboard
     * @param scoreboard scoreboard
     */
    void setScoreboard(CoreScoreboard scoreboard);

}
