/*
 * Copyright (c) 2017 - 2018 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.coresystem.bungee;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import eu.mcone.coresystem.api.bungee.CorePlugin;
import eu.mcone.coresystem.api.bungee.CoreSystem;
import eu.mcone.coresystem.api.bungee.player.CorePlayer;
import eu.mcone.coresystem.api.bungee.player.OfflineCorePlayer;
import eu.mcone.coresystem.api.core.exception.PlayerNotResolvedException;
import eu.mcone.coresystem.api.core.player.GlobalCorePlayer;
import eu.mcone.coresystem.api.core.player.PlayerSettings;
import eu.mcone.coresystem.api.core.player.PlayerState;
import eu.mcone.coresystem.bungee.command.*;
import eu.mcone.coresystem.bungee.friend.FriendSystem;
import eu.mcone.coresystem.bungee.listener.*;
import eu.mcone.coresystem.bungee.player.BungeeOfflineCorePlayer;
import eu.mcone.coresystem.bungee.player.CoinsUtil;
import eu.mcone.coresystem.bungee.player.NickManager;
import eu.mcone.coresystem.bungee.runnable.Broadcast;
import eu.mcone.coresystem.bungee.runnable.OnlineTime;
import eu.mcone.coresystem.bungee.runnable.PremiumCheck;
import eu.mcone.coresystem.bungee.utils.ChannelHandler;
import eu.mcone.coresystem.bungee.utils.PreferencesManager;
import eu.mcone.coresystem.bungee.utils.TeamspeakVerifier;
import eu.mcone.coresystem.core.CoreModuleCoreSystem;
import eu.mcone.coresystem.core.mysql.MySQL;
import eu.mcone.coresystem.core.mysql.MySQLDatabase;
import eu.mcone.coresystem.core.player.PermissionManager;
import eu.mcone.coresystem.core.player.PlayerUtils;
import eu.mcone.coresystem.core.translation.TranslationManager;
import eu.mcone.coresystem.core.util.CooldownSystem;
import eu.mcone.networkmanager.core.api.database.Database;
import eu.mcone.networkmanager.core.api.database.MongoDBManager;
import eu.mcone.networkmanager.core.database.MongoConnection;
import lombok.Getter;
import net.labymod.serverapi.bungee.LabyModAPI;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class BungeeCoreSystem extends CoreSystem implements CoreModuleCoreSystem {

    @Getter
    private static BungeeCoreSystem system;
    @Getter
    private boolean cloudsystemAvailable;

    private Map<String, CorePlugin> plugins;

    private MongoConnection mongoConnection;
    @Getter
    private MongoDBManager mongoDB;

    @Getter
    private TranslationManager translationManager;
    @Getter
    private PreferencesManager preferences;
    @Getter
    private PermissionManager permissionManager;
    @Getter
    private CooldownSystem cooldownSystem;
    @Getter
    private FriendSystem friendSystem;
    @Getter
    private NickManager nickManager;
    @Getter
    private TeamspeakVerifier teamspeakVerifier = null;
    @Getter
    private ChannelHandler channelHandler;
    @Getter
    private PlayerUtils playerUtils;
    @Getter
    private CoinsUtil coinsUtil;
    @Getter
    private LabyModAPI labyModAPI;
    @Getter
    private Gson gson;
    @Getter
    private Gson simpleGson;

    @Getter
    private MySQL database;

    @Getter
    private Map<UUID, CorePlayer> corePlayers;

    public void onEnable() {
        system = this;
        setInstance(this);
        corePlayers = new HashMap<>();
        plugins = new HashMap<>();

        getProxy().getConsole().sendMessage(new TextComponent(TextComponent.fromLegacyText("\n" +
                "      __  _____________  _   ________                                                          \n" +
                "     /  |/  / ____/ __ \\/ | / / ____/                                                          \n" +
                "    / /|_/ / /   / / / /  |/ / __/                                                             \n" +
                "   / /  / / /___/ /_/ / /|  / /___                                                             \n" +
                "  /_/ _/_/\\____/\\____/_/ |_/_____/      ______               _____            __               \n" +
                "     / __ )__  ______  ____ ____  ___  / ____/___  ________ / ___/__  _______/ /____  ____ ___ \n" +
                "    / __  / / / / __ \\/ __ `/ _ \\/ _ \\/ /   / __ \\/ ___/ _ \\\\__ \\/ / / / ___/ __/ _ \\/ __ `__ \\\n" +
                "   / /_/ / /_/ / / / / /_/ /  __/  __/ /___/ /_/ / /  /  __/__/ / /_/ (__  ) /_/  __/ / / / / /\n" +
                "  /_____/\\__,_/_/ /_/\\__, /\\___/\\___/\\____/\\____/_/   \\___/____/\\__, /____/\\__/\\___/_/ /_/ /_/ \n" +
                "                    /____/                                     /____/\n")));

        gson = new GsonBuilder().setPrettyPrinting().create();
        simpleGson = new Gson();

        mongoConnection = new MongoConnection("db.mcone.eu", "admin", "T6KIq8gjmmF1k7futx0cJiJinQXgfguYXruds1dFx1LF5IsVPQjuDTnlI1zltpD9", "admin", 27017);
        mongoConnection.connect();
        mongoDB = mongoConnection.getDatabase(Database.SYSTEM);

        sendConsoleMessage("§aInitializing MariaDB Connections...");
        createTables(database = new MySQL(MySQLDatabase.SYSTEM));

        cooldownSystem = new CooldownSystem();
        channelHandler = new ChannelHandler();
        preferences = new PreferencesManager(database);
        playerUtils = new PlayerUtils(database);
        coinsUtil = new CoinsUtil(this);

        cloudsystemAvailable = checkIfCloudSystemAvailable();
        sendConsoleMessage("§7CloudSystem available: " + cloudsystemAvailable);

        sendConsoleMessage("§aLoading Translations...");
        translationManager = new TranslationManager(mongoDB, this);

        sendConsoleMessage("§aLoading Permissions & Groups...");
        permissionManager = new PermissionManager("Proxy", database, simpleGson);

        sendConsoleMessage("§aLoading FriendSystem...");
        friendSystem = new FriendSystem(database);

        sendConsoleMessage("§aInitializing LabyModAPI...");
        labyModAPI = new LabyModAPI(this);

        if (!Boolean.valueOf(System.getProperty("DisableTsQuery"))) {
            sendConsoleMessage("§aLoading TeamSpeakQuery...");
            //teamspeakVerifier = new TeamspeakVerifier();
        } else {
            sendConsoleMessage("§cTeamSpeakQuery disabled by JVM Argument");
        }

        sendConsoleMessage("§aLoading Nicksystem...");
        nickManager = new NickManager(this);

        sendConsoleMessage("§aRegistering Commands, Events & Scheduler...");
        registerCommand();
        postRegisterCommand();
        registerEvents();
        loadSchedulers();

        sendConsoleMessage("§aRegistering Plugin Messaging Channel...");
        getProxy().registerChannel("MC_ONE_RETURN");
        getProxy().registerChannel("MC_ONE_INFO");

        sendConsoleMessage("§aVersion: §f" + this.getDescription().getVersion() + "§a enabled!");
    }

    public void onDisable() {
        if (teamspeakVerifier != null) teamspeakVerifier.close();
        for (CorePlayer p : getOnlineCorePlayers()) {
            ((eu.mcone.coresystem.core.player.GlobalCorePlayer) p).setState(PlayerState.OFFLINE);
        }

        mongoConnection.disconnect();
        database.close();
        sendConsoleMessage("§cPlugin disabled!");
    }

    private void registerCommand() {
        getProxy().getPluginManager().registerCommand(this, new PingCMD());
        getProxy().getPluginManager().registerCommand(this, new TeamChatCMD());
        getProxy().getPluginManager().registerCommand(this, new PermsCMD());
        getProxy().getPluginManager().registerCommand(this, new BanCMD());
        getProxy().getPluginManager().registerCommand(this, new WhoisCMD());
        getProxy().getPluginManager().registerCommand(this, new RestartCMD());
        getProxy().getPluginManager().registerCommand(this, new WartungCMD());
        getProxy().getPluginManager().registerCommand(this, new DatenschutzCMD());
        getProxy().getPluginManager().registerCommand(this, new CoinsCMD());

        getProxy().getPluginManager().registerCommand(this, new NickCMD());
        getProxy().getPluginManager().registerCommand(this, new UnnickCMD());

        getProxy().getPluginManager().registerCommand(this, new FriendCMD());
        getProxy().getPluginManager().registerCommand(this, new PartyCMD());
        getProxy().getPluginManager().registerCommand(this, new JumpCMD());
        getProxy().getPluginManager().registerCommand(this, new MsgCMD());
        getProxy().getPluginManager().registerCommand(this, new ReplyCMD());
        getProxy().getPluginManager().registerCommand(this, new ReportCMD());
        getProxy().getPluginManager().registerCommand(this, new HelpCMD());
        getProxy().getPluginManager().registerCommand(this, new BungeecordCMD());
        getProxy().getPluginManager().registerCommand(this, new RegisterCMD());
        getProxy().getPluginManager().registerCommand(this, new ForgotpassCMD());
        getProxy().getPluginManager().registerCommand(this, new ChatlogCMD());
        getProxy().getPluginManager().registerCommand(this, new RegelnCMD());

        getProxy().getPluginManager().registerCommand(this, new PremiumCMD());
        getProxy().getPluginManager().registerCommand(this, new YoutubeCMD());
        getProxy().getPluginManager().registerCommand(this, new TsCMD());
        getProxy().getPluginManager().registerCommand(this, new VoteCMD());
        getProxy().getPluginManager().registerCommand(this, new BewerbenCMD());
        getProxy().getPluginManager().registerCommand(this, new TeamCMD());
        getProxy().getPluginManager().registerCommand(this, new BugreportCMD());
        getProxy().getPluginManager().registerCommand(this, new RegelnCMD());
    }

    private void postRegisterCommand() {
        getProxy().getScheduler().schedule(
                getInstance(),
                () -> getProxy().getPluginManager().registerCommand(this, new ServerCMD()),
                1,
                TimeUnit.SECONDS
        );
    }

    private void registerEvents() {
        getProxy().getPluginManager().registerListener(this, new Chat());
        getProxy().getPluginManager().registerListener(this, new CoinsChange());
        getProxy().getPluginManager().registerListener(this, new LabyModPlayerJoin());
        getProxy().getPluginManager().registerListener(this, new PermissionChange());
        getProxy().getPluginManager().registerListener(this, new PermissionCheck());
        getProxy().getPluginManager().registerListener(this, new PostLogin());
        getProxy().getPluginManager().registerListener(this, new ProxyPing());
        getProxy().getPluginManager().registerListener(this, new PlayerSettingsChange());
        getProxy().getPluginManager().registerListener(this, new ServerConnect());
        getProxy().getPluginManager().registerListener(this, new PreLogin());
        getProxy().getPluginManager().registerListener(this, new PlayerDisconnect());
        getProxy().getPluginManager().registerListener(this, new TabComplete());
        getProxy().getPluginManager().registerListener(this, new ServerKick());
        getProxy().getPluginManager().registerListener(this, new ServerSwitch());
        getProxy().getPluginManager().registerListener(this, new PluginMessage());
    }

    private void createTables(MySQL mysql) {
        mysql.update("CREATE TABLE IF NOT EXISTS `userinfo` " +
                "( " +
                "`id` int(11) NOT NULL AUTO_INCREMENT PRIMARY KEY, " +
                "`uuid` varchar(100) NOT NULL UNIQUE KEY, " +
                "`name` varchar(20) NOT NULL, " +
                "`groups` varchar(20), " +
                "`coins` int(100) NOT NULL, " +
                "`about` varchar(500), " +
                "`state` int(10) NOT NULL, " +
                "`email` varchar(100), " +
                "`ip` varchar(100), " +
                "`timestamp` varchar(100), " +
                "`password` varchar(100), " +
                "`onlinetime` int(10) NOT NULL DEFAULT '0', " +
                "`teamspeak_uid` varchar(100), " +
                "`player_settings` varchar(1000) NOT NULL DEFAULT '" + gson.toJson(new PlayerSettings(), PlayerSettings.class) + "'" +
                ") ENGINE=InnoDB DEFAULT CHARSET=utf8;");

        mysql.update("CREATE TABLE IF NOT EXISTS `permissions` " +
                "(" +
                "`id` int(11) NOT NULL AUTO_INCREMENT PRIMARY KEY, " +
                "`name` varchar(100) NOT NULL, " +
                "`key` varchar(100) NOT NULL, " +
                "`value` varchar(1000), " +
                "`server` varchar(100)" +
                ") ENGINE=InnoDB DEFAULT CHARSET=utf8;");

        mysql.update("CREATE TABLE IF NOT EXISTS `translations` " +
                "(" +
                "`id` int(11) NOT NULL AUTO_INCREMENT PRIMARY KEY, " +
                "`key` varchar(100) NOT NULL UNIQUE KEY, " +
                "`category` varchar(20), " +
                "`DE` varchar(2000), " +
                "`EN` varchar(2000), " +
                "`FR` varchar(2000)" +
                ") ENGINE=InnoDB DEFAULT CHARSET=utf8;");

        mysql.update("CREATE TABLE IF NOT EXISTS `bungeesystem_preferences` " +
                "(" +
                "`id` int(11) NOT NULL AUTO_INCREMENT PRIMARY KEY, " +
                "`key` varchar(100) NOT NULL UNIQUE KEY, " +
                "`value` varchar(100)" +
                ") ENGINE=InnoDB DEFAULT CHARSET=utf8;");

        mysql.update("CREATE TABLE IF NOT EXISTS `bungeesystem_betakey` " +
                "(" +
                "`id` int(11) NOT NULL AUTO_INCREMENT PRIMARY KEY, " +
                "`uuid` varchar(100) NOT NULL UNIQUE KEY, " +
                "`timestamp` int(50)" +
                ") ENGINE=InnoDB DEFAULT CHARSET=utf8;");

        mysql.update("CREATE TABLE IF NOT EXISTS `bungeesystem_friends` " +
                "(" +
                "`id` int(11) NOT NULL AUTO_INCREMENT PRIMARY KEY, " +
                "`uuid` varchar(100), " +
                "`target` varchar(100) NOT NULL, " +
                "`key` varchar(100) NOT NULL, " +
                "`timestamp` int(100)" +
                ") ENGINE=InnoDB DEFAULT CHARSET=utf8;");

        mysql.update("CREATE TABLE IF NOT EXISTS `bungeesystem_premium` " +
                "(" +
                "`id` int(11) NOT NULL AUTO_INCREMENT PRIMARY KEY, " +
                "`uuid` varchar(255) NOT NULL, " +
                "`group` varchar(355) NOT NULL, " +
                "`old_group` varchar(355) NOT NULL, " +
                "`kosten` varchar(155) NOT NULL, " +
                "`gekauft` int(50) NOT NULL, " +
                "`timestamp` int(50) NOT NULL " +
                ") ENGINE=InnoDB DEFAULT CHARSET=utf8;");

        mysql.update("CREATE TABLE IF NOT EXISTS `bungeesystem_bansystem_ban` " +
                "(" +
                "`id` int(11) AUTO_INCREMENT PRIMARY KEY, " +
                "`uuid` varchar(50) UNIQUE KEY NOT NULL, " +
                "`template` varchar(50) NOT NULL, " +
                "`reason` varchar(500) NOT NULL, " +
                "`end` int(50) NOT NULL, " +
                "`timestamp` int(50) NOT NULL, " +
                "`team_member` varchar(50)" +
                ") ENGINE=InnoDB DEFAULT CHARSET=utf8;");

        mysql.update("CREATE TABLE IF NOT EXISTS `bungeesystem_bansystem_mute` " +
                "(" +
                "`id` int(11) AUTO_INCREMENT PRIMARY KEY, " +
                "`uuid` varchar(50) UNIQUE KEY NOT NULL, " +
                "`template` varchar(10) NOT NULL, " +
                "`reason` varchar(500) NOT NULL, " +
                "`end` int(50) NOT NULL, " +
                "`timestamp` int(50) NOT NULL, " +
                "`team_member` varchar(50)" +
                ") ENGINE=InnoDB DEFAULT CHARSET=utf8;");

        mysql.update("CREATE TABLE IF NOT EXISTS `bungeesystem_bansystem_points` " +
                "(" +
                "`id` int(11) AUTO_INCREMENT PRIMARY KEY, " +
                "`uuid` varchar(50) UNIQUE KEY NOT NULL, " +
                "`banpoints` int(5) NOT NULL, " +
                "`mutepoints` int(5) NOT NULL" +
                ") ENGINE=InnoDB DEFAULT CHARSET=utf8;");

        mysql.update("CREATE TABLE IF NOT EXISTS `bungeesystem_bansystem_banhistory` " +
                "(" +
                "`id` int(11) AUTO_INCREMENT PRIMARY KEY, " +
                "`uuid` varchar(100) NOT NULL, " +
                "`template` varchar(10) NOT NULL, " +
                "`reason` varchar(500) NOT NULL, " +
                "`end` int(50) NOT NULL, " +
                "`timestamp` int(50) NOT NULL, " +
                "`team_member` varchar(50)" +
                ") ENGINE=InnoDB DEFAULT CHARSET=utf8;");

        mysql.update("CREATE TABLE IF NOT EXISTS `bungeesystem_bansystem_mutehistory` " +
                "(" +
                "`id` int(11) AUTO_INCREMENT PRIMARY KEY, " +
                "`uuid` varchar(100) NOT NULL, " +
                "`template` varchar(10) NOT NULL, " +
                "`reason` varchar(500) NOT NULL, " +
                "`end` int(50) NOT NULL, " +
                "`timestamp` int(50) NOT NULL, " +
                "`team_member` varchar(50)" +
                ") ENGINE=InnoDB DEFAULT CHARSET=utf8;");

        mysql.update("CREATE TABLE IF NOT EXISTS `bungeesystem_nicks` " +
                "( " +
                "`id` int(11) NOT NULL AUTO_INCREMENT PRIMARY KEY, " +
                "`name` varchar(16) NOT NULL UNIQUE KEY, " +
                "`texture` varchar(500) NOT NULL " +
                ") " +
                "ENGINE=InnoDB DEFAULT CHARSET=utf8;");

        mysql.update("CREATE TABLE IF NOT EXISTS `bungeesystem_chatlog` " +
                "(" +
                "`id` int(11) NOT NULL AUTO_INCREMENT PRIMARY KEY, " +
                "`uuid` varchar(100), " +
                "`nachricht` varchar(100), " +
                "`timestamp` int(100)" +
                ") ENGINE=InnoDB DEFAULT CHARSET=utf8;");

        mysql.update("CREATE TABLE IF NOT EXISTS `bungeesystem_teamspeak_icons` " +
                "(" +
                "`id` int(11) NOT NULL AUTO_INCREMENT PRIMARY KEY, " +
                "`uuid` varchar(100) NOT NULL UNIQUE KEY, " +
                "`icon_id` varchar(100) NOT NULL, " +
                "`bytes` longblob NOT NULL" +
                ") ENGINE=InnoDB DEFAULT CHARSET=utf8;");
    }

    private void loadSchedulers() {
        getProxy().getScheduler().schedule(this, new PremiumCheck(), 0, 5, TimeUnit.SECONDS);
        getProxy().getScheduler().schedule(this, new Broadcast(), 0, 15, TimeUnit.MINUTES);
        getProxy().getScheduler().schedule(this, new OnlineTime(), 0, 1, TimeUnit.MINUTES);
    }

    @Override
    public MySQL getMySQL(MySQLDatabase database) {
        switch (database) {
            case SYSTEM:
                return this.database;
            default:
                return null;
        }
    }

    @Override
    public MongoDBManager getMongoDB(eu.mcone.networkmanager.core.api.database.Database database) {
        switch (database) {
            case SYSTEM:
                return this.mongoDB;
            default:
                return null;
        }
    }

    @Override
    public eu.mcone.coresystem.api.core.mysql.MySQL getMySQL() {
        return null;
    }

    public CorePlayer getCorePlayer(ProxiedPlayer p) {
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

    public Collection<CorePlayer> getOnlineCorePlayers() {
        return corePlayers.values();
    }

    @Override
    public OfflineCorePlayer getOfflineCorePlayer(String name) throws PlayerNotResolvedException {
        return new BungeeOfflineCorePlayer(this, name);
    }

    @Override
    public void registerPlugin(CorePlugin plugin) {
        plugins.put(plugin.getPluginName(), plugin);
    }

    @Override
    public CorePlugin getPlugin(String name) {
        return plugins.getOrDefault(name, null);
    }

    public void runAsync(Runnable runnable) {
        getProxy().getScheduler().runAsync(this, runnable);
    }

}