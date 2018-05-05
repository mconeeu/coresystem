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

    void reload();

    void addNPC(String name, Location location, String texture, String displayname);

    void updateNPC(String name, Location location, String texture, String displayname);

    void removeNPC(String name);

    boolean isNPC(String playerName);

    void updateNPCs();

    void setNPCs(Player player);

    void unsetNPCs(Player player);

    void unsetNPCs();

    NPC getNPC(String name);

    Map<String, NPC> getNPCs();

}
