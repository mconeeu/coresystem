/*
 * Copyright (c) 2017 - 2020 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
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
import eu.mcone.coresystem.api.bukkit.chat.Messenger;
import eu.mcone.coresystem.api.bukkit.codec.CodecRegistry;
import eu.mcone.coresystem.api.bukkit.config.typeadapter.bson.ItemStackCodecProvider;
import eu.mcone.coresystem.api.bukkit.config.typeadapter.bson.LocationCodecProvider;
import eu.mcone.coresystem.api.bukkit.config.typeadapter.gson.CraftItemStackTypeAdapter;
import eu.mcone.coresystem.api.bukkit.config.typeadapter.gson.LocationTypeAdapter;
import eu.mcone.coresystem.api.bukkit.event.player.CorePlayerLoadedEvent;
import eu.mcone.coresystem.api.bukkit.event.player.MoneyChangeEvent;
import eu.mcone.coresystem.api.bukkit.inventory.anvil.AnvilClickEventHandler;
import eu.mcone.coresystem.api.bukkit.inventory.anvil.CoreAnvilInventory;
import eu.mcone.coresystem.api.bukkit.listener.WeatherChangeCanceller;
import eu.mcone.coresystem.api.bukkit.listener.WorldGrowCanceller;
import eu.mcone.coresystem.api.bukkit.npc.entity.EntityProjectile;
import eu.mcone.coresystem.api.bukkit.player.CorePlayer;
import eu.mcone.coresystem.api.bukkit.player.OfflineCorePlayer;
import eu.mcone.coresystem.api.bukkit.player.profile.interfaces.EnderchestManagerGetter;
import eu.mcone.coresystem.api.bukkit.player.profile.interfaces.HomeManagerGetter;
import eu.mcone.coresystem.api.bukkit.sound.SoundManager;
import eu.mcone.coresystem.api.bukkit.stats.CoreStatsManager;
import eu.mcone.coresystem.api.bukkit.util.CoreActionBar;
import eu.mcone.coresystem.api.bukkit.util.CoreProjectile;
import eu.mcone.coresystem.api.bukkit.util.CoreTablistInfo;
import eu.mcone.coresystem.api.bukkit.util.CoreTitle;
import eu.mcone.coresystem.api.bukkit.world.BuildSystem;
import eu.mcone.coresystem.api.bukkit.world.CoreWorld;
import eu.mcone.coresystem.api.bukkit.world.schematic.SchematicManager;
import eu.mcone.coresystem.api.core.exception.PlayerNotResolvedException;
import eu.mcone.coresystem.api.core.player.Currency;
import eu.mcone.coresystem.api.core.player.GlobalCorePlayer;
import eu.mcone.coresystem.api.core.player.SkinInfo;
import eu.mcone.coresystem.bukkit.channel.*;
import eu.mcone.coresystem.bukkit.channel.packet.CorePacketManager;
import eu.mcone.coresystem.bukkit.command.*;
import eu.mcone.coresystem.bukkit.hologram.CoreHologramManager;
import eu.mcone.coresystem.bukkit.inventory.anvil.AnvilInventory;
import eu.mcone.coresystem.bukkit.inventory.profile.CoreProfileInventory;
import eu.mcone.coresystem.bukkit.inventory.profile.CoreProfilePlayerInventory;
import eu.mcone.coresystem.bukkit.listener.*;
import eu.mcone.coresystem.bukkit.npc.CoreNpcManager;
import eu.mcone.coresystem.bukkit.overwatch.Overwatch;
import eu.mcone.coresystem.bukkit.player.*;
import eu.mcone.coresystem.bukkit.sound.CoreSoundManager;
import eu.mcone.coresystem.bukkit.util.*;
import eu.mcone.coresystem.bukkit.vanish.CoreVanishManager;
import eu.mcone.coresystem.bukkit.world.WorldManager;
import eu.mcone.coresystem.core.CoreModuleCoreSystem;
import eu.mcone.coresystem.core.player.PermissionManager;
import eu.mcone.coresystem.core.player.PlayerUtils;
import eu.mcone.coresystem.core.util.CoreCooldownSystem;
import eu.mcone.coresystem.core.util.MoneyUtil;
import group.onegaming.networkmanager.core.api.database.Database;
import group.onegaming.networkmanager.core.database.MongoConnection;
import group.onegaming.networkmanager.core.random.NetworkUniqueIdUtil;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.server.v1_8_R3.MinecraftServer;
import net.minecraft.server.v1_8_R3.PacketDataSerializer;
import net.minecraft.server.v1_8_R3.PacketPlayOutCustomPayload;
import org.bson.UuidRepresentation;
import org.bson.codecs.UuidCodecProvider;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.pojo.Conventions;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.*;

public class BukkitCoreSystem extends CoreSystem implements CoreModuleCoreSystem {

    @Getter
    private static BukkitCoreSystem system;

    @Getter
    private BukkitDebugger debugger;

    private MongoConnection mongoConnection;
    private MongoDatabase systemDB;
    private MongoDatabase statsDB;
    private MongoDatabase gameDB;
    private MongoDatabase dataDB;
    private MongoDatabase cloudDB;

    @Getter
    private BukkitTranslationManager translationManager;
    @Getter
    private PermissionManager permissionManager;
    @Getter
    private CoreCooldownSystem cooldownSystem;
    @Getter
    private CorePluginManager pluginManager;
    @Getter
    private Overwatch overwatch;
    @Getter
    private CoreNickManager nickManager;
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
    private LabyModManager labyModAPI;
    @Getter
    private CoreStatsManager coreStatsManager;
    @Getter
    private PlayerUtils playerUtils;
    @Getter
    private MoneyUtil moneyUtil;
    @Getter
    private CoreVanishManager vanishManager;
    @Getter
    private Gson gson;
    @Getter
    private JsonParser jsonParser;
    @Getter
    private CorePacketManager packetManager;
    @Getter
    private SoundManager soundManager;
    @Getter
    private NetworkUniqueIdUtil uniqueIdUtil;

    @Getter
    private Map<UUID, BukkitCorePlayer> corePlayers;
    @Getter
    private boolean cloudsystemAvailable;

    @Getter
    @Setter
    private boolean customEnderchestEnabled = false;

    @Override
    public void onEnable() {
        withErrorLogging(() -> {
            setInstance(this);
            system = this;

            Bukkit.getConsoleSender().sendMessage("??f\n" +
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

            gson = new GsonBuilder()
                    .registerTypeAdapter(Location.class, new LocationTypeAdapter())
                    .registerTypeAdapter(ItemStack.class, new CraftItemStackTypeAdapter())
                    .registerTypeAdapter(CraftItemStack.class, new CraftItemStackTypeAdapter())
                    .create();
            jsonParser = new JsonParser();

            debugger = new BukkitDebugger(this);

            mongoConnection = new MongoConnection().codecRegistry(
                    MongoClientSettings.getDefaultCodecRegistry(),
                    CodecRegistries.fromProviders(
                            new ItemStackCodecProvider(),
                            new LocationCodecProvider(),
                            new UuidCodecProvider(UuidRepresentation.JAVA_LEGACY),
                            PojoCodecProvider.builder().conventions(Collections.singletonList(Conventions.ANNOTATION_CONVENTION)).automatic(true).build()
                    )
            ).connect();

            systemDB = mongoConnection.getDatabase(Database.SYSTEM);
            statsDB = mongoConnection.getDatabase(Database.STATS);
            gameDB = mongoConnection.getDatabase(Database.GAME);
            dataDB = mongoConnection.getDatabase(Database.DATA);
            cloudDB = mongoConnection.getDatabase(Database.CLOUD);

            uniqueIdUtil = new NetworkUniqueIdUtil(getMongoDB(Database.SYSTEM));

            uniqueIdUtil = new NetworkUniqueIdUtil(systemDB);
            packetManager = new CorePacketManager();
            cooldownSystem = new CoreCooldownSystem(this);
            pluginManager = new CorePluginManager();
            moneyUtil = new MoneyUtil(this, systemDB) {
                @Override
                protected void fireEvent(GlobalCorePlayer player, Currency currency) {
                    channelHandler.createSetRequest(
                            ((CorePlayer) player).bukkit(),
                            "MONEY_CHANGE",
                            currency.toString(),
                            String.valueOf(currency.equals(Currency.COINS) ? player.getCoins() : player.getEmeralds())
                    );
                    Bukkit.getServer().getPluginManager().callEvent(new MoneyChangeEvent((CorePlayer) player, currency));
                }
            };
            channelHandler = new ChannelHandler();
            playerUtils = new PlayerUtils(this);

            cloudsystemAvailable = checkIfCloudSystemAvailable();
            sendConsoleMessage("??7CloudSystem available: " + cloudsystemAvailable);

            sendConsoleMessage("??aLoading Translations...");
            translationManager = new BukkitTranslationManager(this, "bukkitsystem");

            sendConsoleMessage("??aInitializing LabyModManager...");
            labyModAPI = new LabyModManager();

            sendConsoleMessage("??aInitializing CoreStatsManager...");
            coreStatsManager = new CoreStatsManager();

            sendConsoleMessage("??aStarting WorldManager...");
            worldManager = new WorldManager(this);

            sendConsoleMessage("??aStarting NpcManager...");
            npcManager = new CoreNpcManager(this);

            sendConsoleMessage("??aStarting HologramManager...");
            hologramManager = new CoreHologramManager(this);

            sendConsoleMessage("??aStarting AFK-Manager...");
            afkManager = new CoreAfkManager(this);

            sendConsoleMessage("??aLoading Permissions & Groups...");
            permissionManager = new PermissionManager(MinecraftServer.getServer().getPropertyManager().properties.getProperty("server-name"), systemDB);

            sendConsoleMessage("??aStarting Overwatch ??aSystem...");
            overwatch = new Overwatch(this);

            sendConsoleMessage("??aStarting NickManager...");
            nickManager = new CoreNickManager(this);

            sendConsoleMessage("??aStarting VanishManager...");
            vanishManager = new CoreVanishManager(this);

            sendConsoleMessage("??aStarting SoundManager...");
            soundManager = new CoreSoundManager(this);

            sendConsoleMessage("??aLoading Commands, Events, CoreInventories...");
            this.registerListener();
            this.registerCommands();
            corePlayers = new HashMap<>();

            sendConsoleMessage("??aRegistering BungeeCord Messaging Channel...");
            getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
            getServer().getMessenger().registerOutgoingPluginChannel(this, "WDL|CONTROL");
            getServer().getMessenger().registerIncomingPluginChannel(this, "mcone:return", new ReturnPluginChannelListener());
            getServer().getMessenger().registerIncomingPluginChannel(this, "mcone:info", new InfoPluginChannelListener());
            getServer().getMessenger().registerIncomingPluginChannel(this, "BungeeCord", new BungeeCordReturnPluginChannelListener());
            getServer().getMessenger().registerIncomingPluginChannel(this, "WDL|INIT", new AntiWorldDownloader());

            getServer().getScheduler().runTask(this, () -> {
                for (Player p : Bukkit.getOnlinePlayers()) {
                    CorePlayerListener.LOADING_MSG.send(p);
                    p.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, Integer.MAX_VALUE, 0));
                    CorePlayerListener.setCorePermissibleBase(p);


                    Property textures = ((CraftPlayer) p).getHandle().getProfile().getProperties().get("textures").iterator().next();
                    CorePlayerLoadedEvent e = new CorePlayerLoadedEvent(CorePlayerLoadedEvent.Reason.RELOAD, new eu.mcone.coresystem.bukkit.player.BukkitCorePlayer(
                            this,
                            p.getAddress().getAddress(),
                            new SkinInfo(
                                    p.getName(),
                                    textures.getValue(),
                                    textures.getSignature(),
                                    SkinInfo.SkinType.PLAYER
                            ),
                            p
                    ), p);
                    getServer().getPluginManager().callEvent(e);

                    channelHandler.createSetRequest(p, "REFRESH_NICK");

                    CorePlayerListener.LOADING_SUCCESS_MSG.send(p);
                    p.removePotionEffect(PotionEffectType.BLINDNESS);
                }

                getVanishManager().recalculateVanishes();
            });

            super.onEnable();

            Bukkit.getScheduler().runTask(this, () -> {
                if (Bukkit.getOnlinePlayers().size() > 0) {
                    channelHandler.createSetRequest(Bukkit.getOnlinePlayers().iterator().next(), "REFRESH_NICKS");
                }
            });

            sendConsoleMessage("??aVersion ??f" + this.getDescription().getVersion() + "??a enabled!");
        });
    }

    @Override
    public void onDisable() {
        withErrorLogging(() -> {
            nickManager.disable();
            packetManager.disable();

            for (CorePlayer p : getOnlineCorePlayers()) {
                ((BukkitCorePlayer) p).unregister();
                ((BukkitCorePlayer) p).unregisterAttachment();

                if (p.isVanished()) {
                    for (Player t : Bukkit.getOnlinePlayers()) {
                        t.showPlayer(p.bukkit());
                    }
                }
            }

            npcManager.disable();
            hologramManager.disable();
            worldManager.disable();
            afkManager.disable();
            labyModAPI.disable();
            pluginManager.disable();

            try {
                mongoConnection.disconnect();
            } catch (NoClassDefFoundError ignored) {}

            getServer().getMessenger().unregisterIncomingPluginChannel(this);
            getServer().getMessenger().unregisterOutgoingPluginChannel(this);

            sendConsoleMessage("??cPlugin disabled!");
        });
    }

    private void registerCommands() {
        registerCommands(
                new BukkitCMD(),
                new CaptureCMD(),
                new ClearCMD(),
                new DebugCMD(),
                new EnderchestCMD(),
                new FeedCMD(),
                new FlyCMD(),
                new GamemodeCMD(),
                new GiveCMD(),
                new HealCMD(),
                new InvCMD(),
                new ProfileCMD(),
                new SetWorldSpawnCMD(),
                new SlowchatCMD(),
                new StatsCMD(),
                new SpeedCMD(),
                new TpCMD(),
                new TphereCMD(),
                new TpallCMD(),
                new TpposCMD(),
                new VanishChatCMD(),
                new VanishCMD()
        );
    }

    private void registerListener() {
        registerEvents(
                new ArmorListener(),
                new ChatListener(),
                new CoreCommandListener(),
                new CoreInventoryListener(),
                new CorePlayerListener(),
                new CorePlayerUpdateListener(),
                new LabyModListener(),
                new ReloadListener(),
                new SentryListener(),
                new SignChangeListener()
        );

        if (!Boolean.parseBoolean(System.getProperty("EnableWorldGrow"))) {
            sendConsoleMessage("??2Registered WorldGrowCanceller");
            registerEvents(new WorldGrowCanceller(), new WeatherChangeCanceller());
        }
    }

    @Override
    public MongoDatabase getMongoDB(Database database) {
        switch (database) {
            case SYSTEM:
                return systemDB;
            case STATS:
                return statsDB;
            case GAME:
                return gameDB;
            case DATA:
                return dataDB;
            case CLOUD:
                return cloudDB;
            default:
                return null;
        }
    }

    @Override
    public MongoDatabase getMongoDB() {
        return dataDB;
    }

    @Override
    public MongoDatabase getStatsDB() {
        return statsDB;
    }

    @Override
    public CodecRegistry createCodecRegistry(boolean listening) {
        return new eu.mcone.coresystem.bukkit.codec.CodecRegistry(CoreSystem.getInstance(), listening);
    }

    @Override
    public CorePlayer getCorePlayer(Player p) {
        return corePlayers.getOrDefault(p.getUniqueId(), null);
    }

    @Override
    public CorePlayer getCorePlayer(UUID uuid) {
        return corePlayers.getOrDefault(uuid, null);
    }

    @Override
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

    @Override
    public Collection<CorePlayer> getOnlineCorePlayers() {
        return new ArrayList<>(corePlayers.values());
    }

    @Override
    public BuildSystem initialiseBuildSystem(BuildSystem.BuildEvent... events) {
        return new eu.mcone.coresystem.bukkit.world.BuildSystem(this, events);
    }

    @Override
    public Messenger initializeMessenger(String prefixTranslation) {
        return new BukkitMessenger(this, prefixTranslation);
    }

    @Override
    public SchematicManager initializeSchematicManager(boolean cache) {
        return new eu.mcone.coresystem.bukkit.world.schematic.SchematicManager(cache);
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
    public CoreProjectile createProjectile(EntityProjectile type) {
        return new Projectile(type);
    }

    @Override
    public void openBook(Player player, ItemStack book) {
        int slot = player.getInventory().getHeldItemSlot();
        ItemStack old = player.getInventory().getItem(slot);
        player.getInventory().setItem(slot, book);

        ByteBuf buf = Unpooled.buffer(256);
        buf.setByte(0, (byte) 0);
        buf.writerIndex(1);

        PacketPlayOutCustomPayload packet = new PacketPlayOutCustomPayload("MC|BOpen", new PacketDataSerializer(buf));
        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
        player.getInventory().setItem(slot, old);
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
        ChatListener.setEnabled(enabled);
    }

    @Override
    public void setPlayerChatCooldown(int cooldown) {
        ChatListener.setCooldown(cooldown);
    }

    @Override
    public void enableSpawnCommand(CorePlugin plugin, CoreWorld world, int cooldown) {
        plugin.registerCommands(new SpawnCMD(plugin, world, cooldown));
    }

    @Override
    public void enableHomeSystem(CorePlugin plugin, HomeManagerGetter apiGetter, int cooldown) {
        plugin.registerCommands(new HomeCMD(plugin, apiGetter, cooldown), new SethomeCMD(plugin, apiGetter), new DelhomeCMD(plugin, apiGetter), new ListHomesCMD(plugin, apiGetter));
    }

    @Override
    public void enableEnderchestSystem(EnderchestManagerGetter apiGetter) {
        registerEvents(new EnderchestListener(apiGetter));
    }

    @Override
    public void enableTpaSystem(CorePlugin plugin, int cooldown) {
        plugin.registerCommands(new TpaCMD(plugin), new TpacceptCMD(plugin, cooldown), new TpdenyCMD(plugin));
    }

    @Override
    public void openProfileInventory(Player p) {
        new CoreProfileInventory(p);
    }

    @Override
    public void openProfileInventory(Player p, Player t) {
        new CoreProfilePlayerInventory(p, t);
    }

    @Override
    public void runAsync(Runnable runnable) {
        getServer().getScheduler().runTaskAsynchronously(this, runnable);
    }

}
