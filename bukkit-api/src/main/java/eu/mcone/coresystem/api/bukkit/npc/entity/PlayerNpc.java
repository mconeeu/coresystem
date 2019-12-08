/*
 * Copyright (c) 2017 - 2019 Dominik Lippl, Rufus Maiwald, Felix Schmid and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.api.bukkit.npc.entity;

import eu.mcone.coresystem.api.bukkit.npc.NPC;
import eu.mcone.coresystem.api.bukkit.npc.capture.MotionCaptureData;
import eu.mcone.coresystem.api.bukkit.npc.capture.MotionPlayer;
import eu.mcone.coresystem.api.bukkit.npc.enums.EquipmentPosition;
import eu.mcone.coresystem.api.bukkit.world.CoreLocation;
import eu.mcone.coresystem.api.core.player.SkinInfo;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public interface PlayerNpc extends NPC {

    /**
     * returns the current UUID of the NPC
     * they UUID may change when changing name or skin of the NPC
     * @return current npcs uuid
     */
    UUID getUuid();

    MotionPlayer getMotionPlayer();

    /**
     * Sets a specific item in the NPCs inventory and makes it visible for other players
     * (if specific players are chosen, this update is temporary and will not be saved permanently to NpcData)
     * @param position equipment position
     * @param item item
     * @param players players which should receive the item update (choose no players to send the update to all players and save the item in PlayerNpcData & core-config)
     */
    void setEquipment(EquipmentPosition position, ItemStack item, Player... players);

    /**
     * Sends a packet to update the skin (if specific players are chosen, this update is temporary and will not be saved permanently to NpcData)
     * @param skin skin info
     * @param players players which should receive the skin update (choose no players to send the update to all players and save it in PlayerNpcData & core-config)
     */
    void setSkin(SkinInfo skin, Player... players);

    /**
     * returns the current skin of the NPC
     * @return skin info
     */
    SkinInfo getSkin();

    /**
     * set the NPC sleeping for all players on the same location
     * @param sleepWithoutBed if true the NPC lays on the ground, otherwise he gets ported higher
     */
    void setSleeping(boolean sleepWithoutBed);

    /**
     * sets a sleeping NPC awake on the same location
     */
    void setAwake();

    /**
     * Sends a packet to update the tablist name (if specific players are chosen, this update is temporary and will not be saved permanently to NpcData)
     * @param name tablist name
     * @param players players which should receive the tablist name update (choose no players to send the update to all players and save it in PlayerNpcData & core-config)
     */
    void setTablistName(String name, Player... players);

    /**
     * Sends a add|remove packet for the NPC tablist name (if specific players are chosen, this setting is temporary and will not be saved permanently to NpcData)
     * @param visible if the NPC name should be visible on tab
     * @param players players which should receive the name add|remove (choose no players to send the update to all players and save this setting in PlayerNpcData & core-config)
     */
    void setVisibleOnTab(boolean visible, Player... players);

    /**
     * Sends emote message to make the npc do an specific emote
     * @param emoteId LabyMod Emote ID
     * @param players players which should receive the name add|remove (choose no players to send the update to all players and save this setting in PlayerNpcData & core-config)
     */
    void playLabymodEmote(int emoteId, Player... players);

    void playMotionCapture(final String name);

    void playMotionCapture(final MotionCaptureData data);

    void sneak(boolean isSneaking);

    void block(final boolean block);

    void setItemInHand(final ItemStack item);

}
