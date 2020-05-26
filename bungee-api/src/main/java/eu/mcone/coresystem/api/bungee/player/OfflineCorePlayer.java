/*
 * Copyright (c) 2017 - 2020 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.api.bungee.player;

import eu.mcone.coresystem.api.core.player.GlobalOfflineCorePlayer;
import eu.mcone.coresystem.api.core.player.Group;

import java.util.Set;

public interface OfflineCorePlayer extends GlobalOfflineCorePlayer {

    FriendData getFriendData();

    boolean isBanned();

    boolean isMuted();

    int getBanPoints();

    int getMutePoints();

    long getBanTime();

    long getMuteTime();

    /**
     * set the players groups
     *
     * @param groupList group set
     */
    void setGroups(Set<Group> groupList);

    /**
     * add a player group
     *
     * @param group groups
     */
    void addGroup(Group group);

    /**
     * remove a player group
     *
     * @param group group
     */
    void removeGroup(Group group);

}
