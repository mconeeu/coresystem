/*
 * Copyright (c) 2017 - 2019 Dominik Lippl, Rufus Maiwald, Felix Schmid and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.api.bukkit.inventory.modification;

import eu.mcone.coresystem.api.bukkit.gamemode.Gamemode;
import eu.mcone.coresystem.api.core.exception.RuntimeCoreException;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public interface InventoryModificationManager {

    /**
     * returns the Gamemode of the plugin that registered this InventoryModificationManager
     *
     * @return plugin Gamemode
     */
    Gamemode getGamemode();

    /**
     * Reloads the CoreInventoryModificationManager
     * this triggers all default & modified Inventories to get re pulled from the database
     */
    void reload();

    /**
     * Disables the CoreInventoryModificationManager
     * this triggers all modified Inventories to get pushed to the database
     */
    void disable();

    void registerInventories(ModifyInventory... modifyInventories);

    void registerInventory(ModifyInventory modifyInventory);

    /**
     * Returns all registered ModifyInventories
     *
     * @return List of GameInventories
     */
    List<ModifyInventory> getModifyInventories();

    List<ModifyInventory> getModifyInventories(Gamemode gamemode, String category);

    List<ModifyInventory> getModifyInventories(Gamemode gamemode);

    Set<String> getModifyInventoryCategories(Gamemode gamemode);

    /**
     * Modifies a specified inventory for the specified uuid
     *
     * @param player            Bukkit Player
     * @param modifyInventory   Modify Inventory that should get modified
     * @param modifiedInventory Bukkit Inventory
     */
    void modifyInventory(Player player, ModifyInventory modifyInventory, Inventory modifiedInventory);

    /**
     * Check if the player modified currently
     *
     * @param player Bukkit Player
     * @return boolean
     */
    boolean isCurrentlyModifying(Player player);

    /**
     * Returns the currently modifying inventory for the player
     *
     * @param player Bukkit Player
     * @return Current ModifyInventory
     */
    ModifyInventory getCurrentlyModifyingInventory(Player player);

    /**
     * Sets a player modifying an Inventory
     *  @param player Bukkit Player
     * @param inventory modifiying Inventory
     */
    ModifyInventory setCurrentlyModifying(Player player, ModifyInventory inventory);

    void removeCurrentlyModifying(Player player);

    /**
     * Creates the ModificationInventory for the player
     *
     * @param player Bukkit Player
     */
    void openGamemodeModificationInventory(Player player);

    void openCategoryModificationInventory(Player player, Gamemode gamemode);

    void openCategoryModificationInventory(Player player, Gamemode gamemode, String category);

    void openModifyInventory(Player player, String category, String name) throws RuntimeCoreException;

    /**
     * Checks if the inventory for the Player uuid and name is modified
     *
     * @param uuid   Player UUID
     * @param inv    the Modify inventory that should be checked
     * @return boolean
     */
    boolean hasInventoryModified(UUID uuid, ModifyInventory inv);

    Map<String, UUID> getModifiedInventoryItems(UUID uuid, ModifyInventory modifiedInventory);

    /**
     * Pushes the modifications for the specified player
     *
     * @param uuid Bukkit Player uuid
     */
    void saveModifications(UUID uuid);

}
