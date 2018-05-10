/*
 * Copyright (c) 2017 - 2018 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.coresystem.api.bukkit.player;

import eu.mcone.coresystem.api.core.gamemode.Gamemode;

import java.util.UUID;

public interface StatsAPI {

    int getKills(UUID uuid);

    int getWins(UUID uuid);

    int getLoses(UUID uuid);

    int getDeaths(UUID uuid);

    int getGoals(UUID uuid);


    void setKills(UUID uuid, int kills);

    void setDeaths(UUID uuid, int deaths);

    void setWins(UUID uuid, int wins);

    void setLoses(UUID uuid, int loses);

    void setGoals(UUID uuid, int goals);


    void addKills(UUID uuid, int kills);

    void addDeaths(UUID uuid, int deaths);

    void addWins(UUID uuid, int wins);

    void addLoses(UUID uuid, int loses);

    void addGoal(UUID uuid, int goals);


    void removeKills(UUID uuid, int kills);

    void removeDeaths(UUID uuid, int kills);

    void removeWins(UUID uuid, int kills);

    void removeLoses(UUID uuid, int kills);

    void removeGoals(UUID uuid, int kills);


    /**
     * returns the users place in hierarchy
     * @param uuid players uuid
     * @return place in hierarchy
     */
    int getUserRanking(UUID uuid);

    /**
     * returns the player Kill/Death stats
     * @param uuid players uuid
     * @return KD
     */
    double getKD(UUID uuid);

    /**
     * returns all data
     * @param uuid players uuid
     * @return data as int array
     */
    int[] getData(UUID uuid);

    /**
     * returns the target gamemode of this api
     * @return gamemode
     */
    Gamemode getGamemode();

}
