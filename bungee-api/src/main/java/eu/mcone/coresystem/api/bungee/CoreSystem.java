/*
 * Copyright (c) 2017 - 2018 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.coresystem.api.bungee;

import com.google.gson.Gson;
import eu.mcone.coresystem.api.bungee.player.BungeeCorePlayer;
import eu.mcone.coresystem.api.bungee.player.FriendSystem;
import eu.mcone.coresystem.api.bungee.player.NickManager;
import eu.mcone.coresystem.api.core.GlobalCoreSystem;
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
        super("BungeeCoreSystem", ChatColor.DARK_AQUA, "system.prefix");
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

    public abstract Gson getGson();

    public abstract BungeeCorePlayer getCorePlayer(ProxiedPlayer player);

    public abstract BungeeCorePlayer getCorePlayer(UUID uuid);

    public abstract BungeeCorePlayer getCorePlayer(String name);

    public abstract Collection<BungeeCorePlayer> getOnlineCorePlayers();

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
