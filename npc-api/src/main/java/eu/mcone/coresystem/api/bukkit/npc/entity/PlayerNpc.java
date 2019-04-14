/*
 * Copyright (c) 2017 - 2019 Dominik Lippl, Rufus Maiwald, Felix Schmid and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.api.bukkit.npc.entity;

import eu.mcone.coresystem.api.bukkit.npc.NPC;
import eu.mcone.coresystem.api.bukkit.npc.enums.EquipmentPosition;
import eu.mcone.coresystem.api.core.player.SkinInfo;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public interface PlayerNpc extends NPC {

    void setEquipment(EquipmentPosition position, ItemStack item, Player... players);

    void setSkin(SkinInfo skin, Player... players);

    SkinInfo getSkin();

    void setSleeping(boolean sleepWithoutBed);

    void setAwake();

    void setTablistName(String name, Player... players);

    void setVisibleOnTab(boolean visible, Player... players);
}
