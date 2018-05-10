/*
 * Copyright (c) 2017 - 2018 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.coresystem.api.bukkit.world;

import org.bukkit.World;

import java.util.Map;

public interface WorldManager {

    /**
     * @param name name of the world
     * @return this
     */
    CoreWorld getWorld(String name);

    /**
     * @param world Bukkit World object
     * @return this
     */
    CoreWorld getWorld(World world);


    /**
     * To use for existing worlds
     * @param name name of the target World
     * @param environment Environment of the target world
     * @return new bukkit world
     */
    boolean addWorld(String name, World.Environment environment);

    /**
     * To use for new non-existing worlds
     * @param name name
     * @param settings Map of world settings [key, value]
     * @return boolean created
     * @throws IllegalArgumentException thrown if one setting was formatted false, but world was created though
     */
    boolean addWorld(String name, Map<String, String> settings) throws IllegalArgumentException;

    /**
     * reloads all world configs
     */
    void reload();

}
