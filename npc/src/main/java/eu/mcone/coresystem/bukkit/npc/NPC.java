/*
 * Copyright (c) 2017 - 2018 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.coresystem.bukkit.npc;

import eu.mcone.coresystem.bukkit.api.npc.enums.NpcAnimation;
import eu.mcone.coresystem.bukkit.api.npc.enums.NpcState;
import eu.mcone.coresystem.bukkit.api.npc.enums.NpcVisibilityMode;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class NPC {

    @Getter
    protected String name, displayname;
    @Getter
    private NpcVisibilityMode visibilityMode;
    @Getter
    private List<Player> visibilityPlayers;

    protected NPC(String name, String displayname, NpcVisibilityMode visibilityMode, Player... players) {
        this.name = name;
        this.displayname = displayname;
        this.visibilityMode = visibilityMode;

        toggleNpcVisibility(visibilityMode, players);
    }

    protected abstract void set(Player... players);

    protected abstract void unset(Player... players);

    public void toggleNpcVisibility(NpcVisibilityMode visibility, Player... players) {
        List<Player> uuidList = new ArrayList<>(Arrays.asList(players));
        List<Player> doSet = new ArrayList<>();
        List<Player> doUnset = new ArrayList<>();

        if (visibility.equals(NpcVisibilityMode.WHITELIST)) {
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (uuidList.contains(player) && !visibilityPlayers.contains(player)) {
                    doSet.add(player);
                    visibilityPlayers.add(player);
                } else if (visibilityPlayers.contains(player)) {
                    doUnset.add(player);
                    visibilityPlayers.remove(player);
                }
            }
        } else if (visibility.equals(NpcVisibilityMode.BLACKLIST)) {
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (uuidList.contains(player) && visibilityPlayers.contains(player)) {
                    doUnset.add(player);
                    visibilityPlayers.remove(player);
                } else if (!visibilityPlayers.contains(player)) {
                    doSet.add(player);
                    visibilityPlayers.add(player);
                }
            }
        }

        set(doSet.toArray(new Player[0]));
        unset(doUnset.toArray(new Player[0]));
        visibilityPlayers = uuidList;
    }

    public void toggleVisibility(Player player, boolean canSee) {
        if (canSee && !visibilityPlayers.contains(player)) {
            set(player);
            visibilityPlayers.add(player);
        } else if (visibilityPlayers.contains(player)) {
            unset(player);
            visibilityPlayers.remove(player);
        }
    }

    public void sendState(NpcState state) {

    }

    public void sendAnimation(NpcAnimation animation) {

    }

    public Location getLocation() {
        return null;
    }

    public void teleport(Location location) {

    }

    public boolean canSee(Player player) {
        return false;
    }

    public int getEntityID() {
        return 0;
    }

    public Class<? extends Entity> getEntity() {
        return null;
    }

    public boolean isLocal() {
        return false;
    }

}
