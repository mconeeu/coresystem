/*
 * Copyright (c) 2017 - 2019 Dominik Lippl, Rufus Maiwald, Felix Schmid and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.bukkit;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParser;
import com.mojang.authlib.properties.Property;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoDatabase;
import eu.mcone.coresystem.api.bukkit.CorePlugin;
import eu.mcone.coresystem.api.bukkit.CoreSystem;
import eu.mcone.coresystem.api.bukkit.config.typeadapter.bson.CraftItemStackCodecProvider;
import eu.mcone.coresystem.api.bukkit.config.typeadapter.bson.LocationCodecProvider;
import eu.mcone.coresystem.api.bukkit.config.typeadapter.gson.CraftItemStackTypeAdapter;
import eu.mcone.coresystem.api.bukkit.config.typeadapter.gson.LocationTypeAdapter;
import eu.mcone.coresystem.api.bukkit.event.MoneyChangeEvent;
import eu.mcone.coresystem.api.bukkit.inventory.ProfileInventoryModifier;
import eu.mcone.coresystem.api.bukkit.inventory.anvil.AnvilClickEventHandler;
import eu.mcone.coresystem.api.bukkit.inventory.anvil.CoreAnvilInventory;
import eu.mcone.coresystem.api.bukkit.player.CorePlayer;
import eu.mcone.coresystem.api.bukkit.player.OfflineCorePlayer;
import eu.mcone.coresystem.api.bukkit.scoreboard.MainScoreboard;
import eu.mcone.coresystem.api.bukkit.util.CoreActionBar;
import eu.mcone.coresystem.api.bukkit.util.CoreTablistInfo;
import eu.mcone.coresystem.api.bukkit.util.CoreTitle;
import eu.mcone.coresystem.api.bukkit.world.BuildSystem;
import eu.mcone.coresystem.api.bukkit.world.CoreWorld;
import eu.mcone.coresystem.api.core.exception.PlayerNotResolvedException;
import eu.mcone.coresystem.api.core.player.Currency;
import eu.mcone.coresystem.api.core.player.GlobalCorePlayer;
import eu.mcone.coresystem.api.core.player.SkinInfo;
import eu.mcone.coresystem.bukkit.channel.*;
import eu.mcone.coresystem.bukkit.command.*;
import eu.mcone.coresystem.bukkit.hologram.CoreHologramManager;
import eu.mcone.coresystem.bukkit.inventory.ProfileInventory;
import eu.mcone.coresystem.bukkit.inventory.anvil.AnvilInventory;
import eu.mcone.coresystem.bukkit.labymod.LabyModAPI;
import eu.mcone.coresystem.bukkit.listener.*;
import eu.mcone.coresystem.bukkit.npc.CoreNpcManager;
import eu.mcone.coresystem.bukkit.player.BukkitCorePlayer;
import eu.mcone.coresystem.bukkit.player.BukkitOfflineCorePlayer;
import eu.mcone.coresystem.bukkit.player.CoreAfkManager;
import eu.mcone.coresystem.bukkit.player.NickManager;
import eu.mcone.coresystem.bukkit.util.*;
import eu.mcone.coresystem.bukkit.world.WorldManager;
import eu.mcone.coresystem.core.CoreModuleCoreSystem;
import eu.mcone.coresystem.core.player.PermissionManager;
import eu.mcone.coresystem.core.player.PlayerUtils;
import eu.mcone.coresystem.core.translation.TranslationManager;
import eu.mcone.coresystem.core.util.CoreCooldownSystem;
import eu.mcone.coresystem.core.util.MoneyUtil;
import eu.mcone.networkmanager.core.api.database.Database;
import eu.mcone.networkmanager.core.database.MongoConnection;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.server.v1_8_R3.MinecraftServer;
import org.bson.UuidRepresentation;
import org.bson.codecs.UuidCodecProvider;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class BukkitCoreSystem extends CoreSystem implements CoreModuleCoreSystem {

    @Getter
    private static BukkitCoreSystem system;

    private MongoConnection mongoConnection;
    private MongoDatabase database1;
    private MongoDatabase database2;
    private MongoDatabase database3;
    private MongoDatabase database4;

    @Getter
    private TranslationManager translationManager;
    @Getter
    private PermissionManager permissionManager;
    @Getter
    private PluginManager pluginManager;
    @Getter
    private NickManager nickManager;
    @Getter
    private ChannelHandler channelHandler;
    @Getter
    private WorldManager worldManager;
    @Getter
    private CoreAfkManager afkManager;
    @Getter
    private CoreNpcManager npcManager;
    @Getter
    private CoreHologramManager hologramManager;
    @Getter
    private LabyModAPI labyModAPI;
    @Getter
    private PlayerUtils playerUtils;
    @Getter
    private MoneyUtil moneyUtil;
    @Getter
    private Gson gson;
    @Getter
    private JsonParser jsonParser;

    @Getter
    private CorePlayerDataStorage playerDataStorage;
    @Getter
    private Map<UUID, BukkitCorePlayer> corePlayers;
    @Getter
    private boolean cloudsystemAvailable;

    @Getter
    @Setter
    private boolean customEnderchestEnabled = false;

    @Override
    public void onEnable() {
        setInstance(this);
        system = this;

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

        mongoConnection = new MongoConnection("db.mcone.eu", "admin", "T6KIq8gjmmF1k7futx0cJiJinQXgfguYXruds1dFx1LF5IsVPQjuDTnlI1zltpD9", "admin", 27017)
                .codecRegistry(
                        MongoClientSettings.getDefaultCodecRegistry(),
                        CodecRegistries.fromProviders(
                                new CraftItemStackCodecProvider(),
                                new LocationCodecProvider(),
                                new UuidCodecProvider(UuidRepresentation.JAVA_LEGACY),
                                PojoCodecProvider.builder().automatic(true).build()
                        )
                )
                .connect();

        database1 = mongoConnection.getDatabase(Database.SYSTEM);
        database2 = mongoConnection.getDatabase(Database.STATS);
        database3 = mongoConnection.getDatabase(Database.DATA);
        database4 = mongoConnection.getDatabase(Database.CLOUD);

        System.out.println(database1.getCodecRegistry().get(CraftItemStack.class));

        pluginManager = new PluginManager();
        moneyUtil = new MoneyUtil(this, database1) {
            @Override
            protected void fireEvent(GlobalCorePlayer player, Currency currency) {
                Bukkit.getServer().getPluginManager().callEvent(new MoneyChangeEvent((CorePlayer) player, currency));
            }
        };
        channelHandler = new ChannelHandler();
        playerUtils = new PlayerUtils(this);
        gson = new GsonBuilder()
                .registerTypeAdapter(Location.class, new LocationTypeAdapter())
                .registerTypeAdapter(ItemStack.class, new CraftItemStackTypeAdapter())
                .registerTypeAdapter(CraftItemStack.class, new CraftItemStackTypeAdapter())
                .create();
        jsonParser = new JsonParser();
        playerDataStorage = new CorePlayerDataStorage(this);

        cloudsystemAvailable = checkIfCloudSystemAvailable();
        sendConsoleMessage("§7CloudSystem available: " + cloudsystemAvailable);

        sendConsoleMessage("§aLoading Translations...");
        translationManager = new TranslationManager(database1, this);

        sendConsoleMessage("§aInitializing LabyModAPI...");
        labyModAPI = new LabyModAPI();

        sendConsoleMessage("§aStarting WorldManager...");
        worldManager = new WorldManager(this);

        sendConsoleMessage("§aStarting NpcManager...");
        npcManager = new CoreNpcManager(this);

        sendConsoleMessage("§aStarting HologramManager...");
        hologramManager = new CoreHologramManager(this);

        sendConsoleMessage("§aStarting AFK-Manager...");
        afkManager = new CoreAfkManager();

        sendConsoleMessage("§aLoading Permissions & Groups...");
        permissionManager = new PermissionManager(MinecraftServer.getServer().getPropertyManager().properties.getProperty("server-name"), database1);

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
            CorePlayerListener.setPermissions(p);
            Property textures = ((CraftPlayer) p).getHandle().getProfile().getProperties().get("textures").iterator().next();

            new eu.mcone.coresystem.bukkit.player.BukkitCorePlayer(
                    this,
                    p.getAddress().getAddress(),
                    new SkinInfo(
                            p.getName(),
                            textures.getValue(),
                            textures.getSignature(),
                            SkinInfo.SkinType.PLAYER
                    ),
                    p
            ).registerPacketListener(p);
            channelHandler.createSetRequest(p, "UNNICK");
        }

        for (CorePlayer p : getOnlineCorePlayers()) p.setScoreboard(new MainScoreboard());
    }

    @Override
    public void onDisable() {
        for (CorePlayer p : getOnlineCorePlayers()) {
            ((BukkitCorePlayer) p).unregister();
            ((BukkitCorePlayer) p).unregisterAttachment();

            if (p.isNicked()) {
                nickManager.unnick(p.bukkit(), false);
            }
            if (p.isVanished()) {
                for (Player t : Bukkit.getOnlinePlayers()) {
                    t.showPlayer(p.bukkit());
                }
            }
        }

        npcManager.disable();
        hologramManager.disable();
        afkManager.disable();
        labyModAPI.disable();
        pluginManager.disable();

        try {
            mongoConnection.disconnect();
        } catch (NoClassDefFoundError ignored) {
        }
        corePlayers.clear();

        getServer().getMessenger().unregisterIncomingPluginChannel(this);
        getServer().getMessenger().unregisterOutgoingPluginChannel(this);

        sendConsoleMessage("§cPlugin disabled!");
    }

    private void registerCommands() {
        registerCommands(
                new BukkitCMD(),
                new FeedCMD(),
                new FlyCMD(),
                new GamemodeCMD(),
                new HealCMD(),
                new InvCMD(),
                new EcCMD(),
                new TpCMD(),
                new TphereCMD(),
                new TpallCMD(),
                new TpposCMD(),
                new SetWorldSpawnCMD(),
                new StatsCMD(),
                new SpeedCMD(),
                new VanishCMD(),
                new ProfileCMD()
        );
    }

    private void registerListener() {
        registerEvents(
                new ChatListener(),
                new CoreCommandListener(),
                new CoreInventoryListener(),
                new CorePlayerListener(),
                new CorePlayerUpdateListener(),
                new LabyModListener(),
                new SignChangeListener(),
                new VanishListener()
        );
    }

    @Override
    public MongoDatabase getMongoDB(eu.mcone.networkmanager.core.api.database.Database database) {
        switch (database) {
            case SYSTEM:
                return database1;
            case STATS:
                return database2;
            case DATA:
                return database3;
            case CLOUD:
                return database4;
            default:
                return null;
        }
    }

    @Override
    public MongoDatabase getMongoDB() {
        return database3;
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
        return new ArrayList<>(corePlayers.values());
    }

    @Override
    public BuildSystem initialiseBuildSystem(BuildSystem.BuildEvent... events) {
        return new eu.mcone.coresystem.bukkit.world.BuildSystem(this, events);
    }

    @Override
    public CoreCooldownSystem getCooldownSystem() {
        return pluginManager.getCooldownSystem();
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
    public CoreAnvilInventory createAnvilInventory(AnvilClickEventHandler handler) {
        return new AnvilInventory(handler);
    }

    @Override
    public OfflineCorePlayer getOfflineCorePlayer(String name) throws PlayerNotResolvedException {
        for (BukkitCorePlayer cp : corePlayers.values()) {
            if (cp.getName().equalsIgnoreCase(name)) return cp;
        }
        return new BukkitOfflineCorePlayer(this, name);
    }

    @Override
    public OfflineCorePlayer getOfflineCorePlayer(UUID uuid) throws PlayerNotResolvedException {
        BukkitCorePlayer cp = corePlayers.getOrDefault(uuid, null);
        return cp != null ? cp : new BukkitOfflineCorePlayer(this, uuid);
    }

    @Override
    public void setPlayerChatEnabled(boolean enabled) {
        ChatListener.enabled = enabled;
    }

    @Override
    public void enableSpawnCommand(CorePlugin plugin, CoreWorld world, int cooldown) {
        plugin.registerCommands(new SpawnCMD(plugin, world, cooldown));
    }

    @Override
    public void enableHomeSystem(CorePlugin plugin, int cooldown) {
        plugin.registerCommands(new HomeCMD(plugin, cooldown), new SethomeCMD(plugin), new DelhomeCMD(plugin), new ListHomesCMD(plugin));
    }

    @Override
    public void enableTpaSystem(CorePlugin plugin, int cooldown) {
        plugin.registerCommands(new TpaCMD(plugin), new TpacceptCMD(plugin, cooldown), new TpdenyCMD(plugin));
    }

    @Override
    public void modifyProfileInventory(ProfileInventoryModifier modifier) {
        ProfileInventory.addModifier(modifier);
    }

    @Override
    public void setProfileInventorySize(int inventorySize) {
        ProfileInventory.setSize(inventorySize);
    }

    @Override
    public void runAsync(Runnable runnable) {
        getServer().getScheduler().runTaskAsynchronously(this, runnable);
    }

}
