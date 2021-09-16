/*
 * Copyright (c) 2017 - 2020 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.bukkit.listener;

import eu.mcone.coresystem.api.bukkit.event.npc.NpcInteractEvent;
import eu.mcone.coresystem.api.bukkit.event.player.CorePlayerLoadedEvent;
import eu.mcone.coresystem.api.bukkit.event.world.CoreWorldLoadEvent;
import eu.mcone.coresystem.api.bukkit.npc.NPC;
import eu.mcone.coresystem.api.bukkit.npc.NpcData;
import eu.mcone.coresystem.api.bukkit.util.PacketListener;
import eu.mcone.coresystem.api.bukkit.util.ReflectionManager;
import eu.mcone.coresystem.api.bukkit.world.CoreWorld;
import eu.mcone.coresystem.bukkit.BukkitCoreSystem;
import eu.mcone.coresystem.bukkit.npc.CoreNPC;
import eu.mcone.coresystem.bukkit.npc.CoreNpcManager;
import eu.mcone.coresystem.bukkit.world.BukkitCoreWorld;
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
import org.bukkit.event.world.WorldUnloadEvent;

import java.util.concurrent.atomic.AtomicInteger;

@RequiredArgsConstructor
public class NpcListener implements Listener, PacketListener {

    private final BukkitCoreSystem system;
    private final CoreNpcManager manager;

    @EventHandler
    public void onEnable(PluginEnableEvent e) {
        for (CoreNPC<?, ?> npc : manager.getNpcSet()) {
            npc.playerJoined(Bukkit.getOnlinePlayers().toArray(new Player[0]));
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onCorePlayerLoaded(CorePlayerLoadedEvent e) {
        Player p = e.getBukkitPlayer();

        Bukkit.getScheduler().runTask(system, () -> {
            for (CoreNPC<?, ?> npc : manager.getNpcSet()) {
                npc.playerJoined(p);
            }
        });
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onQuit(PlayerQuitEvent e) {
        for (CoreNPC<?, ?> npc : manager.getNpcSet()) {
            npc.playerLeaved(e.getPlayer());
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onTeleport(PlayerTeleportEvent e) {
        Player p = e.getPlayer();

        for (CoreNPC<?, ?> npc : manager.getNpcSet()) {
            if (!npc.canBeSeenBy(p) || !npc.isVisibleFor(p)) {
                npc.despawn(p);
            } else if (npc.canBeSeenBy(p) && npc.isVisibleFor(p)) {
                Bukkit.getScheduler().runTaskLaterAsynchronously(system, () -> npc.spawn(p), 1L);
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onMove(PlayerMoveEvent e) {
        Player p = e.getPlayer();

        Bukkit.getScheduler().runTaskAsynchronously(system, () -> {
            if (!checkLocation(e.getFrom(), e.getTo())) {
                for (CoreNPC<?, ?> npc : manager.getNpcSet()) {
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
    public void onRespawn(PlayerRespawnEvent e) {
        for (CoreNPC<?, ?> npc : manager.getNpcSet()) {
            npc.despawn(e.getPlayer());
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onChangdeWorld(PlayerChangedWorldEvent e) {
        Player p = e.getPlayer();

        for (CoreNPC<?, ?> npc : manager.getNpcSet()) {
            if (!npc.getData().getLocation().getWorld().equals(p.getWorld().getName())) {
                npc.despawn(p);
            }
        }
    }

    @EventHandler
    public void onWorldLoad(CoreWorldLoadEvent e) {
        CoreWorld w = e.getWorld();

        int loaded = 0;
        for (NpcData data : ((BukkitCoreWorld) w).getNpcData()) {
            manager.addNPC(data);
            loaded++;
        }

        if (loaded > 0) {
            system.sendConsoleMessage("§2Loaded "+loaded+" NPCs");
        }
    }

    @EventHandler
    public void onWorldUnload(WorldUnloadEvent e) {
        AtomicInteger unloaded = new AtomicInteger();
        manager.getNpcSet().removeIf(npc -> {
            unloaded.incrementAndGet();
            return npc.getLocation().getWorld().equals(e.getWorld());
        });

        if (unloaded.get() > 0) {
            system.sendConsoleMessage("§2Unloaded "+unloaded.get()+" NPCs");
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
                                system,
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
