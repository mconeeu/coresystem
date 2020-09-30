/*
 * Copyright (c) 2017 - 2020 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.api.bukkit.player.profile.interfaces;

import eu.mcone.coresystem.api.bukkit.player.profile.PlayerInventoryProfile;
import org.bukkit.entity.Player;

/**
 * use a CorePlugin instance that implements implement ManagerGetter
 */
public interface EnderchestManagerGetter {

    /**
     * return an Player Instance that gets enderchest packets from GameProfile and implements the EnderchestManager class
     *
     * @param player Player that enderchest should be managed
     * @return EnderchestManager instance to modify a players enderchest
     */
    EnderchestManager<? extends PlayerInventoryProfile> getEnderchestManager(Player player);

}
