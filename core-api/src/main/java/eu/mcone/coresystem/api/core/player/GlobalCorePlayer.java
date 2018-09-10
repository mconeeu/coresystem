/*
 * Copyright (c) 2017 - 2018 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.coresystem.api.core.player;

import eu.mcone.coresystem.api.core.labymod.LabyModConnection;

public interface GlobalCorePlayer extends GlobalOfflineCorePlayer {

    /**
     * get players IP-Adress
     * @return IP-Adress as String
     */
    String getIpAdress();

    /**
     * adds the player a specific permission
     * this permission gets removed after the PermissionManager or the players permissions get reloaded
     * @param permission permission String
     */
    void addSemiPermission(String permission);

    /**
     * removes either a normal or a semi permission
     * if its a normal permission, it gets added after the PermissionManager or the players permissions get reloaded
     * @param permission permission String
     */
    void removeSemiPermission(String permission);

    /**
     * check if a player is nicked
     * @return boolean nicked
     */
    boolean isNicked();

    /**
     * returns the unique id and other LabyMod spefific data if player is connected via LabyMod
     * @return LabyModConnection object
     */
    LabyModConnection getLabyModConnection();

    /**
     * updates the set changes to the playerSettings on both bungee & bukkit
     */
    void updateSettings();

    /**
     * sends the player a message
     * @param message message
     */
    void sendMessage(String message);

    /**
     * unregisters a player from internal storage
     */
    void unregister();
    
}
