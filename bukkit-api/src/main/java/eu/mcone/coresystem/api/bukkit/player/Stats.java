/*
 * Copyright (c) 2017 - 2018 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.coresystem.api.bukkit.player;

import eu.mcone.coresystem.api.core.gamemode.Gamemode;

public interface Stats {

    /**
     * Returns the player's current kills
     */
    int getKill();

    /**
     * Returns the player's current wins
     */
    int getWin();

    /**
     * Returns the player's current loses
     */
    int getLose();

    /**
     * Returns the player's current deaths
     */
    int getDeath();

    /**
     * Returns the gamemodes main goals
     */
    int getGoal();


    /**
     * Set the specified integer to the table column kill
     * @param kill >> specified Integer
     */
    void setKills(int kill);

    /**
     * Set the specified integer to the table column death
     * @param death >> specified Integer
     */
    void setDeaths(int death);

    /**
     * Set the specified integer to the table column win
     * @param win >> specified Integer
     */
    void setWins(int win);

    /**
     * Set the specified integer to the table column lose
     * @param lose >> specified Integer
     */
    void setLoses(int lose);

    /**
     * Set the specified integer to the table column goal
     * @param goal >> specified Integer
     */
    void setGoals(int goal);


    /**
     * Adds the specified integer to the table column kill
     * @param kill >> specified Integer
     */
    void addKills(int kill);

    /**
     * Adds the specified integer to the table column death
     * @param death >> specified Integer
     */
    void addDeaths(int death);

    /**
     * Adds the specified integer to the table column win
     * @param win >> specified Integer
     */
    void addWins(int win);

    /**
     * Adds the specified integer to the table column lose
     * @param lose >> specified Integer
     */
    void addLoses(int lose);

    /**
     * Adds the specified integer to the table column goal
     * @param goal >> specified Integer
     */
    void addGoal(int goal);


    /**
     * Removes the specified integer (kill) from the player statistics
     * @param kill >> specified Integer
     */
    void removeKills(int kill);

    /**
     * Removes the specified integer (Death) from the player statistics
     * @param death >> specified Integer
     */
    void removeDeaths(int death);

    /**
     * Removes the specified integer (kill) from the player statistics
     * @param win >> specified Integer
     */
    void removeWins(int win);

    /**
     * Removes the specified integer (Death) from the player statistics
     * @param lose >> specified Integer
     */
    void removeLoses(int lose);

    /**
     * Removes the specified integer (Death) from the player statistics
     * @param goal >> specified Integer
     */
    void removeGoals(int goal);


    /**
     * returns the users place in hierarchy
     * @return place in hierarchy
     */
    int getUserRanking();

    /**
     * returns the player Kill/Death stats
     * @return KD
     */
    double getKD();

    /**
     * returns all data
     * @return data as int array
     */
    int[] getData();

    /**
     * returns the target gamemode of this api
     * @return gamemode
     */
    Gamemode getGamemode();

}
