/*
 * Copyright (c) 2017 - 2018 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.coresystem.api.core.labymod;

import eu.mcone.coresystem.api.core.player.GlobalCorePlayer;

import java.util.Map;

public interface LabyModAPI {

    void sendPermissions(GlobalCorePlayer player, Map<LabyPermission, Boolean> permissions);

    void sendServerMessage(GlobalCorePlayer player, String messageKey, String json);

}
