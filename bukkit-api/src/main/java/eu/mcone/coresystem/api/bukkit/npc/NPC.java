/*
 * Copyright (c) 2017 - 2019 Dominik Lippl, Rufus Maiwald, Felix Schmid and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.api.bukkit.npc;

import eu.mcone.coresystem.api.bukkit.npc.data.AbstractNpcData;
import eu.mcone.coresystem.api.bukkit.npc.entity.EntityProjectile;
import eu.mcone.coresystem.api.bukkit.npc.enums.NpcAnimation;
import eu.mcone.coresystem.api.bukkit.npc.enums.NpcState;
import eu.mcone.coresystem.api.bukkit.spawnable.PlayerListModeToggleable;
import eu.mcone.coresystem.api.bukkit.util.CoreProjectile;
import eu.mcone.coresystem.api.bukkit.world.CoreLocation;
import net.minecraft.server.v1_8_R3.Entity;
import net.minecraft.server.v1_8_R3.Packet;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

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
     * returns the created NMS entity instance
     * @return entity
     */
    <E extends Entity> E getEntity();

    /**
     * returns the live Location of the NPC
     * @return location
     */
    Location getLocation();

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

    void teleport(CoreLocation location, Player... players);

    /**
     * Launches a specified projectile from the players location
     * @param type Projectile type (Snowball etc.)
     * @return Returns a CoreProjectile object
     */
    CoreProjectile throwProjectile(EntityProjectile type);

    /**
     * Launches a specified projectile from the players location with a given vector (Velocity)
     * @param type Projectile type (Snowball etc.)
     * @return Returns a CoreProjectile object
     */
    CoreProjectile throwProjectile(EntityProjectile type, Vector vector);

    /**
     * Returns the current location from the NPC as bukkit Vector
     * @return The Vector from the player
     */
    Vector getVector();

    void sendPackets(Packet<?>... packets);

}
