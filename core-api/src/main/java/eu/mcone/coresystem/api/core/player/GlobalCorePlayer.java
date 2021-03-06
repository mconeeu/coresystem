/*
 * Copyright (c) 2017 - 2020 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.api.core.player;

import eu.mcone.coresystem.api.core.labymod.LabyModConnection;

public interface GlobalCorePlayer extends GlobalOfflineCorePlayer {

    /**
     * get players IP-Adress
     *
     * @return IP-Adress as String
     */
    String getIpAdress();

    /**
     * adds the player a specific permission
     * this permission gets removed after the PermissionManager or the players permissions get reloaded
     *
     * @param permission permission String
     */
    void addSemiPermission(String permission);

    /**
     * removes either a normal or a semi permission
     * if its a normal permission, it gets added after the PermissionManager or the players permissions get reloaded
     *
     * @param permission permission String
     */
    void removeSemiPermission(String permission);

    /**
     * check if a player is nicked
     *
     * @return boolean nicked
     */
    boolean isNicked();

    /**
     * returns the unique id and other LabyMod spefific packets if player is connected via LabyMod
     *
     * @return LabyModConnection object
     */
    LabyModConnection getLabyModConnection();

    /**
     * returns true if the player uses the LabyMod client
     *
     * @return true if LabyMod is used
     */
    boolean isLabyModPlayer();

    /**
     * updates the set changes to the playerSettings on both bungee & bukkit
     * @param settings
     */
    void updateSettings(PlayerSettings settings);

    /**
     * sends the player a message
     *
     * @param message message
     */
    void sendMessage(String message);

}
