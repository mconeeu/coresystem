/*
 * Copyright (c) 2017 - 2019 Dominik Lippl, Rufus Maiwald, Felix Schmid and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.api.bukkit.npc;

import eu.mcone.coresystem.api.bukkit.npc.data.EntityNpcData;
import eu.mcone.coresystem.api.bukkit.npc.enums.NpcAnimation;
import eu.mcone.coresystem.api.bukkit.npc.enums.NpcState;
import eu.mcone.coresystem.api.bukkit.npc.enums.NpcVisibilityMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.Collection;

public interface NPC {

    NpcData getData();

    <T extends EntityNpcData> T getEntityData();

    int getEntityId();

    void spawn(Player p);

    void despawn(Player p);

    void changeDisplayname(String displayname, Player... players);

    void update(NpcData data);

    Collection<Player> getVisiblePlayersList();

    void toggleNpcVisibility(NpcVisibilityMode visibility, Player... players);

    void toggleVisibility(Player player, boolean canSee);

    boolean isVisibleFor(Player player);

    boolean canBeSeenBy(Player player);

    void sendState(NpcState state, Player... players);

    void sendAnimation(NpcAnimation animation, Player... players);

    void teleport(Location location, Player... player);

    void teleport(CoreLocation loc, Player... player);
}
