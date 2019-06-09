/*
 * Copyright (c) 2017 - 2019 Dominik Lippl, Rufus Maiwald, Felix Schmid and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.api.bukkit.world;

import eu.mcone.coresystem.api.bukkit.hologram.Hologram;
import eu.mcone.coresystem.api.bukkit.npc.NPC;
import org.bukkit.Difficulty;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldType;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Map;

public interface CoreWorld {

    /**
     * get the worlds name
     * @return name
     */
    String getName();

    /**
     * get the worlds saved alias name
     * @return world alias
     */
    String getAlias();

    /**
     * get the world type
     * @return world type
     */
    WorldType getWorldType();

    /**
     * get the world environment
     * @return world environment
     */
    World.Environment getEnvironment();

    /**
     * get the worlds difficulty
     * @return difficulty
     */
    Difficulty getDifficulty();

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
     * check if the world is default loaded on startup
     * @return world is loaded on startup
     */
    boolean isLoadOnStartup();

    /**
     * check if worlds auto-save mechanism is toggled on
     * @return autosave on
     */
    boolean isAutoSave();

    /**
     * check if pvp is enabled
     * @return pvp enabled
     */
    boolean isPvp();

    /**
     * check if animals are allowed
     * @return animals allowed
     */
    boolean isAllowAnimals();

    /**
     * check if monsters are allowed
     * @return monsters allowed
     */
    boolean isAllowMonsters();

    /**
     * check if spawn should be kept in memory
     * @return keep spawn in memory
     */
    boolean isKeepSpawnInMemory();

    /**
     * set world type
     * @param worldType world type
     */
    void setWorldType(WorldType worldType);

    /**
     * set world environment
     * @param environment world environment
     */
    void setEnvironment(World.Environment environment);

    /**
     * set difficulty
     * @param difficulty difficulty
     */
    void setDifficulty(Difficulty difficulty);

    /**
     * set generator name
     * @param generator generator name
     */
    void setGenerator(String generator);

    /**
     * set generator settings
     * @param settings generator settings
     */
    void setGeneratorSettings(String settings);

    /**
     * set if world should generate structures
     * @param generate boolean to generate structures
     */
    void setGenerateStructures(boolean generate);

    /**
     * set if the world should be loaded on startup
     * @param load boolean to load on startup
     */
    void setLoadOnStartup(boolean load);

    /**
     * set spawn location
     * @param location location
     */
    void setSpawnLocation(Location location);

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
     * teleports a player to a saved location by its name
     * @param player Bukkit Player
     * @param locationName name of the saved location
     */
    void teleport(Player player, String locationName);

    /**
     * teleports a player to a saved location by its name without notifying him
     * @param player Bukkit Player
     * @param locationName name of the saved location
     */
    void teleportSilently(Player player, String locationName);

    /**
     * get saved location from the internal world storage
     * @param name location name
     * @return Location
     */
    Location getLocation(String name);

    /**
     * get all saved location
     * @return Collection of all Locations
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
     * returns all NPC that are registered for this world
     * @return NPC List
     */
    List<NPC> getNPCs();

    /**
     * get a specific NPC by name
     * @param name name of wished NPC
     * @return NPC object
     */
    NPC getNPC(String name);

    /**
     * returns all Hologram that are registered for this world
     * @return Hologram List
     */
    List<Hologram> getHolograms();

    /**
     * get a specific Hologram by name
     * @param name name of wished Hologram
     * @return Hologram object
     */
    Hologram getHologram(String name);

    /**
     * change the worlds name
     * @param name new name
     */
    void changeName(String name);

    /**
     * purge all animals
     */
    void purgeAnimals();

    /**
     * purge all monsters
     */
    void purgeMonsters();

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
     * upload the world to the mc1cloud database
     * @return if upload was successful
     */
    boolean upload();

    /**
     * uploads the world to the mc1data beta database
     * @return if upload was successful
     */
    boolean betaUpload();

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
