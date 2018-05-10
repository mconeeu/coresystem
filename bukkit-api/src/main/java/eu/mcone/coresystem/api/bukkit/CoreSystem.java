/*
 * Copyright (c) 2017 - 2018 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.coresystem.api.bukkit;

import com.google.gson.Gson;
import eu.mcone.coresystem.api.bukkit.channel.ChannelHandler;
import eu.mcone.coresystem.api.bukkit.hologram.Hologram;
import eu.mcone.coresystem.api.bukkit.hologram.HologramManager;
import eu.mcone.coresystem.api.bukkit.inventory.CoreInventory;
import eu.mcone.coresystem.api.bukkit.npc.NPC;
import eu.mcone.coresystem.api.bukkit.npc.NpcManager;
import eu.mcone.coresystem.api.bukkit.player.BukkitCorePlayer;
import eu.mcone.coresystem.api.bukkit.player.NickManager;
import eu.mcone.coresystem.api.bukkit.player.StatsAPI;
import eu.mcone.coresystem.api.bukkit.util.CoreActionBar;
import eu.mcone.coresystem.api.bukkit.util.CoreTablistInfo;
import eu.mcone.coresystem.api.bukkit.util.CoreTitle;
import eu.mcone.coresystem.api.bukkit.world.BuildSystem;
import eu.mcone.coresystem.api.bukkit.world.LocationManager;
import eu.mcone.coresystem.api.bukkit.world.WorldManager;
import eu.mcone.coresystem.api.core.GlobalCoreSystem;
import eu.mcone.coresystem.api.core.gamemode.Gamemode;
import eu.mcone.coresystem.api.core.player.SkinInfo;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Collection;
import java.util.UUID;

public abstract class CoreSystem extends JavaPlugin implements GlobalCoreSystem {

    @Getter
    private static CoreSystem instance;

    public CoreSystem() {}

    public void setInstance(CoreSystem instance) {
        if (instance == null) {
            System.err.println("BukkitCoreSystem instance cannot be set twice!");
        } else {
            CoreSystem.instance = instance;
        }
    }

    /**
     * returns the BCS NickManager
     * @return NickManager instance
     */
    public abstract NickManager getNickManager();

    /**
     * returns the BCS WorldManager
     * @return WorldManager instance
     */
    public abstract WorldManager getWorldManager();

    /**
     * returns the mc one plugin messaging channel handler
     * @return plugin messaging channel handler
     */
    public abstract ChannelHandler getChannelHandler();

    /**
     * returns the CoreSystems instance of Gson. Use this for better performance
     * @return gson instance
     */
    public abstract Gson getGson();

    /**
     * returns if the mc one Cloudsystem is available
     * @return boolean cloudsystem available
     */
    public abstract boolean isCloudsystemAvailable();

    /**
     * returns the CorePlayer object by Bukkit Player
     * @param player player
     * @return CorePlayer
     */
    public abstract BukkitCorePlayer getCorePlayer(Player player);

    /**
     * returns the CorePlayer by UUID
     * @param uuid uuid
     * @return CorePlayer
     */
    public abstract BukkitCorePlayer getCorePlayer(UUID uuid);

    /**
     * returns the CorePlayer by name
     * @param name name
     * @return CorePlayer
     */
    public abstract BukkitCorePlayer getCorePlayer(String name);

    /**
     * returns all online CorePlayers
     * @return List of online CorePlayers
     */
    public abstract Collection<BukkitCorePlayer> getOnlineCorePlayers();

    /**
     * creates a new NPC
     * @param name data name
     * @param location location where the NPC should be visible
     * @param texture predefined database name of the skin texture
     * @param displayname displayname
     * @return new NPC
     */
    public abstract NPC createNPC(String name, Location location, String texture, String displayname);

    /**
     * creates a new NPC
     * @param name data name
     * @param location location where the NPC should be visible
     * @param skinInfo BCS SkinInfo object
     * @param displayname displayname
     * @return new NPC
     */
    public abstract NPC createNPC(String name, Location location, SkinInfo skinInfo, String displayname);

    /**
     * creates a new Hologram
     * @param text array of lines that the hologram should display
     * @param location location
     * @return new Hologram
     */
    public abstract Hologram createHologram(String[] text, Location location);

    /**
     * creates a new instance of NpcManager
     * @param server name of server/plugin for database management
     * @return new NpcManager instance
     */
    public abstract NpcManager initialiseNpcManager(String server);

    /**
     * creates a new instance od HologramManager
     * @param server name of server/plugin for database management
     * @return new HologramManager instance
     */
    public abstract HologramManager inititaliseHologramManager(String server);

    /**
     * creates a new instance of BuildSystem
     * @param notify should players get notified if they are not allowed to build?
     * @param events events that get blocked
     * @return nwe BuildSystem instance
     */
    public abstract BuildSystem initialiseBuildSystem(boolean notify, BuildSystem.BuildEvent... events);

    /**
     * creates a new LocationManager instance
     * @param server name of server/plugin for database management
     * @return new LocationManager instance
     */
    public abstract LocationManager initialiseLocationManager(String server);

    /**
     * registers a new CoreInventory (not necessary, extending the CoreInventory class will do this)
     * @param inventory CoreInventory
     */
    public abstract void registerInventory(CoreInventory inventory);

    /**
     * returns all current saved CoreInventories
     * @return list of all CoreInventories
     */
    public abstract Collection<CoreInventory> getInventories();

    /**
     * returns the StatsAPI for a specific Gamemode
     * @param gamemode gamemode
     * @return StatsAPI
     */
    public abstract StatsAPI getStatsAPI(Gamemode gamemode);

    /**
     * creates a new player title
     * @return new CoreTitle
     */
    public abstract CoreTitle createTitle();

    /**
     * create a new Tablist Header/Footer
     * @return new TablistInfo
     */
    public abstract CoreTablistInfo createTablistInfo();

    /**
     * creates a new ActionBar message
     * @return new ActionBar
     */
    public abstract CoreActionBar createActionBar();

}
