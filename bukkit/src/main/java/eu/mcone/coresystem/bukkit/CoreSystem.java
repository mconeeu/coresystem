/*
 * Copyright (c) 2017 - 2018 Dominik L., Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.coresystem.bukkit;

import eu.mcone.coresystem.bukkit.api.StatsAPI;
import eu.mcone.coresystem.bukkit.channel.PluginChannelListener;
import eu.mcone.coresystem.bukkit.channel.PluginMessage;
import eu.mcone.coresystem.bukkit.command.*;
import eu.mcone.coresystem.bukkit.config.YAML_Config;
import eu.mcone.coresystem.bukkit.inventory.CoreInventory;
import eu.mcone.coresystem.bukkit.listener.*;
import eu.mcone.coresystem.bukkit.player.CorePlayer;
import eu.mcone.coresystem.bukkit.player.NickManager;
import eu.mcone.coresystem.bukkit.scoreboard.MainScoreboard;
import eu.mcone.coresystem.bukkit.util.AFKCheck;
import eu.mcone.coresystem.bukkit.util.CooldownSystem;
import eu.mcone.coresystem.lib.exception.CoreException;
import eu.mcone.coresystem.lib.gamemode.Gamemode;
import eu.mcone.coresystem.lib.mysql.MySQL;
import eu.mcone.coresystem.lib.mysql.MySQL_Config;
import eu.mcone.coresystem.lib.player.PermissionManager;
import lombok.Getter;
import net.minecraft.server.v1_8_R3.MinecraftServer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.*;

import static org.bukkit.Bukkit.getMessenger;
import static org.bukkit.Bukkit.getPluginManager;

public class CoreSystem extends JavaPlugin {

    @Getter
	private static CoreSystem instance;
    private static String MainPrefix = "§8[§fBukkitCore§8] ";

    public static MySQL mysql1;
    public static MySQL mysql2;
    public static MySQL mysql3;
	
	public static MySQL_Config config;
    public static YAML_Config cfg = new YAML_Config("MCONE-BukkitCoreSystem", "config.yml");

    @Getter
	private PermissionManager permissionManager;
    @Getter
	private CooldownSystem cooldownSystem;
    @Getter
	private NickManager nickManager;

    @Getter
    private static Map<UUID, CorePlayer> corePlayers;
    @Getter
    private HashSet<CoreCommand> commands;

    private Map<UUID, CoreInventory> inventories;
    private Map<Gamemode, StatsAPI> stats;

	@Override
	public void onEnable(){
		instance = this;
		inventories = new HashMap<>();
		commands = new HashSet<>();
        cooldownSystem = new CooldownSystem();
        createPluginDir("worlds");

        getServer().getConsoleSender().sendMessage("§f\n"+
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

        getServer().getConsoleSender().sendMessage(MainPrefix + "§aMySQL Verbindungen werden initialisiert...");
        mysql1 = new MySQL("78.46.249.195", 3306, "mc1system", "mc1system", "6THk8uDbTtDKf8yUMf2r62MHMZ57EVMBFkMDEgFqz9YF8prKug2q9DXLvTJZEmsa");
        mysql2 = new MySQL("78.46.249.195", 3306, "mc1stats", "mc1stats", "qN8FQK.hj)_Lat?uK)-#6F-$3![t;2E6KZ$sb+Am3g!VHRDe&w$nQX)5}VKb@-@[}e");
        mysql3 = new MySQL("78.46.249.195", 3306, "mc1config", "mc1config", "q%sZp=6/_wx2M2B.Qzaeya4Kd5;f4W*w*M?3#kM,QPjv6VuG3=TjTJ63CPD)}WV;");
        createTables(mysql1);

        stats = new HashMap<>();
        for (Gamemode gamemode : Gamemode.values()) {
            stats.put(gamemode, new StatsAPI(gamemode, mysql2));
        }

        getServer().getConsoleSender().sendMessage(MainPrefix + "§aMySQL Config wird initiiert...");
		config = new MySQL_Config(mysql3, "BukkitCoreSystem", 800);
		this.registerMySQLConfig();

        getServer().getConsoleSender().sendMessage(MainPrefix + "§aPermissions & Gruppen werden geladen...");
        permissionManager = new PermissionManager(MinecraftServer.getServer().getPropertyManager().properties.getProperty("server-name"), mysql1);

        getServer().getConsoleSender().sendMessage(MainPrefix + "§aNickManager wird gestartet...");
        nickManager = new NickManager();

        getServer().getConsoleSender().sendMessage(MainPrefix + "§aBefehle, Events, Config & Scheduler werden geladen...");
		this.setupConfig();
        this.startScheduler();
        this.registerListener();
        this.registerCommands();
        corePlayers = new HashMap<>();

        getServer().getConsoleSender().sendMessage(MainPrefix + "§aBungeeCord Messaging Channel wird registriert...");
        getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
        getMessenger().registerIncomingPluginChannel(this, "Return", new PluginChannelListener());
        getMessenger().registerIncomingPluginChannel(this, "EventHandler", new PluginChannelListener());

        StringBuilder functions = new StringBuilder();
        int i = 0;
        for (String key : cfg.getConfig().getKeys(true)) {
            if (cfg.getConfig().getBoolean(key)) {
                if ((key == null) || key.equals("") || key.equals(" ")) {
                    return;
                } else if (i==0) {
                    functions = new StringBuilder("§a" + key);
                    i++;
                } else if (i>0) {
                    functions.append("§7, §a").append(key);
                }
            }
        }
        getServer().getConsoleSender().sendMessage(MainPrefix + "§7Folgende Funktionen wurden aktiviert: " + functions.toString());
        getServer().getConsoleSender().sendMessage(MainPrefix + "§aVersion §f" + this.getDescription().getVersion() + "§a wurde aktiviert...");

        for (Player p : Bukkit.getOnlinePlayers()) {
            PlayerLogin.setPermissions(p);
            try {
                new CorePlayer(p.getUniqueId(), p.getName());
                new PluginMessage(p, "UNNICK");
            } catch (CoreException e) {
                e.printStackTrace();
            }
        }
        for (CorePlayer p : getOnlineCorePlayers()) p.setScoreboard(new MainScoreboard());
	}

    @Override
	public void onDisable(){
	    if (CoreSystem.cfg.getConfig().getBoolean("AFK-Manager")) {
	        for (HashMap.Entry<UUID, Integer> templateEntry : AFKCheck.players.entrySet()) {
	            AFKCheck.players.put(templateEntry.getKey(), 0);
            }
        }

        for (CorePlayer p : getOnlineCorePlayers()) {
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

	private void registerMySQLConfig(){
		//create table
		config.createTable();

		//System-Prefix Config values
        config.insertMySQLConfig("Prefix", "&8[&7&l!&8]&f Server &8» &7");
        config.insertMySQLConfig("Chat-Design", "&7%Player% &8» &7Nachricht");

        //store
        config.store();
	}

    private void setupConfig() {
	    cfg.getConfig().options().copyDefaults(true);

        cfg.getConfig().addDefault("Tablist", Boolean.TRUE);
        cfg.getConfig().addDefault("UserChat", Boolean.TRUE);
        cfg.getConfig().addDefault("CoinsAPI", Boolean.TRUE);
        cfg.getConfig().addDefault("StatsAPI", Boolean.TRUE);
        cfg.getConfig().addDefault("AFK-Manager", Boolean.TRUE);

        cfg.save();
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
        getPluginManager().registerEvents(new PermissionChange(), this);
		getPluginManager().registerEvents(new PlayerJoin(), this);
		getPluginManager().registerEvents(new PlayerLogin(), this);
		getPluginManager().registerEvents(new PlayerQuit(), this);
		getPluginManager().registerEvents(new PlayerChat(), this);
		getPluginManager().registerEvents(new InventoryClick(), this);
		getPluginManager().registerEvents(new PlayerCommandPreprocess(), this);
        getPluginManager().registerEvents(new SignChange(), this);
	}

	private void startScheduler() {
        org.bukkit.Bukkit.getScheduler().runTaskTimer(this, () -> {
            if (CoreSystem.cfg.getConfig().getBoolean("AFK-Manager")){
                AFKCheck.check();
            }
        }, 20L, 20L);
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
                "CREATE TABLE IF NOT EXISTS `bukkitsystem_worlds`" +
                "(" +
                    "`id` int(11) NOT NULL AUTO_INCREMENT PRIMARY KEY," +
                    "`name` VARCHAR(100) NOT NULL UNIQUE KEY," +
                    "`bytes` int NOT NULL," +
                    "`server` VARCHAR(100) NOT NULL" +
                ")" +
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
        File file = new File(System.getProperty("user.dir")+s+"plugins"+s+path);

        if (!file.exists()) {
            file.mkdir();
        }
    }

    public static CorePlayer getCorePlayer(Player p) {
	    return corePlayers.getOrDefault(p.getUniqueId(), null);
    }

    public static CorePlayer getCorePlayer(UUID uuid) {
        return corePlayers.getOrDefault(uuid, null);
    }

    public static CorePlayer getCorePlayer(String name) {
        for (CorePlayer p : corePlayers.values()) {
            if (p.getName().equals(name)) return p;
        }
        return null;
    }

    public static Collection<CorePlayer> getOnlineCorePlayers() {
	    return corePlayers.values();
    }

    public void registerInventory(CoreInventory inventory) {
	    inventories.put(inventory.getPlayer().getUniqueId(), inventory);
    }

    public Collection<CoreInventory> getInventories() {
        return inventories.values();
    }

    public void clearPlayerInventories(UUID uuid) {
	    if (inventories.containsKey(uuid)) inventories.remove(uuid);
    }

    public StatsAPI getStatsAPI(Gamemode gamemode) {
	    return stats.getOrDefault(gamemode, null);
    }

    public void registerCommand(CoreCommand command) {
	    commands.add(command);
    }

    public CoreCommand getCoreCommand(String name) {
        for (CoreCommand command : commands) {
            if (command.getCommand().equalsIgnoreCase(name)) {
                return command;
            }
        }

        return null;
    }

}
