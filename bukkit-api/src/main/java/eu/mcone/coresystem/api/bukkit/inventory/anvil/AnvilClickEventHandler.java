/*
 * Copyright (c) 2017 - 2019 Dominik Lippl, Rufus Maiwald, Felix Schmid and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.api.bukkit.inventory.anvil;

public interface AnvilClickEventHandler {

    /**
     * called when a player clicks on an item in the anvil inventory
     * @param event the event containing all relevant data
     */
    void onAnvilClick(AnvilClickEvent event);

}
