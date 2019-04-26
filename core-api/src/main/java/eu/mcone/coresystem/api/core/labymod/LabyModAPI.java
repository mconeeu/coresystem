/*
 * Copyright (c) 2017 - 2019 Dominik Lippl, Rufus Maiwald, Felix Schmid and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.api.core.labymod;

import eu.mcone.coresystem.api.core.player.GlobalCorePlayer;

import java.util.Map;

public interface LabyModAPI {

    /**
     * sends a list of permissions to the LabyMod client to forbid specific functions
     * @param player target player
     * @param permissions permission map
     */
    void sendPermissions(GlobalCorePlayer player, Map<LabyPermission, Boolean> permissions);

    /**
     * sends a specific message to the LabyMod client that can be read i.e. with a LabyMod AddOn
     * @param player target player
     * @param messageKey message key
     * @param json message data in JSON format
     */
    void sendServerMessage(GlobalCorePlayer player, String messageKey, String json);

}
