/*
 * Copyright (c) 2017 - 2020 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.bukkit.util;

import eu.mcone.coresystem.api.bukkit.spawnable.PlayerListModeToggleable;
import eu.mcone.coresystem.api.bukkit.spawnable.ListMode;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.*;

public abstract class PlayerListModeToggleUtil implements PlayerListModeToggleable {

    @Getter
    protected ListMode listMode = ListMode.BLACKLIST;
    @Getter
    protected Set<Player> visiblePlayersList = new HashSet<>();

    @Override
    public void togglePlayerVisibility(ListMode listMode, Player... players) {
        Set<Player> listed = new HashSet<>(Arrays.asList(players));
        Map<Player, Boolean> setMap = new HashMap<>();

        if (listMode.equals(ListMode.WHITELIST)) {
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (listed.contains(player) && !visiblePlayersList.contains(player)) {
                    setMap.put(player, true);
                    visiblePlayersList.add(player);
                } else if (!listed.contains(player) && visiblePlayersList.contains(player)) {
                    setMap.put(player, false);
                    visiblePlayersList.remove(player);
                }
            }

            visiblePlayersList = new HashSet<>(listed);
        } else if (listMode.equals(ListMode.BLACKLIST)) {
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (listed.contains(player) && visiblePlayersList.contains(player)) {
                    setMap.put(player, false);
                    visiblePlayersList.remove(player);
                } else if (!listed.contains(player) && !visiblePlayersList.contains(player)) {
                    setMap.put(player, true);
                    visiblePlayersList.add(player);
                }
            }

            visiblePlayersList = new HashSet<>(Bukkit.getOnlinePlayers());
            for (Player p : listed) {
                visiblePlayersList.remove(p);
            }
        }

        this.listMode = listMode;
        for (Map.Entry<Player, Boolean> entry : setMap.entrySet()) {
            if (entry.getValue()) { ;
                spawn(entry.getKey());
            } else {
                despawn(entry.getKey());
            }
        }
    }

    public abstract void spawn(Player p);

    public abstract void despawn(Player p);

    public void playerJoined(Player... players) {
        if (listMode.equals(ListMode.BLACKLIST)) {
            visiblePlayersList.addAll(Arrays.asList(players));
        }
    }

    public void playerLeaved(Player... players) {
        visiblePlayersList.removeAll(Arrays.asList(players));
    }

    @Override
    public void toggleVisibility(Player player, boolean canSee) {
        if (canSee && !visiblePlayersList.contains(player)) {
            spawn(player);
            visiblePlayersList.add(player);
        } else if (!canSee && visiblePlayersList.contains(player)) {
            despawn(player);
            visiblePlayersList.remove(player);
        }
    }

    public boolean isVisibleFor(Player player) {
        return visiblePlayersList.contains(player);
    }

}
