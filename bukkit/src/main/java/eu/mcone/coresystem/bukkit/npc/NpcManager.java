/*
 * Copyright (c) 2017 - 2018 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.coresystem.bukkit.npc;

import eu.mcone.coresystem.api.bukkit.CoreSystem;
import eu.mcone.coresystem.api.bukkit.npc.NpcData;
import eu.mcone.coresystem.api.bukkit.world.CoreLocation;
import eu.mcone.coresystem.api.bukkit.world.CoreWorld;
import eu.mcone.coresystem.api.core.exception.RuntimeCoreException;
import eu.mcone.coresystem.bukkit.BukkitCoreSystem;
import eu.mcone.coresystem.bukkit.command.NpcCMD;
import eu.mcone.coresystem.bukkit.world.BukkitCoreWorld;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.ArrayList;
import java.util.List;

public class NpcManager implements Listener, eu.mcone.coresystem.api.bukkit.npc.NpcManager {

    private List<NPC> npcs;

    public NpcManager(BukkitCoreSystem instance) {
        BukkitCoreSystem.getInstance().getServer().getPluginManager().registerEvents(this, BukkitCoreSystem.getInstance());
        instance.getPluginManager().registerCoreCommand(new NpcCMD(this), CoreSystem.getInstance());

        this.reload();
        Bukkit.getScheduler().runTaskTimerAsynchronously(instance, () -> {
            for (NPC npc : npcs) {
                for (Player p : Bukkit.getOnlinePlayers()) {
                    if (npc.getWorld().bukkit().equals(p.getWorld())) {
                        if (npc.getData().getLocation().bukkit(npc.getWorld()).distance(p.getLocation()) > 60 && npc.getLoadedPlayers().contains(p.getUniqueId())) {
                            npc.unset(p);
                        } else if (npc.getData().getLocation().bukkit(npc.getWorld()).distance(p.getLocation()) < 60 && !npc.getLoadedPlayers().contains(p.getUniqueId())) {
                            npc.set(p);
                        }
                    } else if (npc.getLoadedPlayers().contains(p.getUniqueId())) {
                        npc.unset(p);
                    }
                }
            }
        }, 0, 20);
    }

    @EventHandler
    public void on(PlayerQuitEvent e) {
        unsetNPCs(e.getPlayer());
    }

    public void reload() {
        if (this.npcs != null) {
            for (Player p : Bukkit.getOnlinePlayers()) {
                this.unsetNPCs(p);
            }
        } else {
            this.npcs = new ArrayList<>();
        }

        this.npcs.clear();
        for (CoreWorld w : CoreSystem.getInstance().getWorldManager().getWorlds()) {
            for (NpcData data : ((BukkitCoreWorld) w).getNpcData()) {
                if (data.getSkinName() != null) {
                    this.npcs.add(new NPC(w, data));
                } else {
                    throw new RuntimeCoreException("NPC " + data.getName() + " besitzt keine Textur!");
                }
            }
        }
    }

    @Override
    public List<eu.mcone.coresystem.api.bukkit.npc.NPC> getNPCs() {
        return new ArrayList<>(npcs);
    }

    @Override
    public void addLocalNPC(String name, String displayname, String skinName, Location location) {
        NpcData data = new NpcData(name, displayname, skinName, new CoreLocation(location));
        BukkitCoreWorld world = (BukkitCoreWorld) CoreSystem.getInstance().getWorldManager().getWorld(location.getWorld());

        NPC npc = new NPC(world, data);
        npc.setLocal(true);

        this.npcs.add(npc);
        this.updateNPCs();
    }

    @Override
    public void addNPC(String name, String displayname, String skinName, Location location) {
        NpcData data = new NpcData(name, displayname, skinName, new CoreLocation(location));
        BukkitCoreWorld world = (BukkitCoreWorld) CoreSystem.getInstance().getWorldManager().getWorld(location.getWorld());

        NPC npc = new NPC(world, data);

        this.npcs.add(npc);
        this.updateNPCs();

        world.getNpcData().add(data);
        world.save();
    }

    public void removeNPC(CoreWorld w, String name) {
        NPC npc = getNPC(w, name);

        if (npc != null) {
            removeNPC(npc);
        } else {
            throw new RuntimeCoreException("Tried to remove NPC "+name+" on world "+w.getName()+", but npcs list in NpcManager does not cointain it!");
        }
    }

    @Override
    public void removeNPC(eu.mcone.coresystem.api.bukkit.npc.NPC npc) {
        ((BukkitCoreWorld) npc.getWorld()).getNpcData().remove(npc.getData());

        npc.unsetAll();
        npc.destroy();
        npcs.remove(npc);
    }

    @Override
    public void updateNPC(eu.mcone.coresystem.api.bukkit.npc.NPC oldNpc, NpcData newData) {
        BukkitCoreWorld w = (BukkitCoreWorld) oldNpc.getWorld();

        NPC npc = new NPC(w, newData);
        npc.setLocal(oldNpc.isLocal());

        for (Player p : Bukkit.getOnlinePlayers()) {
            oldNpc.unset(p);
        }

        if (!oldNpc.isLocal()) {
            w.getNpcData().remove(oldNpc.getData());
            w.getNpcData().add(newData);
            w.save();
        }

        this.npcs.remove(oldNpc);
        this.npcs.add(npc);
        this.updateNPCs();
    }

    public void updateNPC(CoreWorld w, String name, Location loc, String skinName, String displayname) {
        NPC oldNPC = getNPC(w, name);

        if (oldNPC != null) {
            updateNPC(oldNPC, new NpcData(name, displayname, skinName, new CoreLocation(loc)));
        } else {
            throw new RuntimeCoreException("Tried to update NPC "+name+" on w "+w.getName()+", but npcs list in NpcManager does not cointain it!");
        }
    }

    public NPC getNPC(CoreWorld world, String name) {
        for (NPC npc : npcs) {
            if (npc.getWorld().equals(world) && npc.getData().getName().equals(name)) {
                return npc;
            }
        }
        return null;
    }

    public boolean isNPC(String playerName) {
        for (eu.mcone.coresystem.api.bukkit.npc.NPC npc : npcs) {
            if (npc.getData().getDisplayname().equalsIgnoreCase(playerName)) {
                return true;
            }
        }
        return false;
    }

    public void updateNPCs() {
        for (Player p : Bukkit.getOnlinePlayers()) {
            this.unsetNPCs(p);
            this.setNPCs(p);
        }
    }

    public void unsetNPCs(Player p) {
        for (NPC npc : this.npcs) {
            npc.unset(p);
        }
    }

    public void unsetNPCs() {
        for (Player p : Bukkit.getOnlinePlayers()) {
            unsetNPCs(p);
        }
    }

    public void setNPCs(Player p) {
        for (NPC npc : npcs) {
            npc.set(p);
        }
    }

    public void disable() {
        unsetNPCs();
        npcs.clear();
    }

}