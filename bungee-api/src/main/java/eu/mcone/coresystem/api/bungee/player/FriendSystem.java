/*
 * Copyright (c) 2017 - 2018 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.coresystem.api.bungee.player;

import java.util.UUID;

public interface FriendSystem {

    FriendData getData(UUID uuid);


    void addFriend(UUID player, UUID friend, String friendName);

    void removeFriend(UUID player, UUID friend);


    void addRequest(UUID player, UUID friend, String friendName);

    void removeRequest(UUID player, UUID friend);


    void addBlock(UUID player, UUID friend);

    void removeBlock(UUID player, UUID friend);


    void addToggled(UUID player);

    void removeToggled(UUID player);

}
