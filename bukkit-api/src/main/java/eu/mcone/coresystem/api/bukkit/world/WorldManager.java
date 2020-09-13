/*
 * Copyright (c) 2017 - 2020 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.api.bukkit.world;

import eu.mcone.cloud.core.api.world.CloudWorldManager;
import org.bukkit.World;

import java.util.List;
import java.util.function.Consumer;

public interface WorldManager {

    /**
     * returns the instance of the cloud world manager
     *
     * @return Cloud world manager instance
     */
    CloudWorldManager getCloudWorldManager();

    /**
     * returns all loaded worlds
     *
     * @return Collection of CoreWorlds
     */
    List<CoreWorld> getWorlds();

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
     * checks if a world exists
     *
     * @param world World name
     * @return boolean
     */
    boolean existWorld(String world);

    /**
     * enables the /world upload [name] command
     *
     * @param enable enables the upload command
     */
    void enableUploadCommand(boolean enable);

    /**
     * To use for existing worlds
     *
     * @param name        name of the target World
     * @param environment Environment of the target world
     * @return new bukkit world
     */
    boolean importWorld(String name, World.Environment environment);

    /**
     * To use for new non-existing worlds
     *
     * @param name       name
     * @param properties WorldProperties for creating and maintaining
     * @return boolean created
     * @throws IllegalArgumentException thrown if one setting was formatted false, but world was created though
     */
    World createWorld(String name, WorldCreateProperties properties) throws IllegalArgumentException;

    /**
     * Downloads the world with the specified name from the database and imports it
     *
     * @param id        World ID
     * @param succeeded Consumer that accepts an boolean
     */
    void download(final String id, Consumer<Boolean> succeeded);

    /**
     * Uploads the world as byte array in the database
     *
     * @param world     CoreWorld
     * @param succeeded Consumer that accepts an boolean
     */
    void upload(final CoreWorld world, Consumer<Boolean> succeeded);

    /**
     * Checks if the world with the specified name exists in the database
     *
     * @param name WorldName
     * @return boolean
     */
    boolean existsWorldInDatabase(final String name);

    /**
     * reloads all world configs
     */
    void reload();

}
