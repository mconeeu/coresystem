/*
 * Copyright (c) 2017 - 2020 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.bukkit.player;

import eu.mcone.coresystem.api.bukkit.CoreSystem;
import eu.mcone.coresystem.api.bukkit.event.player.AfkEvent;
import eu.mcone.coresystem.api.bukkit.facades.Msg;
import eu.mcone.coresystem.api.bukkit.player.AfkManager;
import eu.mcone.coresystem.api.core.exception.RuntimeCoreException;
import eu.mcone.coresystem.api.core.player.PlayerState;
import eu.mcone.coresystem.bukkit.BukkitCoreSystem;
import eu.mcone.coresystem.bukkit.listener.AfkListener;
import eu.mcone.coresystem.core.player.GlobalCorePlayer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;

public class CoreAfkManager implements AfkManager {

    private final Map<UUID, Location> locations;
    private final Map<UUID, Long> players;
    private final Set<UUID> afkPlayers;

    private BukkitTask task;

    public CoreAfkManager(BukkitCoreSystem system) {
        locations = new HashMap<>();
        players = new HashMap<>();
        afkPlayers = new HashSet<>();

        system.registerEvents(new AfkListener(this));

        start();
    }

    @Override
    public boolean isAfk(UUID uuid) {
        return afkPlayers.contains(uuid);
    }

    @Override
    public long getAfkTime(UUID uuid) {
        return players.getOrDefault(uuid, 0L);
    }

    private void check() {
        Collection<? extends Player> online = Bukkit.getOnlinePlayers();

        for (Player p : online) {
            if (locations.getOrDefault(p.getUniqueId(), new Location(p.getWorld(), 0, 0, 0)).equals(p.getLocation())) {
                players.put(p.getUniqueId(), players.getOrDefault(p.getUniqueId(), 0L) + 1);
            } else {
                players.put(p.getUniqueId(), 0L);
            }

            long i = players.get(p.getUniqueId());
            if (afkPlayers.contains(p.getUniqueId())) {
                if (i < 150) {
                    setAfk(p, false);
                }
            } else {
                if (i > 150) {
                    setAfk(p, true);
                }
            }

            locations.put(p.getUniqueId(), p.getLocation());
        }
    }

    public void setAfk(Player p, boolean afk) {
        PlayerState state = null;

        if (afk) {
            if (afkPlayers.add(p.getUniqueId())) {
                Msg.send(p, "??2Du bist nun AFK!");
                state = PlayerState.AFK;
            }
        } else {
            players.put(p.getUniqueId(), 0L);

            if (afkPlayers.remove(p.getUniqueId())) {
                Msg.send(p, "??2Du bist nun nicht mehr AFK!");
                state = PlayerState.ONLINE;
            }
        }

        if (state != null) {
            PlayerState changedState = state;

            ((GlobalCorePlayer) BukkitCoreSystem.getInstance().getCorePlayer(p)).setState(changedState);
            Bukkit.getScheduler().runTask(BukkitCoreSystem.getSystem(), () -> Bukkit.getPluginManager().callEvent(new AfkEvent(p, changedState)));
        }
    }

    void unregisterPlayer(UUID uuid) {
        locations.remove(uuid);
        players.remove(uuid);
        afkPlayers.remove(uuid);
    }

    @Override
    public void start() {
        if (task == null) {
            task = Bukkit.getScheduler().runTaskTimerAsynchronously(CoreSystem.getInstance(), this::check, 25, 15);
        } else {
            throw new RuntimeCoreException("Tried to start new AfkManager, but task is not null!");
        }
    }

    @Override
    public void disable() {
        task.cancel();
        task = null;

        locations.clear();
        players.clear();
        afkPlayers.clear();
    }

}
