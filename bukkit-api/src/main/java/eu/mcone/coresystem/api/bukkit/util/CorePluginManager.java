/*
 * Copyright (c) 2017 - 2018 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.coresystem.api.bukkit.util;

import eu.mcone.coresystem.api.bukkit.CorePlugin;
import eu.mcone.coresystem.api.bukkit.command.CoreCommand;
import eu.mcone.coresystem.api.bukkit.inventory.CoreInventory;
import eu.mcone.coresystem.api.core.exception.CoreException;
import eu.mcone.coresystem.api.core.util.CooldownSystem;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.List;

public interface CorePluginManager {

    CooldownSystem getCooldownSystem();

    List<CorePlugin> getCorePlugins();

    /**
     * registers new CorePlugin in BCS
     * @param plugin CorePlugin
     * @throws CoreException thrown if the plugin is already registered in BCS
     */
    void registerCorePlugin(CorePlugin plugin) throws CoreException;

    /**
     * registers a new CoreInventory (not necessary, extending the CoreInventory class will do this)
     * @param inventory CoreInventory
     * @param player Player
     */
    void registerCoreInventory(CoreInventory inventory, Player player);

    /**
     * returns the CoreInventory with a specific name
     * null if no CoreInventory with that name exists
     * @param player Player
     * @return CoreInventory object
     */
    CoreInventory getCoreInventory(Player player);

    /**
     * returns all current saved CoreInventories
     * @return list of all CoreInventories
     */
    Collection<CoreInventory> getCoreInventories();

    /**
     * registers a new CoreCommand
     * @param coreCommand CoreCommand
     * @param plugin CorePlugin
     * @return CoreCommand object
     */
    CoreCommand registerCoreCommand(CoreCommand coreCommand, CorePlugin plugin);

    /**
     * returns the CoreCommand with a specific name
     * null if no CoreCommand with that name exists
     * @param plugin CorePlugin
     * @param name command name
     * @return CoreCommand object
     */
    CoreCommand getCoreCommand(CorePlugin plugin, String name);

    /**
     * returns all current saved CoreCommands
     * @return Map of all CoreCommands
     */
    Collection<CoreCommand> getCoreCommands();

    /**
     * returns all current saved CoreCommands registered by a specific plugin
     * @param plugin CorePlugin
     * @return Map of all plugin CoreCommands
     */
    Collection<CoreCommand> getCoreCommands(CorePlugin plugin);

}
