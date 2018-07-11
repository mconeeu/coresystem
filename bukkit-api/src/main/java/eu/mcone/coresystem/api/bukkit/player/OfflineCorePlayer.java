/*
 * Copyright (c) 2017 - 2018 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.coresystem.api.bukkit.player;

import eu.mcone.coresystem.api.core.player.GlobalOfflineCorePlayer;

public interface OfflineCorePlayer extends GlobalOfflineCorePlayer {

    /**
     * loads all permissions
     * @return this
     */
    OfflineCorePlayer loadPermissions();

    /**
     * checks if player would have the specified permission
     * @param permission permission name
     * @return this
     */
    boolean hasPermission(String permission);

}
