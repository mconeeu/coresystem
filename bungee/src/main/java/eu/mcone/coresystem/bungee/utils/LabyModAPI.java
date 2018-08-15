/*
 * Copyright (c) 2017 - 2018 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.coresystem.bungee.utils;

import com.google.gson.JsonElement;
import eu.mcone.coresystem.api.bungee.event.LabyModMessageSendEvent;
import eu.mcone.coresystem.api.core.labymod.LabyPermission;
import eu.mcone.coresystem.api.core.player.GlobalCorePlayer;
import eu.mcone.coresystem.bungee.BungeeCoreSystem;
import eu.mcone.coresystem.core.labymod.LabyModConnectionHandler;
import lombok.Getter;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.protocol.packet.PluginMessage;

import java.util.Map;

/**
 * Class created by qlow | Jan
 */
public class LabyModAPI implements eu.mcone.coresystem.api.core.labymod.LabyModAPI {

    @Getter
    private LabyModConnectionHandler api = new LabyModConnectionHandler();

    /**
     * Sends the modified permissions to the given player
     *
     * @param player the player the permissions should be sent to
     */
    public void sendPermissions(GlobalCorePlayer player, Map<LabyPermission, Boolean> permissions) {
        if (permissions.size() > 0) {
            ProxyServer.getInstance().getPlayer(player.getUuid()).unsafe().sendPacket(new PluginMessage("LMC", api.getBytesToSend(permissions), false));
        }
    }

    /**
     * Sends a JSON server-message to the player
     *
     * @param player          the player the message should be sent to
     * @param messageKey      the message's key
     * @param json            the message's contents as JSON String
     */
    public void sendServerMessage(GlobalCorePlayer player, String messageKey, String json) {
        ProxiedPlayer p = ProxyServer.getInstance().getPlayer(player.getUuid());

        // Calling the Bukkit event
        LabyModMessageSendEvent sendEvent = new LabyModMessageSendEvent(p, messageKey, json, false);
        ProxyServer.getInstance().getPluginManager().callEvent(sendEvent);

        // Sending the packet
        if (!sendEvent.isCancelled())
            p.unsafe().sendPacket(new PluginMessage("LMC", api.getBytesToSend(messageKey, json), false));
    }

}
