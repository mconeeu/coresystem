/*
 * Copyright (c) 2017 - 2020 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.api.bukkit.player.profile.interfaces;

import org.bukkit.entity.Player;

/**
 * use a CorePlugin instance that implements implement ManagerGetter
 */
public interface HomeManagerGetter {

    /**
     * return an Player Instance that gets home packets from GameProfile and implements the HomeManager class
     *
     * @param player Player that homes should be managed
     * @return EnderchestManager instance to modify a players homes
     */
    HomeManager getHomeManager(Player player);

}
