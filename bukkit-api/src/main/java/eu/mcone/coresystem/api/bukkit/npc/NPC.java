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

    List<UUID> getLoadedPlayers();

    Location getLocation();

    SkinInfo getSkin();

    EntityPlayer getEntity();

    UUID getUuid();

    String getName();

    String getDisplayname();

    void set(Player player);

    void setSkin(SkinInfo skin, Player player);

    void setName(String displayname, Player player);

    void unset(Player player);

    void unsetAll();

    void destroy();

}
