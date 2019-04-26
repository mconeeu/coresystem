/*
 * Copyright (c) 2017 - 2019 Dominik Lippl, Rufus Maiwald, Felix Schmid and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.api.bukkit.npc;

import eu.mcone.coresystem.api.bukkit.npc.enums.NpcVisibilityMode;
import eu.mcone.coresystem.api.bukkit.world.CoreWorld;
import org.bukkit.entity.Player;

import java.util.Collection;

public interface NpcManager {

    /**
     * reloads all NPCs and Data from the core-config
     */
    void reload();

    /**
     * adds a temporary NPC to the Server. This NPC will not be saved in the core-config and stays until server reload|restart or NPC-Manager reload
     * @param data npc data
     * @return the new Npc
     */
    NPC addNPC(NpcData data);

    /**
     * adds a temporary NPC to the Server. This NPC will not be saved in the core-config and stays until server reload|restart or NPC-Manager reload
     * use /npc or the core-config.json to add Holograms permanently
     * @param data npc data
     * @param mode Choose a Visbility mode and the players that should be on the list (i.e. BLACKLIST with no players means that all players can see the NPC)
     * @param players players that should be on the list
     * @return the new NPC
     */
    NPC addNPC(NpcData data, NpcVisibilityMode mode, Player... players);

    /**
     * removes an existing NPC (if its an permanent NPC from core-config this is just temporary)
     * if you want to delete NPC from core-config permanently use ingame command /npc remove
     * @param npc npc
     */
    void removeNPC(NPC npc);

    /**
     * returns an existing NPC from the given world with the given config-name (not displayname!)
     * null if no NPC with this name in the world exists
     * @param world world
     * @param name config-name (not displayname!)
     * @return NPC|null
     */
    NPC getNPC(CoreWorld world, String name);

    /**
     * returns an existing NPC with a specific entity id
     * null if no NPC with this entity id exists
     * @param entityId entity id
     * @return NPC|null
     */
    NPC getNPC(int entityId);

    /**
     * returns a collection of all registered NPCs
     * @return NPC Collection
     */
    Collection<NPC> getNpcs();

}
