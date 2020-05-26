/*
 * Copyright (c) 2017 - 2020 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.api.bukkit.player;

import eu.mcone.coresystem.api.core.player.GlobalOfflineCorePlayer;
import eu.mcone.coresystem.api.core.player.SkinInfo;

public interface OfflineCorePlayer extends GlobalOfflineCorePlayer {

    /**
     * returns the current skin of the player
     *
     * @return skin info
     */
    SkinInfo getSkin();

}
