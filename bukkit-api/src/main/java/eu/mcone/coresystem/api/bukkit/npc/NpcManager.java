/*
 * Copyright (c) 2017 - 2018 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.coresystem.api.bukkit.npc;

import eu.mcone.coresystem.api.bukkit.world.CoreWorld;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.List;

public interface NpcManager {

    /**
     * reload all NPCs from Database
     */
    void reload();

    /**
     * creates new local NPC that dont gets saved in the database
     * @param name config name
     * @param displayname npc display name
     * @param skinName skin name from bungeesystem_textures database
     * @param skinKind choose PLAYER and pass an username as skinName or choose DATABASE and pass a database name as skinName
     * @param location location of the npc
     */
    void addLocalNPC(String name, String displayname, String skinName, NpcData.SkinKind skinKind, Location location);

    /**
     * create new NPC
     * @param name config name
     * @param displayname npc display name
     * @param skinName skin name from bungeesystem_textures database
     * @param skinKind choose PLAYER and pass an username as skinName or choose DATABASE and pass a database name as skinName
     * @param location location of the npc
     */
    void addNPC(String name, String displayname, String skinName, NpcData.SkinKind skinKind, Location location);

    /**
     * updates the NpcData of an existing NPC
     * @param oldNpc old NPC object
     * @param newData new data
     */
    void updateNPC(NPC oldNpc, NpcData newData);

    /**
     * remove existing npc
     * @param npc npc
     */
    void removeNPC(NPC npc);

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
     * @param world world
     * @param name data name
     * @return NPC
     */
    NPC getNPC(CoreWorld world, String name);

    /**
     * returns all loaded NPCs
     * @return Map with all NPCs
     */
    List<NPC> getNPCs();

}
