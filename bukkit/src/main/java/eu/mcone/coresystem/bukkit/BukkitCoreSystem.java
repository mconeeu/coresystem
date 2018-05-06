/*
 * Copyright (c) 2017 - 2018 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.coresystem.bukkit;

import eu.mcone.coresystem.api.bukkit.CoreSystem;
import eu.mcone.coresystem.api.bukkit.command.CoreCommand;
import eu.mcone.coresystem.api.bukkit.config.YAML_Config;
import eu.mcone.coresystem.api.bukkit.hologram.Hologram;
import eu.mcone.coresystem.api.bukkit.hologram.HologramManager;
import eu.mcone.coresystem.api.bukkit.inventory.CoreInventory;
import eu.mcone.coresystem.api.bukkit.npc.NPC;
import eu.mcone.coresystem.api.bukkit.player.BukkitCorePlayer;
import eu.mcone.coresystem.api.bukkit.world.BuildSystem;
import eu.mcone.coresystem.api.bukkit.world.LocationManager;
import eu.mcone.coresystem.api.core.exception.PlayerNotFoundException;
import eu.mcone.coresystem.api.core.gamemode.Gamemode;
import eu.mcone.coresystem.api.core.player.GlobalCorePlayer;
import eu.mcone.coresystem.api.core.player.SkinInfo;
import eu.mcone.coresystem.api.core.translation.TranslationField;
import eu.mcone.coresystem.bukkit.channel.ChannelHandler;
import eu.mcone.coresystem.bukkit.channel.PluginChannelListener;
import eu.mcone.coresystem.bukkit.command.*;
import eu.mcone.coresystem.bukkit.listener.*;
import eu.mcone.coresystem.bukkit.npc.NpcManager;
import eu.mcone.coresystem.bukkit.player.CoinsAPI;
import eu.mcone.coresystem.bukkit.player.NickManager;
import eu.mcone.coresystem.bukkit.player.StatsAPI;
import eu.mcone.coresystem.bukkit.scoreboard.MainScoreboard;
import eu.mcone.coresystem.bukkit.util.AFKCheck;
import eu.mcone.coresystem.bukkit.world.WorldManager;
import eu.mcone.coresystem.bukkit.world.WorldUploader;
import eu.mcone.coresystem.core.CoreModuleCoreSystem;
import eu.mcone.coresystem.core.mysql.Database;
import eu.mcone.coresystem.core.mysql.MySQL;
import eu.mcone.coresystem.core.player.PermissionManager;
import eu.mcone.coresystem.core.player.PlayerUtils;
import eu.mcone.coresystem.core.translation.TranslationManager;
import eu.mcone.coresystem.core.util.CooldownSystem;
import lombok.Getter;
import net.minecraft.server.v1_8_R3.MinecraftServer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.*;

public class BukkitCoreSystem extends CoreSystem implements CoreModuleCoreSystem {

    @Getter
    private static BukkitCoreSystem system;
    private static String MainPrefix = "§8[§fBukkitCore§8] ";

    private MySQL mysql1;
    private MySQL mysql2;
    private MySQL mysql3;
    private Map<UUID, CoreInventory> inventories;
    private Map<Gamemode, StatsAPI> stats;

    @Getter
    private TranslationManager translationManager;
    @Getter
    private PermissionManager permissionManager;
    @Getter
    private CooldownSystem cooldownSystem;
    @Getter
    private NickManager nickManager;
    @Getter
    private CoinsAPI coinsAPI;
    @Getter
    private ChannelHandler channelHandler;
    @Getter
    private WorldManager worldManager;
    @Getter
    private PlayerUtils playerUtils;

    @Getter
    private YAML_Config yamlConfig;
    @Getter
    private Map<UUID, BukkitCorePlayer> corePlayers;
    @Getter
    private HashSet<CoreCommand> commands;
    @Getter
    private boolean cloudsystemAvailable;

    @Override
    public void onEnable() {
        setInstance(this);
        system = this;
        inventories = new HashMap<>();
        commands = new HashSet<>();
        createPluginDir("worlds");

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

        Bukkit.getConsoleSender().sendMessage(MainPrefix + "§aMySQL Verbindungen werden initialisiert...");
        mysql1 = new MySQL(Database.SYSTEM);
        mysql2 = new MySQL(Database.STATS);
        mysql3 = new MySQL(Database.DATA);
        createTables(mysql1);

        cooldownSystem = new CooldownSystem();
        coinsAPI = new CoinsAPI(this);
        channelHandler = new ChannelHandler();
        playerUtils = new PlayerUtils(mysql1);
        yamlConfig = new YAML_Config("MCONE-BukkitCoreSystem", "config.yml");

        stats = new HashMap<>();
        for (Gamemode gamemode : Gamemode.values()) {
            stats.put(gamemode, new StatsAPI(this, gamemode));
        }

        try {
            Class.forName("eu.mcone.cloud.plugin.CloudPlugin");
            cloudsystemAvailable = true;
        } catch (ClassNotFoundException e) {
            cloudsystemAvailable = false;
            Bukkit.getConsoleSender().sendMessage(MainPrefix + "§cCloudSystem nicht verfügbar!");
        }

        Bukkit.getConsoleSender().sendMessage(MainPrefix + "§aWorldManager wird gestartet...");
        worldManager = new WorldManager(this);

        Bukkit.getConsoleSender().sendMessage(MainPrefix + "§aTranslations werden geladen...");
        translationManager = new TranslationManager(mysql1);
        registerTranslations();

        Bukkit.getConsoleSender().sendMessage(MainPrefix + "§aPermissions & Gruppen werden geladen...");
        permissionManager = new PermissionManager(MinecraftServer.getServer().getPropertyManager().properties.getProperty("server-name"), mysql1);

        Bukkit.getConsoleSender().sendMessage(MainPrefix + "§aNickManager wird gestartet...");
        nickManager = new NickManager(this);

        getServer().getConsoleSender().sendMessage(MainPrefix + "§aBefehle, Events, Config & Scheduler werden geladen...");
        this.setupConfig();
        this.startScheduler();
        this.registerListener();
        this.registerCommands();
        corePlayers = new HashMap<>();

        getServer().getConsoleSender().sendMessage(MainPrefix + "§aBungeeCord Messaging Channel wird registriert...");
        getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
        getServer().getMessenger().registerIncomingPluginChannel(this, "Return", new PluginChannelListener());
        getServer().getMessenger().registerIncomingPluginChannel(this, "EventHandler", new PluginChannelListener());

        StringBuilder functions = new StringBuilder();
        int i = 0;
        for (String key : yamlConfig.getConfig().getKeys(true)) {
            if (yamlConfig.getConfig().getBoolean(key)) {
                if ((key == null) || key.equals("") || key.equals(" ")) {
                    return;
                } else if (i == 0) {
                    functions = new StringBuilder("§a" + key);
                    i++;
                } else if (i > 0) {
                    functions.append("§7, §a").append(key);
                }
            }
        }
        getServer().getConsoleSender().sendMessage(MainPrefix + "§7Folgende Funktionen wurden aktiviert: " + functions.toString());
        getServer().getConsoleSender().sendMessage(MainPrefix + "§aVersion §f" + this.getDescription().getVersion() + "§a wurde aktiviert...");

        for (Player p : Bukkit.getOnlinePlayers()) {
            PlayerLogin.setPermissions(p);
            try {
                new eu.mcone.coresystem.bukkit.player.BukkitCorePlayer(this, p.getName());
                channelHandler.sendPluginMessage(p, "UNNICK");
            } catch (PlayerNotFoundException e) {
                e.printStackTrace();
            }
        }
        for (BukkitCorePlayer p : getOnlineCorePlayers()) p.setScoreboard(new MainScoreboard());
    }

    @Override
    public void onDisable() {
        if (yamlConfig.getConfig().getBoolean("AFK-Manager")) {
            for (HashMap.Entry<UUID, Integer> templateEntry : AFKCheck.players.entrySet()) {
                AFKCheck.players.put(templateEntry.getKey(), 0);
            }
        }

        for (BukkitCorePlayer p : getOnlineCorePlayers()) {
            if (p.isNicked()) {
                nickManager.unnick(p.bukkit());
            }
        }

        mysql1.close();
        mysql2.close();
        mysql3.close();

        getCorePlayers().clear();
        getServer().getConsoleSender().sendMessage(MainPrefix + "§cPlugin wurde deaktiviert");
    }

    private void registerTranslations() {
        translationManager.insertIfNotExists(
                new HashMap<String, TranslationField>() {{
                    put("system.bukkit.chat", new TranslationField("§7%Player% §8» §7Nachricht"));
                }}
        );
    }

    private void setupConfig() {
        yamlConfig.getConfig().options().copyDefaults(true);

        yamlConfig.getConfig().addDefault("Tablist", Boolean.TRUE);
        yamlConfig.getConfig().addDefault("UserChat", Boolean.TRUE);
        yamlConfig.getConfig().addDefault("CoinsAPI", Boolean.TRUE);
        yamlConfig.getConfig().addDefault("StatsAPI", Boolean.TRUE);
        yamlConfig.getConfig().addDefault("AFK-Manager", Boolean.TRUE);

        yamlConfig.save();
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
        getCommand("world").setExecutor(new WorldCMD());
    }

    private void registerListener() {
        getServer().getPluginManager().registerEvents(new PermissionChange(), this);
        getServer().getPluginManager().registerEvents(new PlayerJoin(), this);
        getServer().getPluginManager().registerEvents(new PlayerLogin(), this);
        getServer().getPluginManager().registerEvents(new PlayerQuit(), this);
        getServer().getPluginManager().registerEvents(new PlayerChat(), this);
        getServer().getPluginManager().registerEvents(new InventoryClick(), this);
        getServer().getPluginManager().registerEvents(new PlayerCommandPreprocess(), this);
        getServer().getPluginManager().registerEvents(new SignChange(), this);
    }

    private void startScheduler() {
        Bukkit.getScheduler().runTaskTimerAsynchronously(this, () -> {
            if (yamlConfig.getConfig().getBoolean("AFK-Manager")) {
                AFKCheck.check();
            }
        }, 25, 15);
    }

    private void createTables(MySQL mysql) {
        mysql.update(
                "CREATE TABLE IF NOT EXISTS `bukkitsystem_npcs`" +
                        "(" +
                        "`id` int(11) NOT NULL AUTO_INCREMENT PRIMARY KEY, " +
                        "`name` VARCHAR(100) NOT NULL, " +
                        "`location` VARCHAR(100) NOT NULL, " +
                        "`texture` VARCHAR(10000) NOT NULL, " +
                        "`displayname` VARCHAR(1000) NOT NULL, " +
                        "`server` varchar(100) NOT NULL" +
                        ") " +
                        "ENGINE=InnoDB DEFAULT CHARSET=utf8;"
        );

        mysql.update(
                "CREATE TABLE IF NOT EXISTS `bukkitsystem_textures`" +
                        "(" +
                        "`id` int(11) NOT NULL AUTO_INCREMENT PRIMARY KEY, " +
                        "`name` VARCHAR(100) NOT NULL UNIQUE KEY REFERENCES bukkitsystem_npcs(`texture`) ON DELETE SET NULL ON UPDATE SET NULL, " +
                        "`texture_value` VARCHAR(500) NOT NULL, " +
                        "`texture_signature` VARCHAR(1000) NOT NULL" +
                        ") " +
                        "ENGINE=InnoDB DEFAULT CHARSET=utf8;"
        );

        mysql.update(
                "CREATE TABLE IF NOT EXISTS `bukkitsystem_holograms`" +
                        "(" +
                        "`id` int(11) NOT NULL AUTO_INCREMENT PRIMARY KEY, " +
                        "`name` VARCHAR(100) NOT NULL UNIQUE KEY, " +
                        "`location` VARCHAR(100) NOT NULL, " +
                        "`lines` VARCHAR(1000) NOT NULL, " +
                        "`server` varchar(100) NOT NULL" +
                        ") " +
                        "ENGINE=InnoDB DEFAULT CHARSET=utf8;"
        );

        mysql.update(
                "CREATE TABLE IF NOT EXISTS `bukkitsystem_locations`" +
                        "(" +
                        "`id` int(11) NOT NULL AUTO_INCREMENT PRIMARY KEY," +
                        "`name` VARCHAR(100) NOT NULL," +
                        "`location` VARCHAR(100) NOT NULL," +
                        "`server` VARCHAR(100) NOT NULL" +
                        ")" +
                        "ENGINE=InnoDB DEFAULT CHARSET=utf8;"
        );
    }

    private void createPluginDir(String path) {
        String s = File.separator;
        File file = new File(System.getProperty("user.dir") + s + "plugins" + s + path);

        if (!file.exists()) {
            file.mkdir();
        }
    }

    @Override
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

    public BukkitCorePlayer getCorePlayer(Player p) {
        return corePlayers.getOrDefault(p.getUniqueId(), null);
    }

    public BukkitCorePlayer getCorePlayer(UUID uuid) {
        return corePlayers.getOrDefault(uuid, null);
    }

    public BukkitCorePlayer getCorePlayer(String name) {
        for (BukkitCorePlayer p : corePlayers.values()) {
            if (p.getName().equals(name)) return p;
        }
        return null;
    }

    @Override
    public GlobalCorePlayer getGlobalCorePlayer(UUID uuid) {
        return getCorePlayer(uuid);
    }

    @Override
    public Collection<BukkitCorePlayer> getOnlineCorePlayers() {
        return corePlayers.values();
    }

    @Override
    public NPC constructNPC(String name, Location location, String texture, String displayname) {
        return new eu.mcone.coresystem.bukkit.npc.NPC(name, location, texture, displayname);
    }

    @Override
    public NPC constructNPC(String name, Location location, SkinInfo skinInfo, String displayname) {
        return new eu.mcone.coresystem.bukkit.npc.NPC(name, location, skinInfo, displayname);
    }

    @Override
    public Hologram constructHologram(String[] text, Location location) {
        return new eu.mcone.coresystem.bukkit.hologram.Hologram(text, location);
    }

    @Override
    public boolean uploadWorld(World w) {
        try {
            new WorldUploader(w).upload();
            return true;
        } catch (SQLException | IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public NpcManager initialiseNpcManager(String server) {
        return new NpcManager(mysql1, server);
    }

    @Override
    public HologramManager inititaliseHologramManager(String server) {
        return new eu.mcone.coresystem.bukkit.hologram.HologramManager(this, server);
    }

    @Override
    public BuildSystem initialiseBuildSystem(boolean notify, BuildSystem.BuildEvent... events) {
        return new eu.mcone.coresystem.bukkit.util.BuildSystem(this, notify, events);
    }

    @Override
    public LocationManager initialiseLocationManager(String server) {
        return new eu.mcone.coresystem.bukkit.util.LocationManager(mysql1, server);
    }

    @Override
    public void registerInventory(CoreInventory inventory) {
        inventories.put(inventory.getPlayer().getUniqueId(), inventory);
    }

    @Override
    public Collection<CoreInventory> getInventories() {
        return inventories.values();
    }

    @Override
    public void clearPlayerInventories(UUID uuid) {
        inventories.remove(uuid);
    }

    @Override
    public StatsAPI getStatsAPI(Gamemode gamemode) {
        return stats.getOrDefault(gamemode, null);
    }

    @Override
    public void registerCommand(CoreCommand command) {
        commands.add(command);
    }

    @Override
    public CoreCommand getCoreCommand(String name) {
        for (CoreCommand command : commands) {
            if (command.getCommand().equalsIgnoreCase(name)) {
                return command;
            }
        }

        return null;
    }

    @Override
    public void runAsync(Runnable runnable) {
        getServer().getScheduler().runTaskAsynchronously(this, runnable);
    }
}
