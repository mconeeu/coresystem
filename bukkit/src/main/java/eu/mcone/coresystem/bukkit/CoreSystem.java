/*
 * Copyright (c) 2017 - 2018 Dominik L., Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.coresystem.bukkit;

import eu.mcone.coresystem.bukkit.api.StatsAPI;
import eu.mcone.coresystem.bukkit.channel.PluginChannelListener;
import eu.mcone.coresystem.bukkit.channel.PluginMessage;
import eu.mcone.coresystem.bukkit.util.CooldownSystem;
import eu.mcone.coresystem.bukkit.config.YAML_Config;
import eu.mcone.coresystem.bukkit.player.CorePlayer;
import eu.mcone.coresystem.bukkit.player.NickManager;
import eu.mcone.coresystem.bukkit.scoreboard.MainScoreboard;
import eu.mcone.coresystem.bukkit.util.AFKCheck;
import eu.mcone.coresystem.bukkit.command.*;
import eu.mcone.coresystem.bukkit.listener.*;
import eu.mcone.coresystem.bukkit.scoreboard.Objective;
import eu.mcone.coresystem.lib.mysql.MySQL;
import eu.mcone.coresystem.lib.mysql.MySQL_Config;
import eu.mcone.coresystem.lib.player.PermissionManager;
import net.minecraft.server.v1_8_R3.MinecraftServer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.DisplaySlot;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.bukkit.Bukkit.getMessenger;
import static org.bukkit.Bukkit.getPluginManager;

public class CoreSystem extends JavaPlugin {

	private static CoreSystem instance;
    public static String MainPrefix = "§8[§fBukkitCore§8] ";

    public static MySQL mysql1;
    public static MySQL mysql2;
    public static MySQL mysql3;

    public static StatsAPI statsBedwars;
    public static StatsAPI statsSkypvp;
    public static StatsAPI statsKnockit;
    public static StatsAPI statsMinewar;
	
	public static MySQL_Config config;
    public static YAML_Config cfg = new YAML_Config("MCONE-BukkitCoreSystem", "config.yml");

	private PermissionManager permissionManager;
	private CooldownSystem cooldownSystem;
	private NickManager nickManager;

    private static Map<UUID, CorePlayer> corePlayers;

	@Override
	public void onEnable(){
		instance = this;
        cooldownSystem = new CooldownSystem();

        mysql1 = new MySQL("78.46.249.195", 3306, "mc1system", "mc1system", "6THk8uDbTtDKf8yUMf2r62MHMZ57EVMBFkMDEgFqz9YF8prKug2q9DXLvTJZEmsa");
        mysql2 = new MySQL("78.46.249.195", 3306, "mc1stats", "mc1stats", "qN8FQK.hj)_Lat?uK)-#6F-$3![t;2E6KZ$sb+Am3g!VHRDe&w$nQX)5}VKb@-@[}e");
        mysql3 = new MySQL("78.46.249.195", 3306, "mc1config", "mc1config", "q%sZp=6/_wx2M2B.Qzaeya4Kd5;f4W*w*M?3#kM,QPjv6VuG3=TjTJ63CPD)}WV;");

        statsMinewar = new StatsAPI("Minewar", "§5§lMineWar", mysql2);
        statsBedwars = new StatsAPI("Bedwars", "§c§lBedwars", mysql2);
        statsSkypvp = new StatsAPI("Skypvp", "§9§lSkypvp", mysql2);
        statsKnockit = new StatsAPI("Knockit", "§2§lKnockIT", mysql2);

        getServer().getConsoleSender().sendMessage(MainPrefix + "§aMySQL Config wird initiiert");
		config = new MySQL_Config(mysql3, "BukkitCoreSystem", 800);
		this.registerMySQLConfig();

        getServer().getConsoleSender().sendMessage(MainPrefix + "§aPermissions & Gruppen werden geladen...");
        permissionManager = new PermissionManager(MinecraftServer.getServer().getPropertyManager().properties.getProperty("server-name"), mysql1);

        getServer().getConsoleSender().sendMessage(MainPrefix + "§aNickManager wird gestartet...");
        nickManager = new NickManager(true);

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
            new CorePlayer(p.getUniqueId(), p.getName());
            new PluginMessage(p, "UNNICK");
        }
        for (CorePlayer p : getOnlineCorePlayers()) p.setScoreboard(new MainScoreboard(p));
	}

	@Override
	public void onDisable(){
	    if (CoreSystem.cfg.getConfig().getBoolean("AFK-Manager")) {
	        for (HashMap.Entry<UUID, Integer> templateEntry : AFKCheck.players.entrySet()) {
	            AFKCheck.players.put(templateEntry.getKey(), 0);
	            mysql1.update("UPDATE userinfo SET status='online' WHERE uuid='" + templateEntry.getKey() + "'");
	            Objective o = getCorePlayer(templateEntry.getKey()).getScoreboard().getObjective(DisplaySlot.BELOW_NAME);
	            if (o != null) o.bukkit().unregister();
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
		getCommand("gm").setExecutor(new GamemodeCMD());
		getCommand("tp").setExecutor(new TpCMD());
		getCommand("tphere").setExecutor(new TphereCMD());
		getCommand("tpall").setExecutor(new TpallCMD());
		getCommand("tppos").setExecutor(new TpposCMD());
		getCommand("stats").setExecutor(new StatsCMD());
		getCommand("vanish").setExecutor(new VanishCMD());
        getCommand("profil").setExecutor(new ProfilCMD());
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

    public static Map<UUID, CorePlayer> getCorePlayers() {
	    return corePlayers;
    }

	public static CoreSystem getInstance(){
		return instance;
	}

    public PermissionManager getPermissionManager() {
        return permissionManager;
    }

    public CooldownSystem getCooldownSystem() {
        return cooldownSystem;
    }

    public NickManager getNickManager() {
        return nickManager;
    }
}
