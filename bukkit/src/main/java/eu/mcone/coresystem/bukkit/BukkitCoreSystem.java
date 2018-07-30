/*
 * Copyright (c) 2017 - 2018 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.coresystem.bukkit;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import eu.mcone.coresystem.api.bukkit.CorePlugin;
import eu.mcone.coresystem.api.bukkit.CoreSystem;
import eu.mcone.coresystem.api.bukkit.hologram.Hologram;
import eu.mcone.coresystem.api.bukkit.hologram.HologramData;
import eu.mcone.coresystem.api.bukkit.inventory.CoreInventory;
import eu.mcone.coresystem.api.bukkit.npc.NPC;
import eu.mcone.coresystem.api.bukkit.npc.NpcData;
import eu.mcone.coresystem.api.bukkit.player.CorePlayer;
import eu.mcone.coresystem.api.bukkit.player.OfflineCorePlayer;
import eu.mcone.coresystem.api.bukkit.scoreboard.MainScoreboard;
import eu.mcone.coresystem.api.bukkit.util.CoreActionBar;
import eu.mcone.coresystem.api.bukkit.util.CoreTablistInfo;
import eu.mcone.coresystem.api.bukkit.util.CoreTitle;
import eu.mcone.coresystem.api.bukkit.world.BuildSystem;
import eu.mcone.coresystem.api.bukkit.world.CoreWorld;
import eu.mcone.coresystem.api.core.exception.PlayerNotResolvedException;
import eu.mcone.coresystem.api.core.gamemode.Gamemode;
import eu.mcone.coresystem.api.core.player.GlobalCorePlayer;
import eu.mcone.coresystem.bukkit.channel.*;
import eu.mcone.coresystem.bukkit.command.*;
import eu.mcone.coresystem.bukkit.hologram.HologramManager;
import eu.mcone.coresystem.bukkit.listener.*;
import eu.mcone.coresystem.bukkit.npc.NpcManager;
import eu.mcone.coresystem.bukkit.player.*;
import eu.mcone.coresystem.bukkit.util.ActionBar;
import eu.mcone.coresystem.bukkit.util.TablistInfo;
import eu.mcone.coresystem.bukkit.util.Title;
import eu.mcone.coresystem.bukkit.world.WorldManager;
import eu.mcone.coresystem.core.CoreModuleCoreSystem;
import eu.mcone.coresystem.core.mysql.Database;
import eu.mcone.coresystem.core.mysql.MySQL;
import eu.mcone.coresystem.core.player.PermissionManager;
import eu.mcone.coresystem.core.player.PlayerUtils;
import eu.mcone.coresystem.core.translation.TranslationManager;
import eu.mcone.coresystem.core.util.CooldownSystem;
import lombok.Getter;
import net.labymod.serverapi.bukkit.LabyModAPI;
import net.minecraft.server.v1_8_R3.MinecraftServer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class BukkitCoreSystem extends CoreSystem implements CoreModuleCoreSystem {

    @Getter
    private static BukkitCoreSystem system;

    private MySQL mysql1;
    private MySQL mysql2;
    private MySQL mysql3;
    private Map<UUID, CoreInventory> inventories;
    private Map<String, CorePlugin> plugins;

    @Getter
    private TranslationManager translationManager;
    @Getter
    private PermissionManager permissionManager;
    @Getter
    private CooldownSystem cooldownSystem;
    @Getter
    private NickManager nickManager;
    @Getter
    private ChannelHandler channelHandler;
    @Getter
    private WorldManager worldManager;
    @Getter
    private CoreAfkManager afkManager;
    @Getter
    private NpcManager npcManager;
    @Getter
    private HologramManager hologramManager;
    @Getter
    private LabyModAPI labyModAPI;
    @Getter
    private PlayerUtils playerUtils;
    @Getter
    private CoinsUtil coinsUtil;
    @Getter
    private Gson gson;

    @Getter
    private Map<UUID, CorePlayer> corePlayers;
    @Getter
    private boolean cloudsystemAvailable;

    @Override
    public void onEnable() {
        setInstance(this);
        system = this;
        inventories = new HashMap<>();
        plugins = new HashMap<>();

        Bukkit.getConsoleSender().sendMessage("§f\n" +
                "      __  _____________  _   ________                                                    \n" +
                "     /  |/  / ____/ __ \\/ | / / ____/                                                    \n" +
                "    / /|_/ / /   / / / /  |/ / __/                                                       \n" +
                "   / /  / / /___/ /_/ / /|  / /___                                                       \n" +
                "  /_/ _/_/\\____/\\____/_/_|_/_____/______               _____            __               \n" +
                "     / __ )__  __/ /__/ /__(_) /_/ ____/___  ________ / ___/__  _______/ /____  ____ ___ \n" +
                "    / __  / / / / //_/ //_/ / __/ /   / __ \\/ ___/ _ \\\\__ \\/ / / / ___/ __/ _ \\/ __ `__ \\\n" +
                "   / /_/ / /_/ / ,< / ,< / / /_/ /___/ /_/ / /  /  __/__/ / /_/ (__  ) /_/  __/ / / / / /\n" +
                "  /_____/\\__,_/_/|_/_/|_/_/\\__/\\____/\\____/_/   \\___/____/\\__, /____/\\__/\\___/_/ /_/ /_/ \n" +
                "                                                         /____/  \n"
        );

        sendConsoleMessage("§aInitializing MariaDB Connections...");
        mysql1 = new MySQL(Database.SYSTEM);
        mysql2 = new MySQL(Database.STATS);
        mysql3 = new MySQL(Database.DATA);
        createTables();

        cooldownSystem = new CooldownSystem();
        coinsUtil = new CoinsUtil(this);
        channelHandler = new ChannelHandler();
        playerUtils = new PlayerUtils(mysql1);
        gson = new GsonBuilder().setPrettyPrinting().create();

        cloudsystemAvailable = checkIfCloudSystemAvailable();
        sendConsoleMessage("§7CloudSystem available: "+cloudsystemAvailable);

        sendConsoleMessage("§aInitializing LabyModAPI...");
        labyModAPI = new LabyModAPI(this);

        sendConsoleMessage("§aStarting WorldManager...");
        worldManager = new WorldManager(this);

        sendConsoleMessage("§aStarting NpcManager...");
        npcManager = new NpcManager(this);

        sendConsoleMessage("§aStarting HologramManager...");
        hologramManager = new HologramManager(this);

        sendConsoleMessage("§aStarting AFK-Manager...");
        afkManager = new CoreAfkManager();

        sendConsoleMessage("§aLoading Translations...");
        translationManager = new TranslationManager(mysql1, this);

        sendConsoleMessage("§aLoading Permissions & Groups...");
        permissionManager = new PermissionManager(MinecraftServer.getServer().getPropertyManager().properties.getProperty("server-name"), mysql1, gson);

        sendConsoleMessage("§aStarting NickManager...");
        nickManager = new NickManager(this);

        sendConsoleMessage("§aLoading Commands, Events...");
        this.registerListener();
        this.registerCommands();
        corePlayers = new HashMap<>();

        sendConsoleMessage("§aRegistering BungeeCord Messaging Channel...");
        getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
        getServer().getMessenger().registerOutgoingPluginChannel(this, "WDL|CONTROL");
        getServer().getMessenger().registerIncomingPluginChannel(this, "MC_ONE_RETURN", new ReturnPluginChannelListener());
        getServer().getMessenger().registerIncomingPluginChannel(this, "MC_ONE_INFO", new InfoPluginChannelListener());
        getServer().getMessenger().registerIncomingPluginChannel(this, "BungeeCord", new BungeeCordReturnPluginChannelListener());
        getServer().getMessenger().registerIncomingPluginChannel(this, "WDL|INIT", new AntiWorldDownloader());

        sendConsoleMessage("§aVersion §f" + this.getDescription().getVersion() + "§a enabled!");

        for (Player p : Bukkit.getOnlinePlayers()) {
            PlayerLogin.setPermissions(p);
            try {
                new eu.mcone.coresystem.bukkit.player.BukkitCorePlayer(this, p.getAddress().getAddress(), p.getName());
                channelHandler.createSetRequest(p, "UNNICK");
            } catch (PlayerNotResolvedException e) {
                e.printStackTrace();
            }
        }

        for (CorePlayer p : getOnlineCorePlayers()) p.setScoreboard(new MainScoreboard());
    }

    @Override
    public void onDisable() {
        afkManager.disable();
        npcManager.unsetNPCs();
        hologramManager.unsetHolograms();

        for (CorePlayer p : getOnlineCorePlayers()) {
            p.getScoreboard().unregister();
            if (p.isNicked()) {
                nickManager.unnick(p.bukkit(), false);
            }
        }

        mysql1.close();
        mysql2.close();
        mysql3.close();

        labyModAPI.disable();
        getCorePlayers().clear();

        getServer().getMessenger().unregisterIncomingPluginChannel(this);
        getServer().getMessenger().unregisterOutgoingPluginChannel(this);

        getServer().getConsoleSender().sendMessage("§cPlugin disabled!");
    }


    private void registerCommands() {
        getCommand("bukkit").setExecutor(new BukkitCMD());
        getCommand("feed").setExecutor(new FeedCMD());
        getCommand("fly").setExecutor(new FlyCMD());
        getCommand("gamemode").setExecutor(new GamemodeCMD());
        getCommand("heal").setExecutor(new HealCMD());
        getCommand("tp").setExecutor(new TpCMD());
        getCommand("tphere").setExecutor(new TphereCMD());
        getCommand("tpall").setExecutor(new TpallCMD());
        getCommand("tppos").setExecutor(new TpposCMD());
        getCommand("stats").setExecutor(new StatsCMD());
        getCommand("speed").setExecutor(new SpeedCMD());
        getCommand("vanish").setExecutor(new VanishCMD());
        getCommand("profil").setExecutor(new ProfileCMD());
    }

    private void registerListener() {
        getServer().getPluginManager().registerEvents(new LabyModPlayerJoin(), this);
        getServer().getPluginManager().registerEvents(new LanguageChange(), this);
        getServer().getPluginManager().registerEvents(new PermissionChange(), this);
        getServer().getPluginManager().registerEvents(new PlayerJoin(), this);
        getServer().getPluginManager().registerEvents(new PlayerLogin(), this);
        getServer().getPluginManager().registerEvents(new PlayerQuit(), this);
        getServer().getPluginManager().registerEvents(new AsyncPlayerChat(), this);
        getServer().getPluginManager().registerEvents(new PlayerSettingsChange(), this);
        getServer().getPluginManager().registerEvents(new InventoryClick(), this);
        getServer().getPluginManager().registerEvents(new PlayerCommandPreprocess(), this);
        getServer().getPluginManager().registerEvents(new SignChange(), this);
    }

    private void createTables() {
        mysql1.update(
                "CREATE TABLE IF NOT EXISTS `bukkitsystem_textures`" +
                        "(" +
                        "`id` int(11) NOT NULL AUTO_INCREMENT PRIMARY KEY, " +
                        "`name` VARCHAR(100) NOT NULL UNIQUE KEY REFERENCES bukkitsystem_npcs(`texture`) ON DELETE SET NULL ON UPDATE SET NULL, " +
                        "`texture_value` VARCHAR(500) NOT NULL, " +
                        "`texture_signature` VARCHAR(1000) NOT NULL" +
                        ") " +
                        "ENGINE=InnoDB DEFAULT CHARSET=utf8;"
        );

        mysql3.update(
                "CREATE TABLE IF NOT EXISTS `bukkitsystem_beta_worlds`" +
                        "(" +
                        "`id` int(11) NOT NULL AUTO_INCREMENT PRIMARY KEY, " +
                        "`build` int NOT NULL, " +
                        "`name` varchar(50) NOT NULL UNIQUE KEY, " +
                        "`bytes` longblob NOT NULL" +
                        ") " +
                        "ENGINE=InnoDB DEFAULT CHARSET=utf8;"
        );
    }

    public MySQL getMySQL(Database database) {
        switch (database) {
            case SYSTEM:
                return mysql1;
            case STATS:
                return mysql2;
            case DATA:
                return mysql3;
            default:
                return null;
        }
    }

    @Override
    public eu.mcone.coresystem.api.core.mysql.MySQL getMySQL() {
        return mysql3;
    }

    public CorePlayer getCorePlayer(Player p) {
        return corePlayers.getOrDefault(p.getUniqueId(), null);
    }

    public CorePlayer getCorePlayer(UUID uuid) {
        return corePlayers.getOrDefault(uuid, null);
    }

    public CorePlayer getCorePlayer(String name) {
        for (CorePlayer p : corePlayers.values()) {
            if (p.getName().equals(name)) return p;
        }
        return null;
    }

    @Override
    public GlobalCorePlayer getGlobalCorePlayer(UUID uuid) {
        return getCorePlayer(uuid);
    }

    @Override
    public Collection<CorePlayer> getOnlineCorePlayers() {
        return corePlayers.values();
    }

    @Override
    public BuildSystem initialiseBuildSystem(BuildSystem.BuildEvent... events) {
        return new eu.mcone.coresystem.bukkit.world.BuildSystem(this, events);
    }

    @Override
    public NPC constructNpc(NpcData npcData) {
        return new eu.mcone.coresystem.bukkit.npc.NPC(worldManager.getWorld(npcData.getLocation().getWorldName()), npcData);
    }

    @Override
    public Hologram constructHologram(HologramData hologramData) {
        return new eu.mcone.coresystem.bukkit.hologram.Hologram(worldManager.getWorld(hologramData.getLocation().getWorldName()), hologramData);
    }

    @Override
    public void enableSpawnCommand(CoreWorld world) {
        getCommand("spawn").setExecutor(new SpawnCMD(world));
    }

    @Override
    public void registerInventory(CoreInventory inventory) {
        inventories.put(inventory.getPlayer().getUniqueId(), inventory);
    }

    @Override
    public Collection<CoreInventory> getInventories() {
        return inventories.values();
    }

    public void clearPlayerInventories(UUID uuid) {
        inventories.remove(uuid);
    }

    @Override
    public StatsAPI getStatsAPI(Gamemode gamemode) {
        return new StatsAPI(this, gamemode);
    }

    @Override
    public void registerPlugin(CorePlugin plugin) {
        plugins.put(plugin.getPluginName(), plugin);
    }

    @Override
    public CorePlugin getPlugin(String name) {
        return plugins.getOrDefault(name, null);
    }

    @Override
    public CoreTitle createTitle() {
        return new Title();
    }

    @Override
    public CoreTablistInfo createTablistInfo() {
        return new TablistInfo();
    }

    @Override
    public CoreActionBar createActionBar() {
        return new ActionBar();
    }

    @Override
    public OfflineCorePlayer getOfflineCorePlayer(String name) throws PlayerNotResolvedException {
        return new BukkitOfflineCorePlayer(this, name);
    }

    @Override
    public void setPlayerChatDisabled(boolean disabled) {
        AsyncPlayerChat.disabled = disabled;
    }

    @Override
    public void runAsync(Runnable runnable) {
        getServer().getScheduler().runTaskAsynchronously(this, runnable);
    }

}
