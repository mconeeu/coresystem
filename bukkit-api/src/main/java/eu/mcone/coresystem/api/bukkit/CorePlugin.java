/*
 * Copyright (c) 2017 - 2020 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.api.bukkit;

import com.mongodb.client.model.ReplaceOptions;
import com.mongodb.client.model.UpdateOptions;
import eu.mcone.coresystem.api.bukkit.command.CoreCommand;
import eu.mcone.coresystem.api.bukkit.gamemode.Gamemode;
import eu.mcone.coresystem.api.bukkit.inventory.modification.InventoryModificationManager;
import eu.mcone.coresystem.api.bukkit.player.profile.GameProfile;
import eu.mcone.coresystem.api.bukkit.util.Messenger;
import eu.mcone.coresystem.api.core.GlobalCorePlugin;
import eu.mcone.coresystem.api.core.exception.CoreException;
import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.Serializable;
import java.util.ArrayList;

import static com.mongodb.client.model.Filters.eq;

public abstract class CorePlugin extends JavaPlugin implements GlobalCorePlugin, Serializable {

    @Getter
    private final Gamemode gamemode;
    @Getter
    private final String pluginName, consolePrefix;
    @Getter
    private final ChatColor pluginColor;
    @Getter
    private final Messenger messenger;

    protected CorePlugin(Gamemode pluginGamemode, String prefixTranslation) {
        this(pluginGamemode, pluginGamemode.getName().toLowerCase(), pluginGamemode.getColor(), prefixTranslation);
    }

    protected CorePlugin(String pluginName, ChatColor pluginColor, String prefixTranslation) {
        this(Gamemode.UNDEFINED, pluginName, pluginColor, prefixTranslation);
    }

    private CorePlugin(Gamemode pluginGamemode, String pluginName, ChatColor pluginColor, String prefixTranslation) {
        if (pluginGamemode == null) {
            throw new NullPointerException("Gamemode must not be null!");
        }

        this.gamemode = pluginGamemode;
        this.pluginName = pluginName;
        this.consolePrefix = "§8[" + pluginColor + pluginName + "§8] §7";
        this.pluginColor = pluginColor;
        this.messenger = new Messenger(prefixTranslation);
    }

    @Override
    public void onEnable() {
        CoreSystem.getInstance().getTranslationManager().loadAdditionalCategories(pluginName);
        registerTranslationKeys();
    }

    public InventoryModificationManager getInventoryModificationManager() {
        return CoreSystem.getInstance().getPluginManager().getInventoryModificationManager(this);
    }

    public <T> T loadGameProfile(Player player, Class<T> clazz) {
        T profile = CoreSystem.getInstance().getMongoDB().getCollection(pluginName + "_profile", clazz).find(eq("uuid", player.getUniqueId().toString())).first();
        if (profile != null) {
            return profile;
        } else {
            try {
                profile = clazz.newInstance();
                return profile;
            } catch (InstantiationException | IllegalAccessException e) {
                try {
                    throw new CoreException("Gameprofile class " + clazz.getName() + " could not be instanciated! Does it has an NoArgsConstructor?", e);
                } catch (CoreException e1) {
                    e1.printStackTrace();
                }
                return null;
            }
        }
    }

    public void saveGameProfile(GameProfile profile) {
        if (profile.getUuid() != null) {
            CoreSystem.getInstance().getMongoDB().getCollection(pluginName + "_profile", GameProfile.class).replaceOne(
                    eq("uuid", profile.getUuid()),
                    profile,
                    ReplaceOptions.createReplaceOptions(
                            new UpdateOptions().upsert(true)
                    )
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

    private void registerTranslationKeys() {
        FileConfiguration config = YamlConfiguration.loadConfiguration(getTextResource("plugin.yml"));
        ArrayList<String> list = (ArrayList<String>) config.getList("translations");

        if (list != null && !list.isEmpty()) {
            CoreSystem.getInstance().getTranslationManager().registerKeys(pluginName, list);
        }
    }

}
