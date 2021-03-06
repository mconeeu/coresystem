/*
 * Copyright (c) 2017 - 2020 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.api.bukkit;

import eu.mcone.coresystem.api.bukkit.channel.ChannelHandler;
import eu.mcone.coresystem.api.bukkit.channel.PacketManager;
import eu.mcone.coresystem.api.bukkit.chat.Messenger;
import eu.mcone.coresystem.api.bukkit.codec.CodecRegistry;
import eu.mcone.coresystem.api.bukkit.inventory.anvil.AnvilClickEventHandler;
import eu.mcone.coresystem.api.bukkit.inventory.anvil.CoreAnvilInventory;
import eu.mcone.coresystem.api.bukkit.npc.NpcManager;
import eu.mcone.coresystem.api.bukkit.npc.entity.EntityProjectile;
import eu.mcone.coresystem.api.bukkit.overwatch.Overwatch;
import eu.mcone.coresystem.api.bukkit.player.*;
import eu.mcone.coresystem.api.bukkit.player.profile.interfaces.EnderchestManagerGetter;
import eu.mcone.coresystem.api.bukkit.player.profile.interfaces.HomeManagerGetter;
import eu.mcone.coresystem.api.bukkit.sound.SoundManager;
import eu.mcone.coresystem.api.bukkit.stats.CoreStatsManager;
import eu.mcone.coresystem.api.bukkit.util.*;
import eu.mcone.coresystem.api.bukkit.vanish.VanishManager;
import eu.mcone.coresystem.api.bukkit.world.BuildSystem;
import eu.mcone.coresystem.api.bukkit.world.CoreWorld;
import eu.mcone.coresystem.api.bukkit.world.WorldManager;
import eu.mcone.coresystem.api.bukkit.world.schematic.SchematicManager;
import eu.mcone.coresystem.api.core.GlobalCoreSystem;
import eu.mcone.coresystem.api.core.exception.PlayerNotResolvedException;
import group.onegaming.networkmanager.core.api.random.UniqueIdUtil;
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
        super(
                "BukkitSystem",
                ChatColor.WHITE,
                "system.prefix.server",
                "https://2edb2eff2d724190bb5be92f90cbcbfd@o267551.ingest.sentry.io/5341153?stacktrace.app.packages=eu.mcone.coresystem"
        );
    }

    protected void setInstance(CoreSystem instance) {
        if (CoreSystem.instance == null) {
            CoreSystem.instance = instance;
        } else throw new IllegalStateException("Could not set CoreSystem instance. Instance already set!");
    }

    public abstract BukkitDebugger getDebugger();

    /**
     * returns the BCS PluginManager
     *
     * @return PluginManager instance
     */
    public abstract PluginManager getPluginManager();

    /**
     * returns the Translation manager System
     *
     * @return Translationmanager instance
     */
    public abstract TranslationManager getTranslationManager();

    /**
     * returns the Overwatch System
     *
     * @return Overwatch instance
     */
    public abstract Overwatch getOverwatch();

    /**
     * returns the CorePacketManager
     *
     * @return CorePacketManager instance
     */
    public abstract PacketManager getPacketManager();

    /**
     * creates a new codec registry object
     *
     * @param listening listening for codecs?
     * @return new CodecRegistry instance
     */
    public abstract CodecRegistry createCodecRegistry(boolean listening);

    /**
     * returns the BCS NickManager
     *
     * @return NickManager instance
     */
    public abstract NickManager getNickManager();

    /**
     * returns the BCS WorldManager
     *
     * @return WorldManager instance
     */
    public abstract WorldManager getWorldManager();

    /**
     * returns the BCS NpcManager
     *
     * @return NpcManager instance
     */
    public abstract NpcManager getNpcManager();

    /**
     * returns the BCS HologramManager
     *
     * @return HologramManager instance
     */
    public abstract eu.mcone.coresystem.api.bukkit.hologram.HologramManager getHologramManager();

    /**
     * returns the BCS AfkManager
     *
     * @return AfkManager instance
     */
    public abstract AfkManager getAfkManager();

    /**
     * returns the BCS VanishManager
     *
     * @return VanishManager instance
     */
    public abstract VanishManager getVanishManager();

    /**
     * returns the BCS SoundManager
     *
     * @return SoundManager instance
     */
    public abstract SoundManager getSoundManager();

    /**
     * returns the BCS UniqueIdUtil
     *
     * @return NetworkUniqueIdUtil instance
     */
    public abstract UniqueIdUtil getUniqueIdUtil();

    /**
     * returns the bukkit Labymod API
     *
     * @return bukkit Labymod API instance
     */
    public abstract LabyModBukkitAPI getLabyModAPI();

    /**
     * returns the core stats manager
     *
     * @return bukkit stats manager instance
     */
    public abstract CoreStatsManager getCoreStatsManager();

    /**
     * returns the mc one plugin messaging channel handler
     *
     * @return plugin messaging channel handler
     */
    public abstract ChannelHandler getChannelHandler();

    /**
     * returns if the mc one Cloudsystem is available
     *
     * @return boolean cloudsystem available
     */
    public abstract boolean isCloudsystemAvailable();

    /**
     * returns the CorePlayer object by Bukkit Player
     *
     * @param player player
     * @return CorePlayer
     */
    public abstract CorePlayer getCorePlayer(Player player);

    /**
     * returns the CorePlayer by UUID
     *
     * @param uuid uuid
     * @return CorePlayer
     */
    public abstract CorePlayer getCorePlayer(UUID uuid);

    /**
     * returns the CorePlayer by name
     *
     * @param name name
     * @return CorePlayer
     */
    public abstract CorePlayer getCorePlayer(String name);

    /**
     * returns all online CorePlayers
     *
     * @return List of online CorePlayers
     */
    public abstract Collection<CorePlayer> getOnlineCorePlayers();

    /**
     * creates a new instance of BuildSystem
     *
     * @param events events that get blocked
     * @return nwe BuildSystem instance
     */
    public abstract BuildSystem initialiseBuildSystem(BuildSystem.BuildEvent... events);

    /**
     * creates a new instance of BukkitMessenger
     *
     * @param prefixTranslation translation that will be used for the prefix
     * @return nwe BukkitMessenger instance
     */
    public abstract Messenger initializeMessenger(String prefixTranslation);

    /**
     * creates a new instance of SchematicManager
     *
     * @param cache activates the local cache
     * @return nwe SchematicManager instance
     */
    public abstract SchematicManager initializeSchematicManager(boolean cache);

    /**
     * creates a new player title
     *
     * @return new CoreTitle
     */
    public abstract CoreTitle createTitle();

    /**
     * create a new Tablist Header/Footer
     *
     * @return new TablistInfo
     */
    public abstract CoreTablistInfo createTablistInfo();

    /**
     * creates a new ActionBar message
     *
     * @return new ActionBar
     */
    public abstract CoreActionBar createActionBar();

    /**
     * creates a new Projectile
     *
     * @return new Projectile
     */
    public abstract CoreProjectile createProjectile(EntityProjectile type);

    /**
     * opens a predefined book for a player
     *
     * @param player player
     * @param book   book
     */
    public abstract void openBook(Player player, ItemStack book);

    /**
     * creates a new AnvilInventory
     * it can be set for more than one player using the open(Player) method
     *
     * @param handler ClickHandler
     * @return new AnvilInventory
     */
    public abstract CoreAnvilInventory createAnvilInventory(AnvilClickEventHandler handler);

    /**
     * creates an CorePlayer object for an offline or online player
     * this object has limited abilities as it should be uses for a potentially offline player
     *
     * @param name Player name
     * @return OfflineCorePlayer object
     * @throws PlayerNotResolvedException thrown if the wished player is not in the database
     */
    public abstract OfflineCorePlayer getOfflineCorePlayer(String name) throws PlayerNotResolvedException;

    /**
     * creates an CorePlayer object for an offline or online player
     * this object has limited abilities as it should be uses for a potentially offline player
     *
     * @param uuid Player uuid
     * @return OfflineCorePlayer object
     * @throws PlayerNotResolvedException thrown if the wished player is not in the database
     */
    public abstract OfflineCorePlayer getOfflineCorePlayer(UUID uuid) throws PlayerNotResolvedException;

    /**
     * disables all Player chat formatting
     *
     * @param disabled if chat should be disabled
     */
    public abstract void setPlayerChatEnabled(boolean disabled);

    /**
     * allows you to change the size of the ProfileInventory
     *
     * @param enabled if custom enderchest (with variable size) should be enabled
     */
    public abstract void setCustomEnderchestEnabled(boolean enabled);

    /**
     * allows to set the cooldown time for how long players must wait to write their next message
     * use 0 for no cooldown
     *
     * @param cooldown cooldown in seconds
     */
    public abstract void setPlayerChatCooldown(int cooldown);

    /**
     * enables an global /server spawn command
     * IMPORTANT: The location "spawn" must be set in the core-config.json of the given world!
     * otherwise the spawn command will throw an error to the player!
     *
     * @param plugin   the plugin for message prefix
     * @param world    CoreWorld
     * @param cooldown cooldown in seconds where the must not move until he get teleported, use 0 for no cooldown
     */
    public abstract void enableSpawnCommand(CorePlugin plugin, CoreWorld world, int cooldown);

    /**
     * allows all players to use /home /sethome /delhome commands
     * allowed home amount can be set with "system.bukkit.home.<i>i</i>" permission where <i>i</i> stands for the maximum allowed homes
     *
     * @param plugin   the plugin for message prefix
     * @param cooldown cooldown in seconds where the must not move until he get teleported, use 0 for no cooldown
     */
    public abstract void enableHomeSystem(CorePlugin plugin, HomeManagerGetter apiGetter, int cooldown);


    public abstract void enableEnderchestSystem(EnderchestManagerGetter apiGetter);

    /**
     * allows all players to use the /tpa /tpaccept /tpdeny commands
     *
     * @param plugin   the plugin for message prefix
     * @param cooldown cooldown in seconds where the must not move until he get teleported, use 0 for no cooldown
     */
    public abstract void enableTpaSystem(CorePlugin plugin, int cooldown);

    public abstract void openProfileInventory(Player p);

    public abstract void openProfileInventory(Player p, Player t);
}
