/*
 * Copyright (c) 2017 - 2018 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.coresystem.api.bukkit.npc;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.Map;

public interface NpcManager {

    /**
     * reload all NPCs from Database
     */
    void reload();

    /**
     * creates new local NPC that dont gets saved in the database
     * @param name data name
     * @param location location
     * @param texture texture name
     * @param displayname displayname
     */
    void addLocalNPC(String name, Location location, String texture, String displayname);

    /**
     * create new NPC
     * @param name data name
     * @param location location
     * @param texture texture name
     * @param displayname displayname
     */
    void addNPC(String name, Location location, String texture, String displayname);

    /**
     * update existing NPC
     * @param name data name
     * @param location location
     * @param texture texture name
     * @param displayname displayname
     */
    void updateNPC(String name, Location location, String texture, String displayname);

    /**
     * remove existing npc
     * @param name data name
     */
    void removeNPC(String name);

    /**
     * check name is a NPCs name
     * @param playerName name
     * @return boolean is NPC
     */
    boolean isNPC(String playerName);

    /**
     * update NPCs for every player
     */
    void updateNPCs();

    /**
     * set all NPCs for specific player (normally not necessary)
     * @param player player
     */
    void setNPCs(Player player);

    /**
     * unset all NPCs for specific player
     * @param player
     */
    void unsetNPCs(Player player);

    /**
     * unset NPCs for every player
     */
    void unsetNPCs();

    /**
     * returns a NPC with the given name. null if no NPC with this name exists
     * @param name data name
     * @return NPC
     */
    NPC getNPC(String name);

    /**
     * returns all loaded NPCs
     * @return Map with all NPCs
     */
    Map<String, NPC> getNPCs();

}
