/*
 * Copyright (c) 2017 - 2020 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.api.bukkit;

import com.mongodb.client.model.ReplaceOptions;
import eu.mcone.coresystem.api.bukkit.chat.Messenger;
import eu.mcone.coresystem.api.bukkit.command.CoreCommand;
import eu.mcone.coresystem.api.bukkit.gamemode.Gamemode;
import eu.mcone.coresystem.api.bukkit.inventory.modification.InventoryModificationManager;
import eu.mcone.coresystem.api.bukkit.player.TranslationManager;
import eu.mcone.coresystem.api.bukkit.player.profile.GameProfile;
import eu.mcone.coresystem.api.core.GlobalCorePlugin;
import io.sentry.SentryClient;
import io.sentry.SentryClientFactory;
import lombok.Getter;
import net.minecraft.server.v1_8_R3.MinecraftServer;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.Serializable;

import static com.mongodb.client.model.Filters.eq;

public abstract class CorePlugin extends JavaPlugin implements GlobalCorePlugin, Serializable {

    @Getter
    private final Gamemode gamemode;
    @Getter
    private final String pluginName, consolePrefix, prefixTranslation;
    @Getter
    private final ChatColor pluginColor;
    @Getter
    private Messenger messenger;
    @Getter
    protected final SentryClient sentryClient;

    protected CorePlugin(Gamemode pluginGamemode, String prefixTranslation) {
        this(pluginGamemode, prefixTranslation, null);
    }

    protected CorePlugin(Gamemode pluginGamemode, String prefixTranslation, String sentryDsn) {
        this(pluginGamemode, pluginGamemode.getName(), pluginGamemode.getColor(), prefixTranslation, sentryDsn);
    }

    protected CorePlugin(String pluginName, ChatColor pluginColor, String prefixTranslation) {
        this(pluginName, pluginColor, prefixTranslation, null);
    }

    protected CorePlugin(String pluginName, ChatColor pluginColor, String prefixTranslation, String sentryDsn) {
        this(Gamemode.UNDEFINED, pluginName, pluginColor, prefixTranslation, sentryDsn);
    }

    private CorePlugin(Gamemode pluginGamemode, String pluginName, ChatColor pluginColor, String prefixTranslation, String sentryDsn) {
        if (pluginGamemode == null) {
            throw new NullPointerException("Gamemode must not be null!");
        }

        this.gamemode = pluginGamemode;
        this.pluginName = pluginName;
        this.prefixTranslation = prefixTranslation;
        this.consolePrefix = "§8[" + pluginColor + pluginName + "§8] §7";
        this.pluginColor = pluginColor;

        if (sentryDsn != null && Boolean.parseBoolean(System.getProperty("EnableSentry"))) {
            sendConsoleMessage("§aInitialzing Sentry...");
            this.sentryClient = SentryClientFactory.sentryClient(sentryDsn);
            this.sentryClient.setServerName(MinecraftServer.getServer().getPropertyManager().properties.getProperty("server-name"));
            this.sentryClient.setRelease(getDescription().getVersion());
            this.sentryClient.addTag("Server version", getServer().getVersion());
            this.sentryClient.addTag("Server IP", getServer().getIp());
            this.sentryClient.addTag("Server Port", String.valueOf(getServer().getPort()));
            this.sentryClient.addTag("Server View distance", String.valueOf(getServer().getViewDistance()));
            this.sentryClient.addTag("Server MOTD", getServer().getMotd());
            this.sentryClient.addTag("Plugin dependencies", getDescription().getDepend().toString());
            this.sentryClient.addTag("Plugin soft dependencies", getDescription().getSoftDepend().toString());
            this.sentryClient.addTag("Max Players", String.valueOf(getServer().getMaxPlayers()));
        } else {
            this.sentryClient = null;
        }
    }

    @Override
    public void onEnable() {
        CoreSystem.getInstance().getPluginManager().registerCorePlugin(this);
        this.messenger = CoreSystem.getInstance().initializeMessenger(prefixTranslation);

        TranslationManager translations = CoreSystem.getInstance().getTranslationManager();
        translations.loadAdditionalCategories(getPluginSlug());
        translations.registerTranslationKeys(
                YamlConfiguration.loadConfiguration(getTextResource("plugin.yml")),
                getPluginSlug()
        );
    }

    public InventoryModificationManager getInventoryModificationManager() {
        return CoreSystem.getInstance().getPluginManager().getInventoryModificationManager(this);
    }

    public <T> T loadGameProfile(Player player, Class<T> clazz) {
        T profile = CoreSystem.getInstance().getMongoDB().getCollection(getPluginSlug() + "_profile", clazz).find(eq("uuid", player.getUniqueId().toString())).first();

        if (profile != null) {
            return profile;
        } else {
            try {
                profile = clazz.newInstance();
                return profile;
            } catch (InstantiationException | IllegalAccessException e) {
                throw new IllegalArgumentException("Gameprofile class " + clazz.getName() + " could not be instanciated! Does it has an NoArgsConstructor?", e);
            }
        }
    }

    public void saveGameProfile(GameProfile profile) {
        if (profile.getUuid() != null) {
            CoreSystem.getInstance().getMongoDB().getCollection(getPluginSlug() + "_profile", GameProfile.class).replaceOne(
                    eq("uuid", profile.getUuid()),
                    profile,
                    new ReplaceOptions().upsert(true)
            );
        } else {
            throw new RuntimeException("UUID Field in Gameprofile is null! The Player constructor must be used!");
        }
    }

    /**
     * sends a message to the console with the plugin name as prefix
     *
     * @param message message
     */
    public void sendConsoleMessage(String message) {
        getServer().getConsoleSender().sendMessage(consolePrefix + message);
    }

    /**
     * registers all Event for this plugin
     *
     * @param listeners listeners array
     */
    public void registerEvents(Listener... listeners) {
        for (Listener listener : listeners) {
            getServer().getPluginManager().registerEvents(listener, this);
        }
    }

    /**
     * registers all CoreCommands for this plugin
     *
     * @param commands commands array
     */
    public void registerCommands(CoreCommand... commands) {
        for (CoreCommand command : commands) {
            CoreSystem.getInstance().getPluginManager().registerCoreCommand(command, this);
        }
    }

    /**
     * unregisters specific CoreCommands (can be registered by any Plugin)
     *
     * @param commands commands array
     */
    public void unregisterCommands(CoreCommand... commands) {
        for (CoreCommand command : commands) {
            CoreSystem.getInstance().getPluginManager().unregisterCoreCommand(command);
        }
    }

    public boolean hasSentryClient() {
        return sentryClient != null;
    }

    public void withErrorLogging(Runnable runnable) {
        try {
            runnable.run();
        } catch (Exception e) {
            if (sentryClient != null) {
                sentryClient.sendException(e);
            }
            throw e;
        }
    }

    public String getPluginSlug() {
        return pluginName.replace(" ", "_").toLowerCase().replaceAll("[^a-z0-9_]", "");
    }

}
