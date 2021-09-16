/*
 * Copyright (c) 2017 - 2020 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.api.bukkit.util;

import eu.mcone.coresystem.api.bukkit.CorePlugin;
import eu.mcone.coresystem.api.bukkit.command.CoreCommand;
import eu.mcone.coresystem.api.bukkit.inventory.CoreInventory;
import eu.mcone.coresystem.api.bukkit.inventory.modify.CoreInventoryModifier;
import eu.mcone.coresystem.api.bukkit.inventory.modification.InventoryModificationManager;
import eu.mcone.coresystem.api.bukkit.world.CoreWorld;
import eu.mcone.coresystem.api.core.util.GlobalPluginManager;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.Set;

public interface PluginManager extends GlobalPluginManager {

    /**
     * returns the registered inventoryModificationManager for the specified CorePlugin
     *
     * @param plugin CorePlugin
     * @return InventoryModificationManager instance
     */
    InventoryModificationManager getInventoryModificationManager(CorePlugin plugin);

    /**
     * registers a coreInventory for the specified
     *
     * @param player        Bukkit Player
     * @param inventory     CoreInventory
     */
    void registerCoreInventory(Player player, CoreInventory inventory);

    /**
     * returns all current saved CoreInventories
     *
     * @return list of all CoreInventories
     */
    CoreInventory getCurrentCoreInventory(Player player);

    /**
     * registers a new CoreCommand
     *
     * @param coreCommand CoreCommand
     * @param plugin      CorePlugin
     * @return CoreCommand object
     */
    CoreCommand registerCoreCommand(CoreCommand coreCommand, CorePlugin plugin);

    /**
     * removes a CoreCommand from CommandMap
     *
     * @param command command to be removed
     */
    void unregisterCoreCommand(CoreCommand command);

    /**
     * returns the CoreCommand with a specific name
     * null if no CoreCommand with that name exists
     *
     * @param plugin CorePlugin
     * @param name   command name
     * @return CoreCommand object
     */
    CoreCommand getCoreCommand(CorePlugin plugin, String name);

    /**
     * returns all current saved CoreCommands
     *
     * @return Map of all CoreCommands
     */
    Collection<CoreCommand> getCoreCommands();

    /**
     * returns all current saved CoreCommands registered by a specific plugin
     *
     * @param plugin CorePlugin
     * @return Map of all plugin CoreCommands
     */
    Collection<CoreCommand> getCoreCommands(CorePlugin plugin);

    boolean registerCoreInventoryModifier(Class<?> inventoryClass, CoreInventoryModifier modifier);

    Set<CoreInventoryModifier> getCoreInventoryModifiers(Class<?> inventoryClass);

    boolean unregisterCoreInventoryModifier(Class<?> inventoryClass, CoreInventoryModifier modifier);

    /**
     * sets the default world for all GameProfiles (might not be necessary)
     *
     * @param world world
     */
    void setGameProfileWorld(CoreWorld world);

    /**
     * returns the set GameProfile world
     *
     * @return default world name used for all game profiles where no world is set
     */
    String getGameProfileWorld();

}
