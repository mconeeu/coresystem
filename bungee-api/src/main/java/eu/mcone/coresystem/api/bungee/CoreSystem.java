/*
 * Copyright (c) 2017 - 2020 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.api.bungee;

import eu.mcone.coresystem.api.bungee.overwatch.Overwatch;
import eu.mcone.coresystem.api.bungee.player.*;
import eu.mcone.coresystem.api.bungee.util.BungeeDebugger;
import eu.mcone.coresystem.api.bungee.util.ChannelHandler;
import eu.mcone.coresystem.api.bungee.util.Messenger;
import eu.mcone.coresystem.api.core.GlobalCoreSystem;
import eu.mcone.coresystem.api.core.exception.PlayerNotResolvedException;
import eu.mcone.coresystem.api.core.labymod.LabyModAPI;
import eu.mcone.coresystem.api.core.util.GlobalPluginManager;
import lombok.Getter;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.Collection;
import java.util.UUID;

public abstract class CoreSystem extends CorePlugin implements GlobalCoreSystem {

    @Getter
    private static CoreSystem instance;

    protected CoreSystem() {
        super(
                "BungeeSystem",
                ChatColor.WHITE,
                "system.prefix",
                "https://60310ff9f7874486af14718c51e62a80@o267551.ingest.sentry.io/5341158?stacktrace.app.packages=eu.mcone.coresystem"
        );
    }

    protected void setInstance(CoreSystem instance) {
        if (CoreSystem.instance == null) {
            CoreSystem.instance = instance;
        } else throw new IllegalStateException("Could not set CoreSystem instance. Instance already set!");
    }

    public abstract BungeeDebugger getDebugger();

    /**
     * returns the BCS PluginManager
     *
     * @return PluginManager instance
     */
    public abstract GlobalPluginManager getPluginManager();

    public abstract Overwatch getOverwatch();

    public abstract FriendSystem getFriendSystem();

    public abstract NickManager getNickManager();

    public abstract LabyModAPI<ProxiedPlayer> getLabyModAPI();

    public abstract ChannelHandler getChannelHandler();

    public abstract CorePlayer getCorePlayer(ProxiedPlayer player);

    public abstract CorePlayer getCorePlayer(UUID uuid);

    public abstract CorePlayer getCorePlayer(String name);

    public abstract Collection<CorePlayer> getOnlineCorePlayers();

    /**
     * creates an CorePlayer object for an offline or online player
     * this object has limited abilities as it should be uses for a potentially offline player
     *
     * @param name Player name
     * @return OfflineCorePlayer object
     * @throws PlayerNotResolvedException thrown if the wished player is not in the database
     */
    public abstract OfflineCorePlayer getOfflineCorePlayer(String name) throws PlayerNotResolvedException;

    /**
     * creates an CorePlayer object for an offline or online player
     * this object has limited abilities as it should be uses for a potentially offline player
     *
     * @param uuid Player uuid
     * @return OfflineCorePlayer object
     * @throws PlayerNotResolvedException thrown if the wished player is not in the database
     */
    public abstract OfflineCorePlayer getOfflineCorePlayer(UUID uuid) throws PlayerNotResolvedException;

    public abstract Messenger initializeMessenger(String prefixTranslation);

    public abstract TranslationManager getTranslationManager();

    /**
     * returns an registered CorePlugin
     *
     * @param name plugin name
     * @return CorePlugin
     */
    public abstract CorePlugin getPlugin(String name);

    /**
     * registers a new CorePlugin in the CoreSystem
     *
     * @param plugin extended CorePlugin Object
     */
    public abstract void registerPlugin(CorePlugin plugin);

}
