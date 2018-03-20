/*
 * Copyright (c) 2017 - 2018 Dominik L., Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.coresystem.bukkit.npc;

import eu.mcone.coresystem.bukkit.CoreSystem;
import eu.mcone.coresystem.bukkit.command.NpcCMD;
import eu.mcone.coresystem.bukkit.npc.NPC;
import eu.mcone.coresystem.bukkit.util.LocationFactory;
import eu.mcone.coresystem.lib.mysql.MySQL;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Objects;

public class NpcManager implements Listener {

    private MySQL mysql;
    private String server;
    private HashMap<String, NPC> npcs;

    public NpcManager(MySQL mysql, String server) {
        this.mysql = mysql;
        this.server = server;

        CoreSystem.getInstance().getServer().getPluginManager().registerEvents(this, CoreSystem.getInstance());
        CoreSystem.getInstance().getCommand("npc").setExecutor(new NpcCMD(this));

        this.reload();
        Bukkit.getScheduler().scheduleSyncRepeatingTask(CoreSystem.getInstance(), () -> {
            for(NPC npc : npcs.values()){
                for(Player p : Bukkit.getOnlinePlayers()){
                    if(npc.getLocation().getWorld().equals(p.getWorld())){
                        if(npc.getLocation().distance(p.getLocation())>60 && npc.getLoadedPlayers().contains(p.getUniqueId())){
                            npc.unset(p);
                        }else if(npc.getLocation().distance(p.getLocation())<60 && !npc.getLoadedPlayers().contains(p.getUniqueId())){
                            npc.set(p);
                        }
                    }else{
                        npc.unset(p);
                    }
                }
            }
        },0,20);
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
        }

        this.npcs = new HashMap<>();
        this.mysql.select("SELECT * FROM bukkitsystem_npcs WHERE server='"+this.server+"'", rs -> {
            try {
                while (rs.next()) {
                    if (rs.getString("texture") != null) {
                        this.npcs.put(rs.getString("name"), new NPC(rs.getString("name"), Objects.requireNonNull(LocationFactory.getLocationfromJSON(rs.getString("location"))), rs.getString("texture"), rs.getString("displayname")));
                    } else {
                        System.err.println("NPC "+rs.getString("name")+" besitzt keine Textur!");
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    public void addNPC(String name, Location loc, String texture, String displayname) {
        NPC npc = new NPC(name, loc, texture, displayname);

        String json = LocationFactory.getJSONLocation(loc);
        this.mysql.update("INSERT INTO bukkitsystem_npcs (`name`, `location`, `displayname`, `texture`, `server`) VALUES ('"+name+"', '"+json+"', '"+displayname+"', '"+texture+"', '"+this.server+"') " +
                "ON DUPLICATE KEY UPDATE `location`='"+json+"'");
        this.npcs.put(name, npc);
        this.updateNPCs();
    }

    public void updateNPC(String name, Location loc, String texture, String displayname) {
        if (getNPCs().containsKey(name)) {
            getNPCs().get(name).unsetAll();
            NPC npc = new NPC(name, loc, texture, displayname);

            String json = LocationFactory.getJSONLocation(loc);
            this.mysql.update("UPDATE bukkitsystem_npcs SET `location`='"+json+"', `displayname`='"+displayname+"', `texture`='"+texture+"' WHERE `name`='"+name+"' AND `server`='"+this.server+"'");
            this.npcs.put(name, npc);
            this.updateNPCs();
        }
    }

    public void removeNPC(String name) {
        this.mysql.update("DELETE FROM bukkitsystem_npcs WHERE `name`='"+name+"' AND `server`='"+this.server+"'");
        if (this.npcs.containsKey(name)) {
            this.npcs.get(name).unsetAll();
            this.npcs.get(name).destroy();
            this.npcs.remove(name);
        }
    }

    public boolean isNPC(String playerName) {
        for (NPC npc : npcs.values()) {
            if (npc.getDisplayname().equalsIgnoreCase(playerName)) {
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

    public void setNPCs(Player p) {
        for (NPC npc : npcs.values()) {
            npc.set(p);
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

    public HashMap<String, NPC> getNPCs() {
        return npcs;
    }
}