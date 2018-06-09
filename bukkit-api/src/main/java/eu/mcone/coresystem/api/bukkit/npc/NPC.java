/*
 * Copyright (c) 2017 - 2018 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.coresystem.api.bukkit.npc;

import eu.mcone.coresystem.api.core.player.SkinInfo;
import net.minecraft.server.v1_8_R3.EntityPlayer;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;

public interface NPC {

    /**
     * returns list of all players that can see me
     * @return list of loaded players
     */
    List<UUID> getLoadedPlayers();

    /**
     * returns the NPCs current location
     * @return location
     */
    Location getLocation();

    /**
     * returns the NPCs predefined skin
     * @return skin
     */
    SkinInfo getSkin();

    /**
     * returns the EntityPlayer object of this NPC
     * @return EntityPlayer
     */
    EntityPlayer getEntity();

    /**
     * returns the NPCs player UUID
     * @return UUID
     */
    UUID getUuid();

    /**
     * returns the NPCs data name
     * @return data name
     */
    String getName();

    /**
     * returns the NPCs display name
     * @return displayname
     */
    String getDisplayname();

    /**
     * returns if the NPC is only locally stored or saved in Database
     * @return loccaly stored
     */
    boolean isLocal();

    /**
     * sets the NPC a specific player
     * @param player player
     */
    void set(Player player);

    /**
     * sets a specific player a new skin for this NPC
     * @param skin BCS SkinInfo
     * @param player player
     */
    void setSkin(SkinInfo skin, Player player);

    /**
     * sets a specific player a new name for this NPC
     * @param displayname displayname
     * @param player player
     */
    void setName(String displayname, Player player);

    /**
     * hides this NPC from the specific player
     * @param player player
     */
    void unset(Player player);

    /**
     * hides the NPC from all players
     */
    void unsetAll();

    /**
     * hides the NPC from all players and destroys it
     */
    void destroy();

}
