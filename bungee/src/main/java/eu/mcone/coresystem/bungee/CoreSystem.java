/*
 * Copyright (c) 2017 - 2018 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.coresystem.bungee;

import eu.mcone.coresystem.bungee.command.*;
import eu.mcone.coresystem.bungee.friend.FriendSystem;
import eu.mcone.coresystem.bungee.listener.*;
import eu.mcone.coresystem.bungee.player.CorePlayer;
import eu.mcone.coresystem.bungee.player.NickManager;
import eu.mcone.coresystem.bungee.runnable.Broadcast;
import eu.mcone.coresystem.bungee.runnable.OnlineTime;
import eu.mcone.coresystem.bungee.runnable.PremiumCheck;
import eu.mcone.coresystem.bungee.utils.CooldownSystem;
import eu.mcone.coresystem.bungee.utils.Messager;
import eu.mcone.coresystem.lib.mysql.MySQL;
import eu.mcone.coresystem.lib.mysql.MySQL_Config;
import eu.mcone.coresystem.lib.player.PermissionManager;
import lombok.Getter;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Plugin;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class CoreSystem extends Plugin{

    @Getter
	private static CoreSystem instance;
	final public static String MainPrefix = "§8[§3BungeeCore§8] ";

	public static MySQL mysql1;
	private static MySQL mysql2;
	public static MySQL_Config sqlconfig;

	@Getter
	private static Map<UUID, CorePlayer> corePlayers;

	@Getter
	private PermissionManager permissionManager;
	@Getter
	private CooldownSystem cooldownSystem;
	@Getter
	private FriendSystem friendSystem;
	@Getter
	private NickManager nickManager;

	public void onEnable(){
        instance = this;
        cooldownSystem = new CooldownSystem();
        corePlayers = new HashMap<>();

        Messager.console("\n"+
				"      __  _____________  _   ________                                                          \n" +
				"     /  |/  / ____/ __ \\/ | / / ____/                                                          \n" +
				"    / /|_/ / /   / / / /  |/ / __/                                                             \n" +
				"   / /  / / /___/ /_/ / /|  / /___                                                             \n" +
				"  /_/ _/_/\\____/\\____/_/ |_/_____/      ______               _____            __               \n" +
				"     / __ )__  ______  ____ ____  ___  / ____/___  ________ / ___/__  _______/ /____  ____ ___ \n" +
				"    / __  / / / / __ \\/ __ `/ _ \\/ _ \\/ /   / __ \\/ ___/ _ \\\\__ \\/ / / / ___/ __/ _ \\/ __ `__ \\\n" +
				"   / /_/ / /_/ / / / / /_/ /  __/  __/ /___/ /_/ / /  /  __/__/ / /_/ (__  ) /_/  __/ / / / / /\n" +
				"  /_____/\\__,_/_/ /_/\\__, /\\___/\\___/\\____/\\____/_/   \\___/____/\\__, /____/\\__/\\___/_/ /_/ /_/ \n" +
				"                    /____/                                     /____/\n");

        Messager.console(MainPrefix + "§aMySQL Verbindungen werden initialisiert...");
		mysql1 = new MySQL("78.46.249.195", 3306, "mc1system", "mc1system", "6THk8uDbTtDKf8yUMf2r62MHMZ57EVMBFkMDEgFqz9YF8prKug2q9DXLvTJZEmsa");
		mysql2 = new MySQL("78.46.249.195", 3306, "mc1config", "mc1config", "q%sZp=6/_wx2M2B.Qzaeya4Kd5;f4W*w*M?3#kM,QPjv6VuG3=TjTJ63CPD)}WV;");

        createTables(mysql1);

		Messager.console(MainPrefix + "§aMySQL Config wird initiiert...");
		sqlconfig = new MySQL_Config(mysql2, "BungeeSystem", 5000);
		registerMySQLConfig();

		Messager.console(MainPrefix + "§aPermissions werden geladen...");
		permissionManager = new PermissionManager("Proxy", mysql1);

        Messager.console(MainPrefix + "§aFreunde System wird geladen...");
		friendSystem = new FriendSystem(mysql1);

		Messager.console(MainPrefix + "§aNachrichten System wird geladen...");
		MsgCMD.updateToggled();

		Messager.console(MainPrefix + "§aNicksystem wird geladen...");
		nickManager = new NickManager(mysql1);

		Messager.console(MainPrefix + "§aBefehle, Events und Scheduler werden registriert...");
	    registerCommand();
        postRegisterCommand();
		registerEvents();
		loadSchedulers();

		Messager.console(MainPrefix + "§aMC ONE Messaging Channel werden registriert...");
		ProxyServer.getInstance().registerChannel("Return");

		Messager.console(MainPrefix + "§aVersion: §f" + this.getDescription().getVersion()+ "§a wurde aktiviert!");
	}

	public void onDisable(){
	    for (ProxiedPlayer p : ProxyServer.getInstance().getPlayers()) {
	        mysql1.update("UPDATE userinfo SET status='offline' WHERE uuid='" + p.getUniqueId() + "'");
        }

    	mysql1.close();
    	mysql2.close();
		Messager.console(MainPrefix+"§cPlugin wurde Deaktiviert!");
	}

    private void registerCommand(){
        ProxyServer.getInstance().getPluginManager().registerCommand(this, new PingCMD());
        ProxyServer.getInstance().getPluginManager().registerCommand(this, new TeamChatCMD());
		ProxyServer.getInstance().getPluginManager().registerCommand(this, new PermsCMD());
        ProxyServer.getInstance().getPluginManager().registerCommand(this, new BanCMD());
        ProxyServer.getInstance().getPluginManager().registerCommand(this, new WhoisCMD());
        ProxyServer.getInstance().getPluginManager().registerCommand(this, new RestartCMD());
        ProxyServer.getInstance().getPluginManager().registerCommand(this, new WartungCMD());
        ProxyServer.getInstance().getPluginManager().registerCommand(this, new CoinsCMD());

        ProxyServer.getInstance().getPluginManager().registerCommand(this, new NickCMD());
        ProxyServer.getInstance().getPluginManager().registerCommand(this, new UnnickCMD());

        ProxyServer.getInstance().getPluginManager().registerCommand(this, new LobbyCMD());
        ProxyServer.getInstance().getPluginManager().registerCommand(this, new JumpCMD());

        ProxyServer.getInstance().getPluginManager().registerCommand(this, new FriendCMD());
        ProxyServer.getInstance().getPluginManager().registerCommand(this, new PartyCMD());
        ProxyServer.getInstance().getPluginManager().registerCommand(this, new MsgCMD());
        ProxyServer.getInstance().getPluginManager().registerCommand(this, new ReplyCMD());
        ProxyServer.getInstance().getPluginManager().registerCommand(this, new ReportCMD());
        ProxyServer.getInstance().getPluginManager().registerCommand(this, new HelpCMD());
        ProxyServer.getInstance().getPluginManager().registerCommand(this, new BungeecordCMD());
        ProxyServer.getInstance().getPluginManager().registerCommand(this, new RegisterCMD());
        ProxyServer.getInstance().getPluginManager().registerCommand(this, new ForgotpassCMD());
        ProxyServer.getInstance().getPluginManager().registerCommand(this, new ChatlogCMD());
        ProxyServer.getInstance().getPluginManager().registerCommand(this, new RegelnCMD());

        ProxyServer.getInstance().getPluginManager().registerCommand(this, new PremiumCMD());
        ProxyServer.getInstance().getPluginManager().registerCommand(this, new YoutubeCMD());
        ProxyServer.getInstance().getPluginManager().registerCommand(this, new TsCMD());
        ProxyServer.getInstance().getPluginManager().registerCommand(this, new VoteCMD());
        ProxyServer.getInstance().getPluginManager().registerCommand(this, new BewerbenCMD());
        ProxyServer.getInstance().getPluginManager().registerCommand(this, new TeamCMD());
        ProxyServer.getInstance().getPluginManager().registerCommand(this, new BugreportCMD());
        ProxyServer.getInstance().getPluginManager().registerCommand(this, new RegelnCMD());
    }

    private void postRegisterCommand() {
		ProxyServer.getInstance().getScheduler().schedule(
				getInstance(),
				() -> ProxyServer.getInstance().getPluginManager().registerCommand(this, new ServerCMD()),
				1,
                TimeUnit.SECONDS
		);
	}
    
    private void registerEvents(){
    	ProxyServer.getInstance().getPluginManager().registerListener(this, new Chat());
		ProxyServer.getInstance().getPluginManager().registerListener(this, new CoinsChange());
		ProxyServer.getInstance().getPluginManager().registerListener(this, new PermissionChange());
        ProxyServer.getInstance().getPluginManager().registerListener(this, new PermissionCheck());
		ProxyServer.getInstance().getPluginManager().registerListener(this, new PostLogin());
        ProxyServer.getInstance().getPluginManager().registerListener(this, new ProxyPing());
		ProxyServer.getInstance().getPluginManager().registerListener(this, new ServerConnect());
        ProxyServer.getInstance().getPluginManager().registerListener(this, new PreLogin());
        ProxyServer.getInstance().getPluginManager().registerListener(this, new PlayerDisconnect());
        ProxyServer.getInstance().getPluginManager().registerListener(this, new TabComplete());
		ProxyServer.getInstance().getPluginManager().registerListener(this, new ServerKick());
		ProxyServer.getInstance().getPluginManager().registerListener(this, new ServerSwitch());
        ProxyServer.getInstance().getPluginManager().registerListener(this, new PluginMessage());
    }

	private void registerMySQLConfig(){
		//create table
		sqlconfig.createTable();

		//System
		sqlconfig.insertMySQLConfig("System-Prefix", "§8[§7§l!§8] §fSystem §8» §7");
		sqlconfig.insertMySQLConfig("System-Report-Cooldown", 1);

		sqlconfig.insertMySQLConfig("System-Server-Lobby", "Lobby");
		sqlconfig.insertMySQLConfig("System-Server-Build", "Build");
		sqlconfig.insertMySQLConfig("System-Connect-Lobby", "§7Du wirst zur §fLobby §7gesendet.");
		sqlconfig.insertMySQLConfig("System-Already-Lobby", "§4Du bist bereits auf der Lobby.");
		sqlconfig.insertMySQLConfig("System-Connect-Test", "§7Du wirst auf den §fTest-Server §7gesendet.");
		sqlconfig.insertMySQLConfig("System-Already-Test", "§4Du bist bereits auf dem Test-Server.");
		sqlconfig.insertMySQLConfig("System-Connect-Build", "§7Du wirst auf den §6Build-Server §7gesendet.");
		sqlconfig.insertMySQLConfig("System-Already-Build", "§4Du bist bereits auf dem Build-Server.");

		sqlconfig.insertMySQLConfig("System-NoPerm", "§4Du hast keine Berechtigung für diesen Befehl!");
		sqlconfig.insertMySQLConfig("System-WrongUse", "§4Bitte §cbenutze.");
		sqlconfig.insertMySQLConfig("System-No-Online-Player", "§4Dieser Spieler ist nicht online!");
		sqlconfig.insertMySQLConfig("System-Konsolen-Sender", "§4Nur ein Spieler kann diesen Befehl ausführen!");

		//BetaKey-System
        sqlconfig.insertMySQLConfig("BetaKey-System", false);

		//Wartungs-Modus
		sqlconfig.insertMySQLConfig("Wartungs-Modus", true);

        //ProxyPing-Wartung
        sqlconfig.insertMySQLConfig("ProxyPing-Motd", "§f§lMCONE.EU §3Minigamenetzwerk §8» §f§lMC 1.12 §7§o[1.8 PVP]" +
                "\n§7§oDein Nummer 1 Minecraftnetzwerk");
		sqlconfig.insertMySQLConfig("ProxyPing-Wartung-Motd", "§f§lMCONE.EU §3Minigamenetzwerk §8» §f§lMC 1.12 §7§o[1.8 PVP]" +
				"\n§4§oWir führen gerade Wartungsarbeiten durch.");
		sqlconfig.insertMySQLConfig("ProxyPing-Protocol-Motd", "§f§lMCONE.EU §3Minigamenetzwerk §8» §c§lMC 1.12 §7§o[1.8 PVP]" +
				"\n§f§oWir empfehlen LabyMod für 1.12 (mcone.eu/launcher)!");
        sqlconfig.insertMySQLConfig("ProxyPing-Offline-Motd", "§f§lMCONE.EU §3Minigamenetzwerk §8» §f§lMC 1.12 §7§o[Online]" +
                "\n§4Du benutzt keinen gekauften Minecraftaccount!");

		//Post-Login
		sqlconfig.insertMySQLConfig("Wartung-KickNachricht", "§f§lMC ONE §3Minecraftnetzwerk" +
				"\n§4§oWir führen gerade Wartungsarbeiten durch" +
				"\n§r" +
				"\n§7Mehr Infos findest du auf §fstatus.mcone.eu§7.");


		//Restart
		sqlconfig.insertMySQLConfig("Restart-KickNachricht", "\u00A7f\u00A7lMC ONE\u00A7r \u00A73Minecraftnetzwerk\n\u00A77\u00A7r" +
				"\n\u00A77Der Netzwerk Server startet neu.\u00A7r" +
				"\n\u00A77\u00A7oDies sollte nicht l\u00E4nger als ein paar Sekunden dauern.");


		//Party
        sqlconfig.insertMySQLConfig("Party-Prefix", "§8[§7§l!§8] §5Party §8» §7");

		//Party
		sqlconfig.insertMySQLConfig("Friend-Prefix", "§8[§7§l!§8] §9Freunde §8» §7");

		//Chat
		sqlconfig.insertMySQLConfig("TeamChat-Prefix", "§8[§7§l!§8] §fTeamchat §8| %Playername% §8» §7");

		sqlconfig.insertMySQLConfig("Beleidigung-Lines", "§cIhre §cChatnachricht §cwurde §cgefiltert!" +
				"\n§cGrund §8: §4Mögliche Beleidigung");

		sqlconfig.insertMySQLConfig("ToggleMsg-NoSee", "§7Du §7kannt §7nun §bkeine §7Private-Nachrichten §7mehr §7Sehen.");
		sqlconfig.insertMySQLConfig("ToggleMsg-See", "§7Du §7kannt §7nun §bwieder alle §7Private-Nachrichten §7sehen.");

		sqlconfig.insertMySQLConfig("Msg-Target", "§8[§7§l!§8] §fMSG §8| §3Du §7-> §f%Msg-Target% §8» §7");
		sqlconfig.insertMySQLConfig("Msg-Player", "§8[§7§l!§8] §fMSG §8| §f%Msg-Player% §7-> §3Dir §8» §7");


		//Commands
        sqlconfig.insertMySQLConfig("CMD-Premium", "§8§m----------------§r§8§m| §6Premium §8§m|----------------" +
                "\n§7Du möchtest uns unterstützen und dir dafür ein paar ingame Coins verdienen? Dann ist der §6Premium §7oder §6Premium+ §7Rang auf MC ONE die richtige Wahl." +
                "\n%button%" +
                "\n§8§m----------------§r§8§m| §6Premium §8§m|----------------");

        sqlconfig.insertMySQLConfig("CMD-Bug", "§8§m----------------§r§8§m| §cBug §8§m|----------------" +
                "\n§7Du hast einen §cBug §7gefunden und möchtest uns helfen in zu fixen?" +
                "\n%button%" +
                "\n§8§m----------------§r§8§m| §cBug §8§m|----------------");

        sqlconfig.insertMySQLConfig("CMD-YT", "§8§m----------------§r§8§m| §5Youtuber §8§m|----------------" +
                "\n§7Für den YouTuber Rang benötigst du mindestens §52 Tausend §7Abonennten. Für alle weiteren Infos und Vereinbarungen stehen dir die Admins zu Verfügung. Um den YouTuber Rang behalten zu dürfen musst du abhängig von deiner Abonenntenzahl §5Lets Plays auf MC ONE hochladen§7. " +
                "\n§r" +
                "\n§7Falls du die Anforderungen nicht erfüllst steht dir der §6Premium+ §7Rang ab 500 Abos kostenlos zu Verfügung." +
                "\n§r" +
                "\n%button%" +
                "\n§8§m----------------§r§8§m| §5Youtuber §8§m|----------------");

        sqlconfig.insertMySQLConfig("CMD-Vote", "§8§m----------------§r§8§m| §5Vote §8§m|----------------" +
                "\n§7Für ein Vote erhälst du §620 §7Coins." +
                "\n%button%" +
                "\n§8§m----------------§r§8§m| §5Vote §8§m|----------------" );

        sqlconfig.insertMySQLConfig("CMD-Bewerben", "§8§m----------------§r§8§m| §fBewerben §8§m|----------------" +
                "\n§7Wir suchen im Moment Bewerber aus den Bereichen §b§lEntwicklung§7, §e§lBuilding§7 und §2§lSupporting§7." +
                "\n%button%" +
                "\n§8§m----------------§r§8§m| §fBewerben §8§m|----------------" );

        sqlconfig.insertMySQLConfig("CMD-Ts", "§8§m----------------§r§8| §3Teamspeak §8§m|----------------" +
                "\n§7Unseren TeamSpeak erreichst du über die IP §fts.mcone.eu§7." +
                "\n%button%" +
                "\n§8§m----------------§r§8| §3Teamspeak §8§m|----------------" );

        sqlconfig.insertMySQLConfig("CMD-Team", "§8§m----------------§r§8| §bTeam §8§m|----------------" +
                "\n§7Unsere aktuellen Teammitglieder findest du auf unserer Homepage" +
                "\n%button%" +
                "\n§8§m----------------§r§8| §bTeam §8§m|----------------" );

        sqlconfig.insertMySQLConfig("CMD-Regeln", "§8§m----------------§r§8| §cRegeln §8§m|----------------" +
                "\n§7Mit dem Spielen auf MC ONE akzeptierst du unsere Regeln " +
                "\nund erklärst dich damit einverstanden sie einzuhalten!" +
                "\n%button%" +
                "\n§8§m----------------§r§8| §cRegeln §8§m|----------------" );


        sqlconfig.insertMySQLConfig("Help-Lines", "§8§m----------------|§r §f§lMC ONE §3Hilfe §8§m|----------------" +
				"\n§7» §f/friends §8- §7Verwalte deine Freunde auf MC ONE" +
				"\n§7» §f/party §8- §7Erstelle deine Party mit deinen Freunden" +
				"\n§7» §f/msg §8- §7Schreibe anderen Spielern Privatnachrichten" +
				"\n§7» §f/lobby §8- §7Teleportiert dich zurück zur Lobby Spielmodiauswahl" +
				"\n§7» §f/report §8- §7Reporte Spieler die gegen unsere Regeln verstoßen" +
				"\n§7» §f/regeln §8- §7Hier findest du den Link zu unseren Regeln" +
				"\n§7» §f/vote §8- §7Mit deisem Befehl kannst du für MC ONE Voten" +
				"\n§7» §f/register §8- §7Registriert dich auf der MC ONE Homepage" +
                "\n§7» §f/forgotpass §8- §7Lässt dich dein Passwort auf der Homepage ändern" +
				"\n§8§m----------------|§r §f§lMC ONE §3Hilfe §8§m|----------------");


		//Broadcast
		sqlconfig.insertMySQLConfig("bc1", String.valueOf("" +
				"\n§8[§7§l!§8] §7Du möchtest als §a§lSupporter§7, §e§lBuilder§7 oder §b§lDeveloper§7" +
				"\n§8[§7§l!§8] §7 dem Team beitreten?" +
				"\n§8[§7§l!§8] §7Dann bewirb dich über unsere Homepage!" +
				"\n§8[§7§l!§8] §7Alle Infos über §f/bewerben" +
				"\n"));
		sqlconfig.insertMySQLConfig("bc2", String.valueOf("" +
				"\n§8[§7§l!§8] §7Bleibe immer auf dem neuesten Stand über unsere Homepage" +
				"\n§8[§7§l!§8] §fhttps://www.mcone.eu" +
				"\n§8[§7§l!§8] §7Registriere dich um Blog Posts liken und kommentieren zu" +
				"\n§8[§7§l!§8] §7können. §3/register" +
				"\n"));
		sqlconfig.insertMySQLConfig("bc3", String.valueOf("" +
				"\n§8[§7§l!§8] §7Du hast einen Spieler gesehen der gegen die Regeln" +
				"\n§8[§7§l!§8] §7verstößt?" +
				"\n§8[§7§l!§8] §7Reporte ihn mit §c/report" +
				"\n"));
		sqlconfig.insertMySQLConfig("bc4", String.valueOf("" +
				"\n§8[§7§l!§8] §7Mit dem Betreten des MC ONE Netzwerks akzeptierst" +
				"\n§8[§7§l!§8] §7du unsere Regeln." +
				"\n§8[§7§l!§8] §7Alle Infos dazu findest du auf §3https://www.mcone.eu/regeln" +
				"\n"));
		sqlconfig.insertMySQLConfig("bc5", String.valueOf("" +
				"\n§8[§7§l!§8] §7Supporte uns auf allen bekannten sozialen Netzwerken" +
				"\n§8[§7§l!§8] §7mit dem Nutzernamen §f@mconeeu" +
				"\n§8[§7§l!§8] §b§lTwitter§7, §9§lFacebook§7 oder §c§lYouTube" +
				"\n"));

		//store
		sqlconfig.store();
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
				"`msg_toggle` boolean" +
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
				"`uuid` varchar(200), `nachricht` varchar(100), " +
				"`timestamp` int(100)" +
				") ENGINE=InnoDB DEFAULT CHARSET=utf8;");
	}
    
    private void loadSchedulers() {
        ProxyServer.getInstance().getScheduler().schedule(this, new PremiumCheck(), 0, 5, TimeUnit.SECONDS);
        ProxyServer.getInstance().getScheduler().schedule(this, new Broadcast(), 0, 15, TimeUnit.MINUTES);
        ProxyServer.getInstance().getScheduler().schedule(this, new OnlineTime(), 0, 1, TimeUnit.MINUTES);
    }

	public static CorePlayer getCorePlayer(ProxiedPlayer p) {
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

}