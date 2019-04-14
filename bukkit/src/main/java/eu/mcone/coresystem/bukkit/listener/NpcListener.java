/*
 * Copyright (c) 2017 - 2019 Dominik Lippl, Rufus Maiwald, Felix Schmid and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.bukkit.listener;

import eu.mcone.coresystem.bukkit.BukkitCoreSystem;
import eu.mcone.coresystem.bukkit.npc.CoreNPC;
import eu.mcone.coresystem.bukkit.npc.CoreNpcManager;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.*;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.plugin.Plugin;

@RequiredArgsConstructor
public class NpcListener implements Listener {

    private final Plugin plugin;
    private final CoreNpcManager api;

    @EventHandler
    public void on(PluginEnableEvent e) {
        for (CoreNPC<?> npc : api.getNpcSet()) {
            npc.playerJoined(Bukkit.getOnlinePlayers().toArray(new Player[0]));
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void on(PlayerJoinEvent e) {
        Player p = e.getPlayer();

        for (CoreNPC<?> npc : api.getNpcSet()) {
            npc.despawn(p);
            npc.playerJoined(p);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void on(PlayerQuitEvent e) {
        for (CoreNPC<?> npc : api.getNpcSet()) {
            npc.playerLeaved(e.getPlayer());
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void on(PlayerTeleportEvent e) {
        Player p = e.getPlayer();

        for (CoreNPC<?> npc : api.getNpcSet()) {
            if (!npc.canBeSeenBy(p) || !npc.isVisibleFor(p)) {
                npc.despawn(p);
            } else if (npc.canBeSeenBy(p) && npc.isVisibleFor(p)) {
                Bukkit.getScheduler().runTaskLaterAsynchronously(BukkitCoreSystem.getSystem(), () -> npc.spawn(p), 1L);
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void on(PlayerMoveEvent e) {
        Player p = e.getPlayer();

        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            if (!checkLocation(e.getFrom(), e.getTo())) {
                for (CoreNPC<?> npc : api.getNpcSet()) {
                    if (!npc.canBeSeenBy(p) || !npc.isVisibleFor(p)) {
                        npc.despawn(p);
                    } else if (npc.canBeSeenBy(p) && npc.isVisibleFor(p)) {
                        npc.spawn(p);
                    }
                }
            }
        });
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void on(PlayerDeathEvent e) {
        for (CoreNPC<?> npc : api.getNpcSet()) {
            npc.despawn(e.getEntity());
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void on(PlayerChangedWorldEvent e) {
        Player p = e.getPlayer();

        for (CoreNPC<?> npc : api.getNpcSet()) {
            if (!npc.getData().getLocation().getWorld().equals(p.getWorld())) {
                npc.despawn(p);
            }
        }
    }

    private boolean checkLocation(Location loc1, Location loc2) {
        if (loc1 == null || loc2 == null) {
            return false;
        } else {
            return loc1.getBlockX() == loc2.getBlockX() && loc1.getBlockY() == loc2.getBlockY() && loc1.getBlockZ() == loc2.getBlockZ();
        }
    }

}
