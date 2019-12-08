/*
 * Copyright (c) 2017 - 2019 Dominik Lippl, Rufus Maiwald, Felix Schmid and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.api.bukkit.npc;

import eu.mcone.coresystem.api.bukkit.npc.data.AbstractNpcData;
import eu.mcone.coresystem.api.bukkit.npc.enums.NpcAnimation;
import eu.mcone.coresystem.api.bukkit.npc.enums.NpcState;
import eu.mcone.coresystem.api.bukkit.spawnable.PlayerListModeToggleable;
import eu.mcone.coresystem.api.bukkit.world.CoreLocation;
import net.minecraft.server.v1_8_R3.Packet;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public interface NPC extends PlayerListModeToggleable {

    /**
     * returns the general NPC Data
     * @return NpcData
     */
    NpcData getData();

    /**
     * returns the Entity specific packets (like <i>skin</i> for PLAYER)
     * @return Entity specific packets
     */
    <T extends AbstractNpcData> T getEntityData();

    /**
     * returns the currently used entity-id by the NPC
     * @return entity-id
     */
    int getEntityId();

    /**
     * Updates the displayname of the entity (if specific players are chosen, this update is temporary and will not be saved permanently to NpcData)
     * @param displayname displayname (must not be longer than 16 chars, including color code chars)
     * @param players players which should receive the displayname update (choose no players to send the update to all players and save it in NpcData & core-config)
     */
    void changeDisplayname(String displayname, Player... players);

    /**
     * Updates the NPCs packets and sends update packets for all changes to visible players dynamically
     * @param data new NpcData
     */
    void update(NpcData data);

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

    void sendPackets(Packet<?>... packets);

}
