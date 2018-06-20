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
import eu.mcone.coresystem.api.bungee.player.BungeeCorePlayer;
import eu.mcone.coresystem.api.core.player.GlobalCorePlayer;
import eu.mcone.coresystem.api.core.translation.TranslationField;
import eu.mcone.coresystem.bungee.command.*;
import eu.mcone.coresystem.bungee.friend.FriendSystem;
import eu.mcone.coresystem.bungee.listener.*;
import eu.mcone.coresystem.bungee.player.CoinsAPI;
import eu.mcone.coresystem.bungee.player.NickManager;
import eu.mcone.coresystem.bungee.runnable.Broadcast;
import eu.mcone.coresystem.bungee.runnable.OnlineTime;
import eu.mcone.coresystem.bungee.runnable.PremiumCheck;
import eu.mcone.coresystem.bungee.utils.PreferencesManager;
import eu.mcone.coresystem.bungee.utils.TeamspeakVerifier;
import eu.mcone.coresystem.core.CoreModuleCoreSystem;
import eu.mcone.coresystem.core.mysql.Database;
import eu.mcone.coresystem.core.mysql.MySQL;
import eu.mcone.coresystem.core.player.PermissionManager;
import eu.mcone.coresystem.core.player.PlayerUtils;
import eu.mcone.coresystem.core.translation.TranslationManager;
import eu.mcone.coresystem.core.util.CooldownSystem;
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
    private static boolean cloudsystemAvailable;

    private Map<String, CorePlugin> plugins;

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
    private CoinsAPI coinsAPI;
    @Getter
    private PlayerUtils playerUtils;
    @Getter
    private LabyModAPI labyModAPI;
    @Getter
    private Gson gson;

    @Getter
    private MySQL database;
    @Getter
    private Map<UUID, BungeeCorePlayer> corePlayers;

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

        sendConsoleMessage("§aInitializing MariaDB Connections...");
        createTables(database = new MySQL(Database.SYSTEM));

        cooldownSystem = new CooldownSystem();
        preferences = new PreferencesManager(database);
        playerUtils = new PlayerUtils(database);
        coinsAPI = new CoinsAPI(this);
        gson = new GsonBuilder().setPrettyPrinting().create();

        cloudsystemAvailable = checkIfCloudSystemAvailable();
        sendConsoleMessage("§7CloudSystem available: "+cloudsystemAvailable);

        sendConsoleMessage("§aLoading Translations...");
        translationManager = new TranslationManager(database);
        registerTranslations();

        sendConsoleMessage("§aLoading Permissions & Groups...");
        permissionManager = new PermissionManager("Proxy", database, gson);

        sendConsoleMessage("§aLoading FriendSystem...");
        friendSystem = new FriendSystem(database);

        sendConsoleMessage("§aInitializing LabyModAPI...");
        labyModAPI = new LabyModAPI(this);

        if (!Boolean.valueOf(System.getProperty("DisableTsQuery"))) {
            sendConsoleMessage("§aLoading TeamSpeakQuery...");
            teamspeakVerifier = new TeamspeakVerifier();
        } else {
            sendConsoleMessage("§cTeamSpeakQuery disabled by JVM Argument");
        }

        sendConsoleMessage("§aLoading MessagingSystem...");
        MsgCMD.updateToggled();

        sendConsoleMessage("§aLoading Nicksystem...");
        nickManager = new NickManager(this);

        sendConsoleMessage("§aRegistering Commands, Events & Scheduler...");
        registerCommand();
        postRegisterCommand();
        registerEvents();
        loadSchedulers();

        sendConsoleMessage("§aRegistering Plugin Messaging Channel...");
        getProxy().registerChannel("Return");

        sendConsoleMessage("§aVersion: §f" + this.getDescription().getVersion() + "§a enabled!");
    }

    public void onDisable() {
        if (teamspeakVerifier != null) teamspeakVerifier.close();
        for (ProxiedPlayer p : getProxy().getPlayers()) {
            database.update("UPDATE userinfo SET status='offline' WHERE uuid='" + p.getUniqueId() + "'");
        }

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
        getProxy().getPluginManager().registerListener(this, new ServerConnect());
        getProxy().getPluginManager().registerListener(this, new PreLogin());
        getProxy().getPluginManager().registerListener(this, new PlayerDisconnect());
        getProxy().getPluginManager().registerListener(this, new TabComplete());
        getProxy().getPluginManager().registerListener(this, new ServerKick());
        getProxy().getPluginManager().registerListener(this, new ServerSwitch());
        getProxy().getPluginManager().registerListener(this, new PluginMessage());
    }

    private void registerTranslations() {
        translationManager.insertIfNotExists(
                new HashMap<String, TranslationField>() {{
                    //Prefix
                    put("system.prefix", new TranslationField("§8[§7§l!§8]§f System §8» §7"));
                    put("system.prefix.server", new TranslationField("§8[§7§l!§8]§f Server §8» §7"));
                    put("system.prefix.party", new TranslationField("§8[§7§l!§8] §5Party §8» §7"));
                    put("system.prefix.friend", new TranslationField("§8[§7§l!§8] §9Freunde §8» §7"));

                    //Error
                    put("system.error", new TranslationField("§4Es ist ein Fehler aufgetreten."));

                    //Server
                    put("system.server.lobby", new TranslationField("§7Du wirst zur §fLobby §7gesendet."));
                    put("system.server.alreadyonthisserver", new TranslationField("§4Du befindest dich bereits auf diesem Server!"));

                    //Command
                    put("system.command.noperm", new TranslationField("§4Du hast keine Berechtigung für diesen Befehl!"));
                    put("system.command.wronguse", new TranslationField("§4Diese Befehlsstruktur existiert nicht!"));
                    put("system.command.consolesender", new TranslationField("§4Nur ein Spieler kann diesen Befehl ausführen!"));

                    //Player
                    put("system.player.notonline", new TranslationField("§4Dieser Spieler ist nicht online!"));

                    //ProxyPing-Wartung
                    put("system.bungee.ping", new TranslationField(
                            "§f§lMCONE.EU §3Minigamenetzwerk §8» §f§lMC 1.12 §7§o[1.8 PVP]" +
                                    "\n§7§oDein Nummer 1 Minecraftnetzwerk"
                    ));
                    put("system.bungee.ping.maintenance", new TranslationField(
                            "§f§lMCONE.EU §3Minigamenetzwerk §8» §f§lMC 1.12 §7§o[1.8 PVP]" +
                                    "\n§4§oWir führen gerade Wartungsarbeiten durch."
                    ));
                    put("system.bungee.ping.outdated", new TranslationField(
                            "§f§lMCONE.EU §3Minigamenetzwerk §8» §c§lMC 1.12 §7§o[1.8 PVP]" +
                                    "\n§f§oWir empfehlen LabyMod für 1.12 (mcone.eu/launcher)!"
                    ));
                    put("system.bungee.ping.cracked", new TranslationField(
                            "§f§lMCONE.EU §3Minigamenetzwerk §8» §f§lMC 1.12 §7§o[Online]" +
                                    "\n§4Du benutzt keinen gekauften Minecraftaccount!"
                    ));

                    //Post-Login
                    put("system.bungee.kick.maintenance", new TranslationField(
                            "§f§lMC ONE §3Minecraftnetzwerk" +
                                    "\n§4§oWir führen gerade Wartungsarbeiten durch" +
                                    "\n§r" +
                                    "\n§7Mehr Infos findest du auf §fstatus.mcone.eu§7."
                    ));

                    //Restart
                    put("system.bungee.kick.restart", new TranslationField(
                            "\u00A7f\u00A7lMC ONE\u00A7r \u00A73Minecraftnetzwerk\n\u00A77\u00A7r" +
                                    "\n\u00A77Der Netzwerk Server startet neu.\u00A7r" +
                                    "\n\u00A77\u00A7oDies sollte nicht l\u00E4nger als ein paar Sekunden dauern."
                    ));

                    //Chat
                    put("system.bungee.chat.filter", new TranslationField("§4Bitte achte auf deine Ausdrucksweise!"));
                    put("system.bungee.chat.private.dontsee", new TranslationField("§2Du hast private Nachrichten deaktiviert!"));
                    put("system.bungee.chat.private.see", new TranslationField("§2Du hast private Nachrichten wieder aktiviert!"));
                    put("system.bungee.chat.private.fromme", new TranslationField("§8[§7§l!§8] §fMSG §8| §3Du §7-> §f%Msg-Target% §8» §7"));
                    put("system.bungee.chat.private.tome", new TranslationField("§8[§7§l!§8] §fMSG §8| §f%Msg-Player% §7-> §3Dir §8» §7"));
                    put("system.bungee.chat.team", new TranslationField("§8[§7§l!§8] §fTeamchat §8| %playername% §8» §7"));

                    //Commands
                    put("system.bungee.command.premium", new TranslationField(
                            "§8§m----------------§r§8§m| §6Premium §8§m|----------------" +
                                    "\n§7Du möchtest uns unterstützen und dir dafür ein paar ingame Coins verdienen? Dann ist der §6Premium §7oder §6Premium+ §7Rang auf MC ONE die richtige Wahl." +
                                    "\n%button%" +
                                    "\n§8§m----------------§r§8§m| §6Premium §8§m|----------------"
                    ));
                    put("system.bungee.command.bug", new TranslationField(
                            "§8§m----------------§r§8§m| §cBug §8§m|----------------" +
                                    "\n§7Du hast einen §cBug §7gefunden und möchtest uns helfen in zu fixen?" +
                                    "\n%button%" +
                                    "\n§8§m----------------§r§8§m| §cBug §8§m|----------------"
                    ));
                    put("system.bungee.command.yt", new TranslationField(
                            "§8§m----------------§r§8§m| §5Youtuber §8§m|----------------" +
                                    "\n§7Für den YouTuber Rang benötigst du mindestens §52 Tausend §7Abonennten. Für alle weiteren Infos und Vereinbarungen stehen dir die Admins zu Verfügung. Um den YouTuber Rang behalten zu dürfen musst du abhängig von deiner Abonenntenzahl §5Lets Plays auf MC ONE hochladen§7. " +
                                    "\n§r" +
                                    "\n§7Falls du die Anforderungen nicht erfüllst steht dir der §6Premium+ §7Rang ab 500 Abos kostenlos zu Verfügung." +
                                    "\n§r" +
                                    "\n%button%" +
                                    "\n§8§m----------------§r§8§m| §5Youtuber §8§m|----------------"
                    ));
                    put("system.bungee.command.vote", new TranslationField(
                            "§8§m----------------§r§8§m| §5Vote §8§m|----------------" +
                                    "\n§7Für ein Vote erhälst du §620 §7Coins." +
                                    "\n%button%" +
                                    "\n§8§m----------------§r§8§m| §5Vote §8§m|----------------"
                    ));
                    put("system.bungee.command.apply", new TranslationField(
                            "§8§m----------------§r§8§m| §fBewerben §8§m|----------------" +
                                    "\n§7Wir suchen im Moment Bewerber aus den Bereichen §b§lEntwicklung§7, §e§lBuilding§7 und §2§lSupporting§7." +
                                    "\n%button%" +
                                    "\n§8§m----------------§r§8§m| §fBewerben §8§m|----------------"
                    ));
                    put("system.bungee.command.ts", new TranslationField(
                            "§8§m----------------§r§8| §3Teamspeak §8§m|----------------" +
                                    "\n§7Unseren TeamSpeak erreichst du über die IP §fts.mcone.eu§7." +
                                    "\n%button%" +
                                    "\n§8§m----------------§r§8| §3Teamspeak §8§m|----------------"));
                    put("system.bungee.command.team", new TranslationField(
                            "§8§m----------------§r§8| §bTeam §8§m|----------------" +
                                    "\n§7Unsere aktuellen Teammitglieder findest du auf unserer Homepage" +
                                    "\n%button%" +
                                    "\n§8§m----------------§r§8| §bTeam §8§m|----------------"
                    ));
                    put("system.bungee.command.rules", new TranslationField(
                            "§8§m----------------§r§8| §cRegeln §8§m|----------------" +
                                    "\n§7Mit dem Spielen auf MC ONE akzeptierst du unsere Regeln " +
                                    "\nund erklärst dich damit einverstanden sie einzuhalten!" +
                                    "\n%button%" +
                                    "\n§8§m----------------§r§8| §cRegeln §8§m|----------------"
                    ));
                    put("system.bungee.command.help", new TranslationField(
                            "§8§m----------------|§r §f§lMC ONE §3Hilfe §8§m|----------------" +
                                    "\n§7» §f/friends §8- §7Verwalte deine Freunde auf MC ONE" +
                                    "\n§7» §f/party §8- §7Erstelle deine Party mit deinen Freunden" +
                                    "\n§7» §f/msg §8- §7Schreibe anderen Spielern Privatnachrichten" +
                                    "\n§7» §f/lobby §8- §7Teleportiert dich zurück zur Lobby Spielmodiauswahl" +
                                    "\n§7» §f/report §8- §7Reporte Spieler die gegen unsere Regeln verstoßen" +
                                    "\n§7» §f/regeln §8- §7Hier findest du den Link zu unseren Regeln" +
                                    "\n§7» §f/vote §8- §7Mit deisem Befehl kannst du für MC ONE Voten" +
                                    "\n§7» §f/register §8- §7Registriert dich auf der MC ONE Homepage" +
                                    "\n§7» §f/forgotpass §8- §7Lässt dich dein Passwort auf der Homepage ändern" +
                                    "\n§8§m----------------|§r §f§lMC ONE §3Hilfe §8§m|----------------"
                    ));

                    //Broadcast
                    put("system.bungee.broadcast1", new TranslationField("" +
                            "\n§8[§7§l!§8] §7Du möchtest als §a§lSupporter§7, §e§lBuilder§7 oder §b§lDeveloper§7" +
                            "\n§8[§7§l!§8] §7 dem Team beitreten?" +
                            "\n§8[§7§l!§8] §7Dann bewirb dich über unsere Homepage!" +
                            "\n§8[§7§l!§8] §7Alle Infos über §f/bewerben" +
                            "\n"));
                    put("system.bungee.broadcast2", new TranslationField("" +
                            "\n§8[§7§l!§8] §7Bleibe immer auf dem neuesten Stand über unsere Homepage" +
                            "\n§8[§7§l!§8] §fhttps://www.mcone.eu" +
                            "\n§8[§7§l!§8] §7Registriere dich um Blog Posts liken und kommentieren zu" +
                            "\n§8[§7§l!§8] §7können. §3/register" +
                            "\n"));
                    put("system.bungee.broadcast3", new TranslationField("" +
                            "\n§8[§7§l!§8] §7Du hast einen Spieler gesehen der gegen die Regeln" +
                            "\n§8[§7§l!§8] §7verstößt?" +
                            "\n§8[§7§l!§8] §7Reporte ihn mit §c/report" +
                            "\n"));
                    put("system.bungee.broadcast4", new TranslationField("" +
                            "\n§8[§7§l!§8] §7Mit dem Betreten des MC ONE Netzwerks akzeptierst" +
                            "\n§8[§7§l!§8] §7du unsere Regeln." +
                            "\n§8[§7§l!§8] §7Alle Infos dazu findest du auf §3https://www.mcone.eu/regeln" +
                            "\n"));
                    put("system.bungee.broadcast5", new TranslationField("" +
                            "\n§8[§7§l!§8] §7Supporte uns auf allen bekannten sozialen Netzwerken" +
                            "\n§8[§7§l!§8] §7mit dem Nutzernamen §f@mconeeu" +
                            "\n§8[§7§l!§8] §b§lTwitter§7, §9§lFacebook§7 oder §c§lYouTube" +
                            "\n"));
                }}
        );
    }

    private void createTables(MySQL mysql) {
        mysql.update("CREATE TABLE IF NOT EXISTS `userinfo` " +
                "( " +
                "`id` int(11) NOT NULL AUTO_INCREMENT PRIMARY KEY, " +
                "`uuid` varchar(100) NOT NULL UNIQUE KEY, " +
                "`name` varchar(20) NOT NULL, " +
                "`groups` varchar(20), " +
                "`coins` int(100), " +
                "`about` varchar(500), " +
                "`status` varchar(200), " +
                "`email` varchar(100), " +
                "`ip` varchar(100), " +
                "`timestamp` varchar(100), " +
                "`password` varchar(100), " +
                "`onlinetime` int(10) NOT NULL, " +
                "`teamspeak_uid` varchar(100), " +
                "`msg_toggle` boolean" +
                ") ENGINE=InnoDB DEFAULT CHARSET=utf8;");

        mysql.update("CREATE TABLE IF NOT EXISTS `translations` " +
                "(" +
                "`id` int(11) NOT NULL AUTO_INCREMENT PRIMARY KEY, " +
                "`key` varchar(100) NOT NULL UNIQUE KEY, " +
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

        mysql.update("CREATE TABLE IF NOT EXISTS `bungeesystem_permissions` " +
                "(" +
                "`id` int(11) NOT NULL AUTO_INCREMENT PRIMARY KEY, " +
                "`name` varchar(100) NOT NULL, " +
                "`key` varchar(100) NOT NULL, " +
                "`value` varchar(1000), " +
                "`server` varchar(100)" +
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
                "`uuid` varchar(100), " +
                "`icon_id` varchar(100)" +
                ") ENGINE=InnoDB DEFAULT CHARSET=utf8;");
    }

    private void loadSchedulers() {
        getProxy().getScheduler().schedule(this, new PremiumCheck(), 0, 5, TimeUnit.SECONDS);
        getProxy().getScheduler().schedule(this, new Broadcast(), 0, 15, TimeUnit.MINUTES);
        getProxy().getScheduler().schedule(this, new OnlineTime(), 0, 1, TimeUnit.MINUTES);
    }

    @Override
    public MySQL getMySQL(Database database) {
        switch (database) {
            case SYSTEM:
                return this.database;
            default:
                return null;
        }
    }

    @Override
    public eu.mcone.coresystem.api.core.mysql.MySQL getMySQL() {
        return null;
    }

    public BungeeCorePlayer getCorePlayer(ProxiedPlayer p) {
        return corePlayers.getOrDefault(p.getUniqueId(), null);
    }

    public BungeeCorePlayer getCorePlayer(UUID uuid) {
        return corePlayers.getOrDefault(uuid, null);
    }

    public BungeeCorePlayer getCorePlayer(String name) {
        for (BungeeCorePlayer p : corePlayers.values()) {
            if (p.getName().equals(name)) return p;
        }
        return null;
    }

    @Override
    public GlobalCorePlayer getGlobalCorePlayer(UUID uuid) {
        return getCorePlayer(uuid);
    }

    public Collection<BungeeCorePlayer> getOnlineCorePlayers() {
        return corePlayers.values();
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