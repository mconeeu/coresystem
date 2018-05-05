/*
 * Copyright (c) 2017 - 2018 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.coresystem.api.bukkit;

import eu.mcone.coresystem.api.bukkit.channel.ChannelHandler;
import eu.mcone.coresystem.api.bukkit.command.CoreCommand;
import eu.mcone.coresystem.api.bukkit.hologram.Hologram;
import eu.mcone.coresystem.api.bukkit.hologram.HologramManager;
import eu.mcone.coresystem.api.bukkit.inventory.CoreInventory;
import eu.mcone.coresystem.api.bukkit.npc.NPC;
import eu.mcone.coresystem.api.bukkit.npc.NpcManager;
import eu.mcone.coresystem.api.bukkit.player.BukkitCorePlayer;
import eu.mcone.coresystem.api.bukkit.player.NickManager;
import eu.mcone.coresystem.api.bukkit.stats.StatsAPI;
import eu.mcone.coresystem.api.bukkit.world.BuildSystem;
import eu.mcone.coresystem.api.bukkit.world.LocationManager;
import eu.mcone.coresystem.api.bukkit.world.WorldManager;
import eu.mcone.coresystem.api.core.GlobalCoreSystem;
import eu.mcone.coresystem.api.core.gamemode.Gamemode;
import eu.mcone.coresystem.api.core.player.SkinInfo;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.World;
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

    public abstract NickManager getNickManager();

    public abstract WorldManager getWorldManager();

    public abstract ChannelHandler getChannelHandler();

    public abstract boolean isCloudsystemAvailable();

    public abstract BukkitCorePlayer getCorePlayer(Player p);

    public abstract BukkitCorePlayer getCorePlayer(UUID uuid);

    public abstract BukkitCorePlayer getCorePlayer(String name);

    public abstract Collection<BukkitCorePlayer> getOnlineCorePlayers();

    public abstract NPC constructNPC(String name, Location location, String texture, String displayname);

    public abstract NPC constructNPC(String name, Location location, SkinInfo skinInfo, String displayname);

    public abstract Hologram constructHologram(String[] text, Location location);

    public abstract boolean uploadWorld(World w);

    public abstract NpcManager initialiseNpcManager(String server);

    public abstract HologramManager inititaliseHologramManager(String server);

    public abstract BuildSystem initialiseBuildSystem(boolean notify, BuildSystem.BuildEvent... events);

    public abstract LocationManager initialiseLocationManager(String server);

    public abstract void registerInventory(CoreInventory inventory);

    public abstract Collection<CoreInventory> getInventories();

    public abstract void clearPlayerInventories(UUID uuid);

    public abstract StatsAPI getStatsAPI(Gamemode gamemode);

    public abstract void registerCommand(CoreCommand command);

    public abstract CoreCommand getCoreCommand(String name);

}
