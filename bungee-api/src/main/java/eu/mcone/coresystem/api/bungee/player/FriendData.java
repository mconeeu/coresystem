/*
 * Copyright (c) 2017 - 2019 Dominik Lippl, Rufus Maiwald, Felix Schmid and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.api.bungee.player;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface FriendData {

    Map<UUID, String> getFriends();

    Map<UUID, String> getRequests();

    List<UUID> getBlocks();

}
