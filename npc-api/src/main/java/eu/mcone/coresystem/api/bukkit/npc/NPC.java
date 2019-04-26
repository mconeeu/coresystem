/*
 * Copyright (c) 2017 - 2019 Dominik Lippl, Rufus Maiwald, Felix Schmid and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.api.bukkit.npc;

import eu.mcone.coresystem.api.bukkit.npc.data.AbstractNpcData;
import eu.mcone.coresystem.api.bukkit.npc.enums.NpcAnimation;
import eu.mcone.coresystem.api.bukkit.npc.enums.NpcState;
import eu.mcone.coresystem.api.bukkit.npc.enums.NpcVisibilityMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.Collection;

public interface NPC {

    /**
     * returns the general NPC Data
     * @return NpcData
     */
    NpcData getData();

    /**
     * returns the Entity specific data (like <i>skin</i> for PLAYER)
     * @return Entity specific data
     */
    <T extends AbstractNpcData> T getEntityData();

    /**
     * returns the currently used entity-id by the NPC
     * @return entity-id
     */
    int getEntityId();

    /**
     * !!! DO NOT USE THIS !!!
     * NPC spawning gets automatically handled by the NpcVisibility setting set through NPC#toggleNpcVisibility() or NPC#toggleVisibility()
     * sends spawn packets to a specific player
     * @param player target player
     */
    void spawn(Player player);

    /**
     * !!! DO NOT USE THIS !!!
     * NPC spawning gets automatically handled by the NpcVisibility setting set through NPC#toggleNpcVisibility() or NPC#toggleVisibility()
     * sends spawn packets to a specific player
     * @param player target player
     */
    void despawn(Player player);

    /**
     * Updates the displayname of the entity (if specific players are chosen, this update is temporary and will not be saved permanently to NpcData)
     * @param displayname displayname (must not be longer than 16 chars, including color code chars)
     * @param players players which should receive the displayname update (choose no players to send the update to all players and save it in NpcData & core-config)
     */
    void changeDisplayname(String displayname, Player... players);

    /**
     * Updates the NPCs data and sends update packets for all changes to visible players dynamically
     * @param data new NpcData
     */
    void update(NpcData data);

    /**
     * returns a Collection of all players that are currently allowed to see the NPC
     * @return visible players list
     */
    Collection<Player> getVisiblePlayersList();

    /**
     * Overrides the current NpcVisibility settings and visible players list
     * Choose a Visbility mode and the players that should be on the list (i.e. BLACKLIST with no players means that all players can see the NPC)
     * @param visibility visibility mode
     * @param players players that should be on the list
     */
    void toggleNpcVisibility(NpcVisibilityMode visibility, Player... players);

    /**
     * makes the NPC either visible or unvisible for a specific player
     * this will add|remove the player to the NPCs black-|whitelist
     * @param player target player
     * @param canSee if the NPC should be shown or hidden
     */
    void toggleVisibility(Player player, boolean canSee);

    /**
     * returns if the player is allowed to see the NPC if the player is in its range. Calculated by the NpcVisibility settings
     * @param player target player
     * @return if player is allowed to see NPC due to NpcVisibility settings
     */
    boolean isVisibleFor(Player player);

    /**
     * returns if the NPC can be seen by a specific player. Calculated by the range between the two entities
     * @param player target player
     * @return if player can see NPC
     */
    boolean canBeSeenBy(Player player);

    /**
     * Sends a NPC state to specific players (if no players are chosen, all players will receive the update)
     * @param state state
     * @param players players which should receive the displayname update (choose no players to send the update to all players)
     */
    void sendState(NpcState state, Player... players);

    /**
     * Sends a NPC animation to specific players (if no players are chosen, all players will receive the update)
     * @param animation animation
     * @param players players which should receive the displayname update (choose no players to send the update to all players)
     */
    void sendAnimation(NpcAnimation animation, Player... players);

    /**
     * Sends a Teleport packet (if specific players are chosen, this update is temporary and will not be saved permanently to NpcData)
     * @param location location to where the NPC should be teleported
     * @param players players which should receive the teleport packet (choose no players to send the update to all players and save it in NpcData & core-config)
     */
    void teleport(Location location, Player... players);

    /**
     * Sends a Teleport packet (if specific players are chosen, this update is temporary and will not be saved permanently to NpcData)
     * @param location location to where the NPC should be teleported
     * @param players players which should receive the teleport packet (choose no players to send the update to all players and save it in NpcData & core-config)
     */
    void teleport(CoreLocation location, Player... players);

}
