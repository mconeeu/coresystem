/*
 * Copyright (c) 2017 - 2018 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.coresystem.api.bukkit.world;

import org.bukkit.Difficulty;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldType;

import java.util.Map;

public interface CoreWorld {

    /**
     * get the worlds name
     * @return name
     */
    String getName();

    /**
     * get the world type
     * @return world type
     */
    String getWorldType();

    /**
     * get the world environment
     * @return world environment
     */
    String getEnvironment();

    /**
     * get the worlds difficulty
     * @return difficulty
     */
    String getDifficulty();

    /**
     * get the worlds generator name
     * @return generator name
     */
    String getGenerator();

    /**
     * get the worlds generator settings
     * @return generator settings
     */
    String getGeneratorSettings();

    /**
     * check if world is generating structures
     * @return generating structures
     */
    boolean isGenerateStructures();

    /**
     * get additional world properties
     * @return world properties
     */
    WorldProperties getProperties();

    /**
     * set world type
     * @param worldType world type
     * @return this
     */
    CoreWorld setWorldType(WorldType worldType);

    /**
     * set world environment
     * @param environment world environment
     * @return this
     */
    CoreWorld setEnvironment(World.Environment environment);

    /**
     * set difficulty
     * @param difficulty difficulty
     * @return this
     */
    CoreWorld setDifficulty(Difficulty difficulty);

    /**
     * set generator name
     * @param generator generator name
     * @return this
     */
    CoreWorld setGenerator(String generator);

    /**
     * set generator settings
     * @param settings generator settings
     * @return this
     */
    CoreWorld setGeneratorSettings(String settings);

    /**
     * set if world should generate structures
     * @param generate boolean to generate structures
     * @return this
     */
    CoreWorld generateStructures(boolean generate);

    /**
     * set spawn location
     * @param location
     * @return this
     */
    CoreWorld setSpawnLocation(Location location);

    /**
     * get the Bukkit World object
     * @return bukkit world
     */
    World bukkit();

    /**
     * get the worlds spawn location as int[]{x,y,z}
     * @return spawn location as int array
     */
    int[] getSpawnLocation();

    /**
     * get saved location from the internal world storage
     * @param name location name
     * @return CoreLocation
     */
    CoreLocation getLocation(String name);

    /**
     * get all saved location
     * @return Collection of all CoreLocations
     */
    Map<String, CoreLocation> getLocations();

    /**
     * add location to the internal world storage
     * @param name location name
     * @param location Location object
     * @return this
     */
    CoreWorld setLocation(String name, Location location);

    /**
     * remove location from the internal world storage
     * @param name location name
     * @return this
     */
    CoreWorld removeLocation(String name);

    /**
     * change the worlds name
     * @param name new name
     * @return this
     */
    void changeName(String name);

    /**
     * unloads the world from Bukkit
     * @param save should the world be saved before unloading?
     */
    void unload(boolean save);

    /**
     * ATTENTION: this deletes the world without making a backup. Be careful!
     * @return delete successful
     */
    boolean delete();

    /**
     * upload the world to the cloudsystem database
     */
    boolean upload();

    /**
     * save latest changes to bukkit
     */
    void save();

    /**
     * get json String of actual CoreWorld object
     * @return json String
     */
    String toString();

}
