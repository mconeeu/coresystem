/*
 * Copyright (c) 2017 - 2018 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.coresystem.api.bungee.player;

import eu.mcone.coresystem.api.core.player.GlobalOfflineCorePlayer;

public interface OfflineCorePlayer extends GlobalOfflineCorePlayer {

    OfflineCorePlayer loadFriendData();

    OfflineCorePlayer loadPermissions();

    OfflineCorePlayer loadBanData();

    FriendData getFriendData();

    boolean hasPermission(String permission);

    boolean isBanned();

    boolean isMuted();

    int getBanPoints();

    int getMutePoints();

    long getBanTime();

    long getMuteTime();

}
