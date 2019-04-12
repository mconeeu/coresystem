/*
 * Copyright (c) 2017 - 2019 Dominik Lippl, Rufus Maiwald, Felix Schmid and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.api.bungee;

import eu.mcone.coresystem.api.bungee.player.CorePlayer;
import eu.mcone.coresystem.api.bungee.player.FriendSystem;
import eu.mcone.coresystem.api.bungee.player.NickManager;
import eu.mcone.coresystem.api.bungee.player.OfflineCorePlayer;
import eu.mcone.coresystem.api.bungee.util.ChannelHandler;
import eu.mcone.coresystem.api.core.GlobalCoreSystem;
import eu.mcone.coresystem.api.core.exception.PlayerNotResolvedException;
import eu.mcone.coresystem.api.core.labymod.LabyModAPI;
import lombok.Getter;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.Collection;
import java.util.UUID;

public abstract class CoreSystem extends CorePlugin implements GlobalCoreSystem {

    @Getter
    private static CoreSystem instance;

    protected CoreSystem() {
        super("bungeesystem", ChatColor.DARK_AQUA, "system.prefix");
    }

    protected void setInstance(CoreSystem instance) {
        if (instance == null) {
            System.err.println("BungeeCoreSystem instance cannot be set twice!");
        } else {
            CoreSystem.instance = instance;
        }
    }

    public abstract FriendSystem getFriendSystem();

    public abstract NickManager getNickManager();

    public abstract LabyModAPI getLabyModAPI();

    public abstract ChannelHandler getChannelHandler();

    public abstract CorePlayer getCorePlayer(ProxiedPlayer player);

    public abstract CorePlayer getCorePlayer(UUID uuid);

    public abstract CorePlayer getCorePlayer(String name);

    public abstract Collection<CorePlayer> getOnlineCorePlayers();

    /**
     * creates an CorePlayer object for an offline or online player
     * this object has limited abilities as it should be uses for a potentially offline player
     * @param name Player name
     * @return OfflineCorePlayer object
     * @throws PlayerNotResolvedException thrown if the wished player is not in the database
     */
    public abstract OfflineCorePlayer getOfflineCorePlayer(String name) throws PlayerNotResolvedException;

    /**
     * creates an CorePlayer object for an offline or online player
     * this object has limited abilities as it should be uses for a potentially offline player
     * @param uuid Player uuid
     * @return OfflineCorePlayer object
     * @throws PlayerNotResolvedException thrown if the wished player is not in the database
     */
    public abstract OfflineCorePlayer getOfflineCorePlayer(UUID uuid) throws PlayerNotResolvedException;

    /**
     * returns an registered CorePlugin
     * @param name plugin name
     * @return CorePlugin
     */
    public abstract CorePlugin getPlugin(String name);

    /**
     * registers a new CorePlugin in the CoreSystem
     * @param plugin extended CorePlugin Object
     */
    public abstract void registerPlugin(CorePlugin plugin);

}
