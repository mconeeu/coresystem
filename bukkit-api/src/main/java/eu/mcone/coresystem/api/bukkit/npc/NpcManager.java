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

    void reload();

    NPC addNPC(NpcData data);

    NPC addNPC(NpcData data, NpcVisibilityMode mode, Player... players);

    void removeNPC(NPC npc);

    NPC getNPC(CoreWorld world, String name);

    NPC getNPC(int entityId);

    Collection<NPC> getNpcs();

}
