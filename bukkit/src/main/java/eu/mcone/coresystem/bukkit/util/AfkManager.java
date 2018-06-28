/*
 * Copyright (c) 2017 - 2018 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.coresystem.bukkit.util;

import eu.mcone.coresystem.api.bukkit.CoreSystem;
import eu.mcone.coresystem.api.core.player.PlayerStatus;
import eu.mcone.coresystem.bukkit.BukkitCoreSystem;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.*;

public class AfkManager {

    private Map<UUID, Location> locations;
    private Map<UUID, Long> players;
    private List<UUID> afkPlayers;

    public AfkManager() {
        locations = new HashMap<>();
        players = new HashMap<>();
        afkPlayers = new ArrayList<>();

        Bukkit.getScheduler().runTaskTimerAsynchronously(CoreSystem.getInstance(), this::check, 25, 15);
    }

    public boolean isAfk(UUID uuid) {
        return afkPlayers.contains(uuid);
    }

    public long getAfkTime(UUID uuid) {
        return players.getOrDefault(uuid, 0L);
    }

    private void check() {
        Collection<? extends Player> online = Bukkit.getOnlinePlayers();

        Bukkit.getScheduler().runTaskAsynchronously(BukkitCoreSystem.getInstance(), () -> {
            for (Player p : online) {
                if (locations.getOrDefault(p.getUniqueId(), new Location(p.getWorld(), 0, 0, 0)).equals(p.getLocation())) {
                    players.put(p.getUniqueId(), players.getOrDefault(p.getUniqueId(), 0L) + 1);
                } else {
                    players.put(p.getUniqueId(), 0L);
                }

                long i = players.get(p.getUniqueId());
                if (afkPlayers.contains(p.getUniqueId())) {
                    if (i<150) {
                        afkPlayers.remove(p.getUniqueId());
                        BukkitCoreSystem.getInstance().getMessager().send(p, "§2Du bist nun nicht mehr AFK!");
                        BukkitCoreSystem.getInstance().getCorePlayer(p).setStatus(PlayerStatus.ONLINE);
                    }
                } else {
                    if (i>150) {
                        afkPlayers.add(p.getUniqueId());
                        BukkitCoreSystem.getInstance().getMessager().send(p, "§2Du bist nun AFK!");
                        BukkitCoreSystem.getInstance().getCorePlayer(p).setStatus(PlayerStatus.AFK);
                    }
                }

                locations.put(p.getUniqueId(), p.getLocation());
            }
        });
    }

    public void unregisterPlayer(UUID uuid) {
        locations.remove(uuid);
        players.remove(uuid);
        afkPlayers.remove(uuid);
    }

    public void stop() {
        for (HashMap.Entry<UUID, Long> templateEntry : players.entrySet()) {
            players.put(templateEntry.getKey(), 0L);
        }
    }

}
