/*
 * Copyright (c) 2017 - 2019 Dominik Lippl, Rufus Maiwald, Felix Schmid and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.api.bukkit;

import eu.mcone.coresystem.api.bukkit.channel.ChannelHandler;
import eu.mcone.coresystem.api.bukkit.inventory.ProfileInventoryModifier;
import eu.mcone.coresystem.api.bukkit.inventory.anvil.AnvilClickEventHandler;
import eu.mcone.coresystem.api.bukkit.inventory.anvil.CoreAnvilInventory;
import eu.mcone.coresystem.api.bukkit.npc.NpcManager;
import eu.mcone.coresystem.api.bukkit.player.AfkManager;
import eu.mcone.coresystem.api.bukkit.player.CorePlayer;
import eu.mcone.coresystem.api.bukkit.player.NickManager;
import eu.mcone.coresystem.api.bukkit.player.OfflineCorePlayer;
import eu.mcone.coresystem.api.bukkit.player.profile.interfaces.EnderchestManagerGetter;
import eu.mcone.coresystem.api.bukkit.player.profile.interfaces.HomeManagerGetter;
import eu.mcone.coresystem.api.bukkit.util.CoreActionBar;
import eu.mcone.coresystem.api.bukkit.util.CorePluginManager;
import eu.mcone.coresystem.api.bukkit.util.CoreTablistInfo;
import eu.mcone.coresystem.api.bukkit.util.CoreTitle;
import eu.mcone.coresystem.api.bukkit.world.BuildSystem;
import eu.mcone.coresystem.api.bukkit.world.CoreWorld;
import eu.mcone.coresystem.api.bukkit.world.WorldManager;
import eu.mcone.coresystem.api.core.GlobalCoreSystem;
import eu.mcone.coresystem.api.core.exception.PlayerNotResolvedException;
import eu.mcone.coresystem.api.core.labymod.LabyModAPI;
import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Collection;
import java.util.UUID;

public abstract class CoreSystem extends CorePlugin implements GlobalCoreSystem {

    @Getter
    private static CoreSystem instance;

    protected CoreSystem() {
        super("bukkitsystem", ChatColor.WHITE, "system.prefix.server");
    }

    protected void setInstance(CoreSystem instance) {
        if (instance == null) {
            System.err.println("BukkitCoreSystem instance cannot be set twice!");
        } else {
            CoreSystem.instance = instance;
        }
    }

    /**
     * returns the BCS PluginManager
     * @return CorePluginManager instance
     */
    public abstract CorePluginManager getPluginManager();

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
     * returns the BCS NpcManager
     * @return NpcManager instance
     */
    public abstract NpcManager getNpcManager();

    /**
     * returns the BCS HologramManager
     * @return HologramManager instance
     */
    public abstract eu.mcone.coresystem.api.bukkit.hologram.HologramManager getHologramManager();

    /**
     * returns the BCS AfkManager
     * @return AfkManager instance
     */
    public abstract AfkManager getAfkManager();

    /**
     * returns the bukkit Labymod API
     * @return bukkit Labymod API instance
     */
    public abstract LabyModAPI<Player> getLabyModAPI();

    /**
     * returns the mc one plugin messaging channel handler
     * @return plugin messaging channel handler
     */
    public abstract ChannelHandler getChannelHandler();

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
    public abstract CorePlayer getCorePlayer(Player player);

    /**
     * returns the CorePlayer by UUID
     * @param uuid uuid
     * @return CorePlayer
     */
    public abstract CorePlayer getCorePlayer(UUID uuid);

    /**
     * returns the CorePlayer by name
     * @param name name
     * @return CorePlayer
     */
    public abstract CorePlayer getCorePlayer(String name);

    /**
     * returns all online CorePlayers
     * @return List of online CorePlayers
     */
    public abstract Collection<CorePlayer> getOnlineCorePlayers();

    /**
     * creates a new instance of BuildSystem
     * @param events events that get blocked
     * @return nwe BuildSystem instance
     */
    public abstract BuildSystem initialiseBuildSystem(BuildSystem.BuildEvent... events);

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

    /**
     * opens a predefined book for a player
     * @param player player
     * @param book book
     */
    public abstract void openBook(Player player, ItemStack book);

    /**
     * creates a new AnvilInventory
     * it can be set for more than one player using the open(Player) method
     * @param title Inventory title
     * @param handler ClickHandler
     * @return new AnvilInventory
     */
    public abstract CoreAnvilInventory createAnvilInventory(String title, AnvilClickEventHandler handler);

    /**
     * creates an CorePlayer object for an offline or online player
     * this object has limited abilities as it should be uses for a potentially offline player
     * @param name Player name
     * @return OfflineCorePlayer object
     * @throws PlayerNotResolvedException thrown if the wished player is not in the database
     */
    public abstract OfflineCorePlayer getOfflineCorePlayer(String name) throws PlayerNotResolvedException;

    /**
     * creates an CorePlayer object for an offline or online player
     * this object has limited abilities as it should be uses for a potentially offline player
     * @param uuid Player uuid
     * @return OfflineCorePlayer object
     * @throws PlayerNotResolvedException thrown if the wished player is not in the database
     */
    public abstract OfflineCorePlayer getOfflineCorePlayer(UUID uuid) throws PlayerNotResolvedException;

    /**
     * disables all Player chat formatting
     * @param disabled if chat should be disabled
     */
    public abstract void setPlayerChatEnabled(boolean disabled);

    /**
     * allows you to add items to the ProfileInventory
     * @param modifier ProfileInventoryModifier
     */
    public abstract void modifyProfileInventory(ProfileInventoryModifier modifier);

    /**
     * allows you to change the size of the ProfileInventory
     * @param enabled if custom enderchest (with variable size) should be enabled
     */
    public abstract void setCustomEnderchestEnabled(boolean enabled);

    /**
     * allows to set the cooldown time for how long players must wait to write their next message
     * use 0 for no cooldown
     * @param cooldown cooldown in seconds
     */
    public abstract void setPlayerChatCooldown(int cooldown);

    /**
     * enables an global /server spawn command
     * IMPORTANT: The location "spawn" must be set in the core-config.json of the given world!
     * otherwise the spawn command will throw an error to the player!
     * @param plugin the plugin for message prefix
     * @param world CoreWorld
     * @param cooldown cooldown in seconds where the must not move until he get teleported, use 0 for no cooldown
     */
    public abstract void enableSpawnCommand(CorePlugin plugin, CoreWorld world, int cooldown);

    /**
     * allows all players to use /home /sethome /delhome commands
     * allowed home amount can be set with "system.bukkit.home.<i>i</i>" permission where <i>i</i> stands for the maximum allowed homes
     * @param plugin the plugin for message prefix
     * @param cooldown cooldown in seconds where the must not move until he get teleported, use 0 for no cooldown
     */
    public abstract void enableHomeSystem(CorePlugin plugin, HomeManagerGetter apiGetter, int cooldown);


    public abstract void enableEnderchestSystem(EnderchestManagerGetter apiGetter);

    /**
     * allows all players to use the /tpa /tpaccept /tpdeny commands
     * @param plugin the plugin for message prefix
     * @param cooldown cooldown in seconds where the must not move until he get teleported, use 0 for no cooldown
     */
    public abstract void enableTpaSystem(CorePlugin plugin, int cooldown);

    /**
     * allows you to change the size of the ProfileInventory
     * @param inventorySize size of the ProfileInventory
     */
    public abstract void setProfileInventorySize(int inventorySize);

}
