/*
 * Copyright (c) 2017 - 2018 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.coresystem.bukkit.api.npc.entity;

import eu.mcone.coresystem.api.bukkit.npc.NPC;
import eu.mcone.coresystem.api.core.player.SkinInfo;
import eu.mcone.coresystem.bukkit.api.npc.enums.EquipmentPosition;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public interface PlayerNpc extends NPC {

    void setEquipment(EquipmentPosition position, ItemStack item);

    void setSkin(SkinInfo skin, Player player);

    SkinInfo getSkin();

    void setSleeping(boolean sleepWithoutBed, Location bedLocation);

    void setTablistName();

    String getTablistName();

    void setVisibleOnTab(boolean visible);

    boolean isVisibleOnTab();

}
