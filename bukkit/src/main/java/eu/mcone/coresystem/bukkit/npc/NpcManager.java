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
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;

public class NpcManager implements Listener, eu.mcone.coresystem.api.bukkit.npc.NpcManager {

    private HashMap<String, NPC> npcs;

    public NpcManager(JavaPlugin plugin) {
        BukkitCoreSystem.getInstance().getServer().getPluginManager().registerEvents(this, BukkitCoreSystem.getInstance());
        BukkitCoreSystem.getInstance().getCommand("npc").setExecutor(new NpcCMD(this));

        this.reload();
        Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, () -> {
            for (NPC npc : npcs.values()) {
                for (Player p : Bukkit.getOnlinePlayers()) {
                    if (npc.getData().getLocation().bukkit().getWorld().equals(p.getWorld())) {
                        if (npc.getData().getLocation().bukkit().distance(p.getLocation()) > 60 && npc.getLoadedPlayers().contains(p.getUniqueId())) {
                            npc.unset(p);
                        } else if (npc.getData().getLocation().bukkit().distance(p.getLocation()) < 60 && !npc.getLoadedPlayers().contains(p.getUniqueId())) {
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
            this.npcs = new HashMap<>();
        }

        this.npcs.clear();
        for (CoreWorld w : CoreSystem.getInstance().getWorldManager().getWorlds()) {
            for (NpcData data : ((BukkitCoreWorld) w).getNpcs()) {
                if (data.getSkinName() != null) {
                    this.npcs.put(data.getName(), new NPC(data));
                } else {
                    throw new RuntimeCoreException("NPC " + data.getName() + " besitzt keine Textur!");
                }
            }
        }
    }

    @Override
    public void addLocalNPC(NpcData data) {
        NPC npc = new NPC(data);
        npc.setLocal(true);

        this.npcs.put(data.getName(), npc);
        this.updateNPCs();
    }

    @Override
    public void addNPC(NpcData data) {
        NPC npc = new NPC(data);

        this.npcs.put(data.getName(), npc);
        this.updateNPCs();

        BukkitCoreWorld w = (BukkitCoreWorld) CoreSystem.getInstance().getWorldManager().getWorld(data.getLocation().getWorldName());
        w.getNpcs().add(data);
        w.save();
    }

    public void updateNPC(String name, Location loc, String skinName, String displayname) {
        if (npcs.containsKey(name)) {
            NpcData data = new NpcData(name, displayname, skinName, new CoreLocation(loc));

            NPC npc = new NPC(data);
            npc.setLocal(npcs.get(name).isLocal());

            if (!npc.isLocal()) {
                BukkitCoreWorld w = (BukkitCoreWorld) CoreSystem.getInstance().getWorldManager().getWorld(data.getLocation().getWorldName());
                w.getNpcs().remove(npcs.get(name).getData());
                w.getNpcs().add(data);
                w.save();
            }

            this.npcs.put(name, npc);
            this.updateNPCs();
        }
    }

    public void removeNPC(String name) {
        NPC npc = npcs.get(name);

        BukkitCoreWorld w = (BukkitCoreWorld) CoreSystem.getInstance().getWorldManager().getWorld(npc.getData().getLocation().getWorldName());
        w.getNpcs().remove(npc.getData());

        if (this.npcs.containsKey(name)) {
            npc.unsetAll();
            npc.destroy();
            npcs.remove(name);
        }
    }

    public boolean isNPC(String playerName) {
        for (NPC npc : npcs.values()) {
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
        for (NPC npc : this.npcs.values()) {
            npc.unset(p);
        }
    }

    public void unsetNPCs() {
        for (Player p : Bukkit.getOnlinePlayers()) {
            unsetNPCs(p);
        }
    }

    public NPC getNPC(String name) {
        for (NPC npc : npcs.values()) {
            if (npc.getData().getName().equals(name)) {
                return npc;
            }
        }
        return null;
    }

    public Map<String, eu.mcone.coresystem.api.bukkit.npc.NPC> getNPCs() {
        Map<String, eu.mcone.coresystem.api.bukkit.npc.NPC> result = new HashMap<>();
        for (HashMap.Entry<String, NPC> e : npcs.entrySet()) {
            result.put(e.getKey(), e.getValue());
        }

        return result;
    }

    public void setNPCs(Player p) {
        for (NPC npc : npcs.values()) {
            npc.set(p);
        }
    }
}