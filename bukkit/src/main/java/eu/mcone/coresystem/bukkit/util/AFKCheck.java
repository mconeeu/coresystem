/*
 * Copyright (c) 2017 - 2018 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.coresystem.bukkit.util;

import eu.mcone.coresystem.api.bukkit.util.Messager;
import eu.mcone.coresystem.bukkit.BukkitCoreSystem;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.UUID;

public class AFKCheck {

    private static HashMap<UUID, Location> locations = new HashMap<>();
    public static HashMap<UUID, Integer> players = new HashMap<>();
    public static ArrayList<UUID> afkPlayers = new ArrayList<>();

    public static void check() {
        Collection<? extends Player> online = Bukkit.getOnlinePlayers();

        Bukkit.getScheduler().runTaskAsynchronously(BukkitCoreSystem.getInstance(), () -> {
            for (Player p : online) {
                if (locations.getOrDefault(p.getUniqueId(), new Location(p.getWorld(), 0, 0, 0)).equals(p.getLocation())) {
                    players.put(p.getUniqueId(), players.getOrDefault(p.getUniqueId(), 0) + 1);
                } else {
                    players.put(p.getUniqueId(), 0);
                }

                int i = players.get(p.getUniqueId());
                if (afkPlayers.contains(p.getUniqueId())) {
                    if (i<150) {
                        afkPlayers.remove(p.getUniqueId());
                        Messager.send(p, "§2Du bist nun nicht mehr AFK!");
                        BukkitCoreSystem.getInstance().getCorePlayer(p).setStatus("online");
                    }
                } else {
                    if (i>150) {
                        afkPlayers.add(p.getUniqueId());
                        Messager.send(p, "§2Du bist nun AFK!");
                        BukkitCoreSystem.getInstance().getCorePlayer(p).setStatus("afk");
                    }
                }

                locations.put(p.getUniqueId(), p.getLocation());
            }
        });
    }
}
