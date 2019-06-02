/*
 * Copyright (c) 2017 - 2019 Dominik Lippl, Rufus Maiwald, Felix Schmid and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.api.bukkit.player.profile.interfaces;

import eu.mcone.coresystem.api.bukkit.player.profile.PlayerInventoryProfile;
import org.bukkit.inventory.Inventory;

/**
 * Implement this Inventory into yout Player class that gets enderchest data from GameProfileClass
 */
public interface EnderchestManager<P extends PlayerInventoryProfile> {

    /**
     * return here the Enderchest inventory stored in your Player class that gets enderchest data from GameProfileClass
     * @return Playerbound Enderchest Inventory
     */
    Inventory getEnderchest();

    /**
     * replace the current Enderchest Inventory with the given
     * Make shure to save the GameProfile in database afterwards
     * @param inventory
     */
    void updateEnderchest(Inventory inventory);

    /**
     * Reload here the complete GameProfile
     * Make shure to save the GameProfile in database if PlayerEnderchestProfile#isSizeChange == true
     */
    P reload();

}
