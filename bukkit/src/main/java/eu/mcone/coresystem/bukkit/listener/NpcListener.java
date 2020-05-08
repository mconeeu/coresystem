/*
 * Copyright (c) 2017 - 2019 Dominik Lippl, Rufus Maiwald, Felix Schmid and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.bukkit.listener;

import eu.mcone.coresystem.api.bukkit.event.npc.NpcInteractEvent;
import eu.mcone.coresystem.api.bukkit.npc.NPC;
import eu.mcone.coresystem.api.bukkit.util.PacketListener;
import eu.mcone.coresystem.api.bukkit.util.ReflectionManager;
import eu.mcone.coresystem.bukkit.BukkitCoreSystem;
import eu.mcone.coresystem.bukkit.npc.CoreNPC;
import eu.mcone.coresystem.bukkit.npc.CoreNpcManager;
import lombok.RequiredArgsConstructor;
import net.minecraft.server.v1_8_R3.Packet;
import net.minecraft.server.v1_8_R3.PacketPlayInUseEntity;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.*;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.plugin.Plugin;

@RequiredArgsConstructor
public class NpcListener implements Listener, PacketListener {

    private final Plugin plugin;
    private final CoreNpcManager api;

    @EventHandler
    public void on(PluginEnableEvent e) {
        for (CoreNPC<?, ?> npc : api.getNpcSet()) {
            npc.playerJoined(Bukkit.getOnlinePlayers().toArray(new Player[0]));
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void on(PlayerJoinEvent e) {
        Player p = e.getPlayer();

        for (CoreNPC<?, ?> npc : api.getNpcSet()) {
            npc.despawn(p);
            npc.playerJoined(p);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void on(PlayerQuitEvent e) {
        for (CoreNPC<?, ?> npc : api.getNpcSet()) {
            npc.playerLeaved(e.getPlayer());
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void on(PlayerTeleportEvent e) {
        Player p = e.getPlayer();

        for (CoreNPC<?, ?> npc : api.getNpcSet()) {
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
                for (CoreNPC<?, ?> npc : api.getNpcSet()) {
                    if (!npc.canBeSeenBy(p) || !npc.isVisibleFor(p)) {
                        npc.despawn(p);
                    } else if (npc.canBeSeenBy(p) && npc.isVisibleFor(p)) {
                        npc.spawn(p);
                    }
                }
            }
        });
    }

//    @EventHandler(priority = EventPriority.MONITOR)
//    public void on(PlayerDeathEvent e) {
//
//    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void on(PlayerRespawnEvent e) {
        for (CoreNPC<?, ?> npc : api.getNpcSet()) {
            npc.despawn(e.getPlayer());
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void on(PlayerChangedWorldEvent e) {
        Player p = e.getPlayer();

        for (CoreNPC<?, ?> npc : api.getNpcSet()) {
            if (!npc.getData().getLocation().getWorld().equals(p.getWorld().getName())) {
                npc.despawn(p);
            }
        }
    }

    @Override
    public void onPacketIn(Player player, Packet<?> packetObject) {
        if (packetObject instanceof PacketPlayInUseEntity) {
            PacketPlayInUseEntity packet = (PacketPlayInUseEntity) packetObject;

            if (packet.a().equals(PacketPlayInUseEntity.EnumEntityUseAction.ATTACK) || packet.a().equals(PacketPlayInUseEntity.EnumEntityUseAction.INTERACT)) {
                try {
                    NPC npc = BukkitCoreSystem.getSystem().getNpcManager().getNPC((int) ReflectionManager.getValue(packet, "a"));

                    if (npc != null) {
                        Bukkit.getScheduler().runTask(
                                BukkitCoreSystem.getSystem(),
                                () -> Bukkit.getPluginManager().callEvent(new NpcInteractEvent(player, npc, packet.a()))
                        );
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void onPacketOut(Player player, Packet<?> packet) {}

    private boolean checkLocation(Location loc1, Location loc2) {
        if (loc1 == null || loc2 == null) {
            return false;
        } else {
            return loc1.getBlockX() == loc2.getBlockX() && loc1.getBlockY() == loc2.getBlockY() && loc1.getBlockZ() == loc2.getBlockZ();
        }
    }

}
