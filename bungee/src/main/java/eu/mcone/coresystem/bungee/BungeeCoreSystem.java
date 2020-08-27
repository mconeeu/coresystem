/*
 * Copyright (c) 2017 - 2020 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.bungee;

import com.google.gson.Gson;
import com.google.gson.JsonParser;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoDatabase;
import eu.mcone.coresystem.api.bungee.CorePlugin;
import eu.mcone.coresystem.api.bungee.CoreSystem;
import eu.mcone.coresystem.api.bungee.event.MoneyChangeEvent;
import eu.mcone.coresystem.api.bungee.player.CorePlayer;
import eu.mcone.coresystem.api.bungee.player.OfflineCorePlayer;
import eu.mcone.coresystem.api.core.exception.PlayerNotResolvedException;
import eu.mcone.coresystem.api.core.player.Currency;
import eu.mcone.coresystem.api.core.player.GlobalCorePlayer;
import eu.mcone.coresystem.api.core.player.PlayerState;
import eu.mcone.coresystem.api.core.translation.Language;
import eu.mcone.coresystem.bungee.command.*;
import eu.mcone.coresystem.bungee.friend.FriendSystem;
import eu.mcone.coresystem.bungee.listener.*;
import eu.mcone.coresystem.bungee.overwatch.Overwatch;
import eu.mcone.coresystem.bungee.overwatch.replay.ReplayServerSessionHandler;
import eu.mcone.coresystem.bungee.player.BungeeCorePlayer;
import eu.mcone.coresystem.bungee.player.BungeeOfflineCorePlayer;
import eu.mcone.coresystem.bungee.player.LabyModManager;
import eu.mcone.coresystem.bungee.player.NickManager;
import eu.mcone.coresystem.bungee.runnable.Broadcast;
import eu.mcone.coresystem.bungee.runnable.OnlineTime;
import eu.mcone.coresystem.bungee.runnable.PremiumCheck;
import eu.mcone.coresystem.bungee.utils.ChannelHandler;
import eu.mcone.coresystem.core.CoreModuleCoreSystem;
import eu.mcone.coresystem.core.player.PermissionManager;
import eu.mcone.coresystem.core.player.PlayerUtils;
import eu.mcone.coresystem.core.translation.TranslationManager;
import eu.mcone.coresystem.core.util.CoreCooldownSystem;
import eu.mcone.coresystem.core.util.MoneyUtil;
import eu.mcone.coresystem.core.util.PreferencesManager;
import group.onegaming.networkmanager.core.api.database.Database;
import group.onegaming.networkmanager.core.database.MongoConnection;
import lombok.Getter;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ListenerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import org.bson.UuidRepresentation;
import org.bson.codecs.UuidCodecProvider;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.pojo.Conventions;
import org.bson.codecs.pojo.PojoCodecProvider;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class BungeeCoreSystem extends CoreSystem implements CoreModuleCoreSystem {

    @Getter
    private static BungeeCoreSystem system;
    @Getter
    private boolean cloudsystemAvailable;

    private Map<String, CorePlugin> plugins;
    private MongoConnection mongoConnection;

    @Getter
    private TranslationManager translationManager;
    @Getter
    private PreferencesManager preferences;
    @Getter
    private PermissionManager permissionManager;
    @Getter
    private Overwatch overwatch;
    @Getter
    private CoreCooldownSystem cooldownSystem;
    @Getter
    private FriendSystem friendSystem;
    @Getter
    private NickManager nickManager;
    @Getter
    private ChannelHandler channelHandler;
    @Getter
    private ReplayServerSessionHandler serverSessionHandler;
    @Getter
    private PlayerUtils playerUtils;
    @Getter
    private MoneyUtil moneyUtil;
    @Getter
    private LabyModManager labyModAPI;
    @Getter
    private Gson gson;
    @Getter
    private JsonParser jsonParser;

    @Getter
    private Map<UUID, BungeeCorePlayer> corePlayers;

    public void onEnable() {
        withErrorLogging(() -> {
            system = this;
            setInstance(this);

            corePlayers = new HashMap<>();
            plugins = new HashMap<>();

            if (sentryClient != null) {
                ListenerInfo listener = getProxy().getConfig().getListeners().iterator().next();
                sentryClient.setServerName("Proxy");
                sentryClient.setRelease(getDescription().getVersion());
                sentryClient.addTag("Server version", getProxy().getVersion());
                sentryClient.addTag("Server Port", listener.getHost().getAddress().toString());
                sentryClient.addTag("Server MOTD", listener.getMotd());
                sentryClient.addTag("Server Foced Hosts", listener.getForcedHosts().toString());
                sentryClient.addTag("Plugin dependencies", getDescription().getDepends().toString());
                sentryClient.addTag("Plugin soft dependencies", getDescription().getSoftDepends().toString());
                sentryClient.addTag("Max Players", String.valueOf(listener.getMaxPlayers()));
            }

            final File file = new File("messages.properties");
            if (!file.isFile()) {
                try {
                    Field customBundle = getProxy().getClass().getDeclaredField("customBundle");

                    customBundle.setAccessible(true);
                    customBundle.set(getProxy(), new PropertyResourceBundle(getResourceAsStream("core_messages.properties")));
                } catch (NoSuchFieldException | IOException | IllegalAccessException e) {
                    e.printStackTrace();
                }
            }

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

            gson = new Gson();
            jsonParser = new JsonParser();

            mongoConnection = new MongoConnection("db.mcone.eu", "admin", "Ze7OCxrVI30wmJU38TX9UmpoL8RnLPogmV3sIljcD2HQkth86bzr6JRiaDxabdt8", "admin", 27017)
                    .codecRegistry(
                            MongoClientSettings.getDefaultCodecRegistry(),
                            CodecRegistries.fromProviders(
                                    new UuidCodecProvider(UuidRepresentation.JAVA_LEGACY),
                                    PojoCodecProvider.builder().conventions(Conventions.DEFAULT_CONVENTIONS).automatic(true).build()
                            )
                    )
                    .connect();

            cooldownSystem = new CoreCooldownSystem(this);
            channelHandler = new ChannelHandler();
            preferences = new PreferencesManager(getMongoDB(), new HashMap<String, Object>() {{
                put("maintenance", false);
                put("betaKeySystem", false);
            }});

            serverSessionHandler = new ReplayServerSessionHandler();
            playerUtils = new PlayerUtils(this);
            moneyUtil = new MoneyUtil(this, getMongoDB()) {
                @Override
                protected void fireEvent(GlobalCorePlayer player, Currency currency) {
                    getProxy().getPluginManager().callEvent(new MoneyChangeEvent((CorePlayer) player, currency));
                }
            };

            cloudsystemAvailable = checkIfCloudSystemAvailable();
            sendConsoleMessage("§7CloudSystem available: " + cloudsystemAvailable);

            sendConsoleMessage("§aLoading Translations...");
            translationManager = new TranslationManager(this, "bungeesystem");
            translationManager.loadAdditionalLanguages(Language.values());
            translationManager.loadAdditionalCategories("bukkitsystem");

            sendConsoleMessage("§aLoading Permissions & Groups...");
            permissionManager = new PermissionManager("Proxy", getMongoDB());

            sendConsoleMessage("§aLoading Overwatch §aSystem...");
            overwatch = new Overwatch(this);

            sendConsoleMessage("§aLoading FriendSystem...");
            friendSystem = new FriendSystem();

            sendConsoleMessage("§aInitializing LabyModManager...");
            labyModAPI = new LabyModManager(this);

            sendConsoleMessage("§aLoading Nicksystem...");
            nickManager = new NickManager(this);

            sendConsoleMessage("§aRegistering Commands, Events & Scheduler...");
            registerCommand();
            registerEvents();
            loadSchedulers();

            sendConsoleMessage("§aRegistering Plugin Messaging Channel...");
            getProxy().registerChannel("MC_ONE_RETURN");
            getProxy().registerChannel("MC_ONE_INFO");

            sendConsoleMessage("§aVersion: §f" + this.getDescription().getVersion() + "§a enabled!");
        });
    }

    public void onDisable() {
        withErrorLogging(() -> {
            for (CorePlayer p : getOnlineCorePlayers()) {
                ((eu.mcone.coresystem.core.player.GlobalCorePlayer) p).setState(PlayerState.OFFLINE);
            }

            for (String id : overwatch.getReportManager().getInProgress().values()) {
                overwatch.getReportManager().removeTeamMember(id);
            }

            try {
                mongoConnection.disconnect();
            } catch (NoClassDefFoundError ignored) {
            }

            sendConsoleMessage("§cPlugin disabled!");
        });
    }

    private void registerCommand() {
        registerCommands(
                new PingCMD(),
                new TeamChatCMD(),
                new PermsCMD(),
                new PunishCMD(overwatch),
                new WhoisCMD(),
                new RestartCMD(),
                new MaintenanceCMD(),
                new PrivacyCMD(),
                new CoinsCMD(),
                new EmeraldsCMD(),
                new PayCMD(),

                new LobbyCMD(),
                new ServerCMD(),
                new SendCMD(),

                new NickCMD(),
                new UnnickCMD(),

                new FriendCMD(),
                new PartyCMD(),
                new JumpCMD(),
                new MsgCMD(),
                new ReplyCMD(),
                new HelpCMD(),
                new BungeecordCMD(),
                new RegisterCMD(),
                new ForgotpassCMD(),
                new ChatlogCMD(),
                new RulesCMD(),

                new PremiumCMD(),
                new YoutubeCMD(),
                new BugreportCMD()
        );
    }

    private void registerEvents() {
        registerEvents(
                new ChatListener(),
                new CorePlayerListener(),
                new CorePlayerUpdateListener(),
                new LabyModListener(),
                new PermissionCheckListener(),
                new PlayerVersionCheckListener(),
                new PluginMessageListener(),
                new PostLoginListener(),
                new ProxyPingListener(),
                new ServerConnectListener(),
                new ServerKickListener(),
                new ServerSwitchListener(),
                new SentryListener(),
                new TabCompleteListener()
        );
    }

    private void loadSchedulers() {
        getProxy().getScheduler().schedule(this, new PremiumCheck(), 0, 5, TimeUnit.SECONDS);
        getProxy().getScheduler().schedule(this, new Broadcast(), 0, 15, TimeUnit.MINUTES);
        getProxy().getScheduler().schedule(this, new OnlineTime(), 0, 1, TimeUnit.MINUTES);
    }

    public MongoDatabase getMongoDB(Database database) {
        switch (database) {
            case SYSTEM:
                return this.mongoConnection.getDatabase(Database.SYSTEM);
            case ONEGAMING:
                return this.mongoConnection.getDatabase(Database.ONEGAMING);
            default:
                return null;
        }
    }

    @Override
    public MongoDatabase getMongoDB() {
        return this.mongoConnection.getDatabase(Database.SYSTEM);
    }

    @Override
    public MongoDatabase getStatsDB() {
        return this.mongoConnection.getDatabase(Database.STATS);
    }

    public CorePlayer getCorePlayer(ProxiedPlayer p) {
        return corePlayers.getOrDefault(p.getUniqueId(), null);
    }

    public CorePlayer getCorePlayer(UUID uuid) {
        return corePlayers.getOrDefault(uuid, null);
    }

    public CorePlayer getCorePlayer(String name) {
        for (CorePlayer p : corePlayers.values()) {
            if (p.getName().equalsIgnoreCase(name)) return p;
        }
        return null;
    }

    @Override
    public GlobalCorePlayer getGlobalCorePlayer(UUID uuid) {
        return getCorePlayer(uuid);
    }

    public Collection<CorePlayer> getOnlineCorePlayers() {
        return new ArrayList<>(corePlayers.values());
    }

    @Override
    public OfflineCorePlayer getOfflineCorePlayer(String name) throws PlayerNotResolvedException {
        for (BungeeCorePlayer cp : corePlayers.values()) {
            if (cp.getName().equalsIgnoreCase(name)) return cp;
        }
        return new BungeeOfflineCorePlayer(this, name);
    }

    @Override
    public OfflineCorePlayer getOfflineCorePlayer(UUID uuid) throws PlayerNotResolvedException {
        BungeeCorePlayer cp = corePlayers.getOrDefault(uuid, null);
        return cp != null ? cp : new BungeeOfflineCorePlayer(this, uuid);
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