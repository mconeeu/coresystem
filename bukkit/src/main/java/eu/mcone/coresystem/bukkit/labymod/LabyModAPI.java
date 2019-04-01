/*
 * Copyright (c) 2017 - 2019 Dominik Lippl, Rufus Maiwald, Felix Schmid and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.bukkit.labymod;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import eu.mcone.coresystem.api.bukkit.CoreSystem;
import eu.mcone.coresystem.api.bukkit.event.LabyModMessageReceiveEvent;
import eu.mcone.coresystem.api.bukkit.event.LabyModMessageSendEvent;
import eu.mcone.coresystem.api.bukkit.event.LabyModPlayerJoinEvent;
import eu.mcone.coresystem.api.core.labymod.LabyModConnection;
import eu.mcone.coresystem.api.core.labymod.LabyPermission;
import eu.mcone.coresystem.api.core.player.GlobalCorePlayer;
import eu.mcone.coresystem.bukkit.BukkitCoreSystem;
import eu.mcone.coresystem.core.labymod.AddonManager;
import eu.mcone.coresystem.core.labymod.LabyModConnectionHandler;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Map;

/**
 * Class created by qlow | Jan
 */
public class LabyModAPI implements eu.mcone.coresystem.api.core.labymod.LabyModAPI {

    @Getter
    private LabyModConnectionHandler api = new LabyModConnectionHandler();
    @Getter
    private PacketUtils packetUtils;

    public LabyModAPI() {
        // Initializing packet utils
        this.packetUtils = new PacketUtils();

        // The LABYMOD plugin channel is higly deprecated and shouldn't be used - we just listen to it to retrieve old labymod clients.
        // Registering the incoming plugin messages listeners
        Bukkit.getMessenger().registerIncomingPluginChannel(BukkitCoreSystem.getSystem(), "LABYMOD", (channel, player, bytes) -> {
            // Converting the byte array into a byte buffer
            ByteBuf buf = Unpooled.wrappedBuffer(bytes);

            try {
                // Reading the version from the buffer
                final String version = api.readString(buf, Short.MAX_VALUE);

                // Calling the event synchronously
                Bukkit.getScheduler().runTask(BukkitCoreSystem.getSystem(), () -> {
                    // Checking whether the player is still online
                    if (!player.isOnline())
                        return;

                    // Calling the LabyModPlayerJoinEvent
                    Bukkit.getPluginManager().callEvent(new LabyModPlayerJoinEvent(player,
                            new LabyModConnection(
                                    player.getUniqueId(),
                                    version,
                                    false,
                                    0,
                                    new ArrayList<>()
                            )
                    ));
                });
            } catch (RuntimeException ignored) {
            }
        });

        Bukkit.getMessenger().registerIncomingPluginChannel(BukkitCoreSystem.getSystem(), "LMC", (channel, player, bytes) -> {
            // Converting the byte array into a byte buffer
            ByteBuf buf = Unpooled.wrappedBuffer(bytes);

            try {
                // Reading the message key
                final String messageKey = api.readString(buf, Short.MAX_VALUE);
                final String messageContents = api.readString(buf, Short.MAX_VALUE);
                final JsonElement jsonMessage = CoreSystem.getInstance().getJsonParser().parse(messageContents);

                // Calling the event synchronously
                Bukkit.getScheduler().runTask(BukkitCoreSystem.getSystem(), () -> {
                    // Checking whether the player is still online
                    if (!player.isOnline())
                        return;

                    // Listening to the INFO (join) message
                    if (messageKey.equals("INFO") && jsonMessage.isJsonObject()) {
                        JsonObject jsonObject = jsonMessage.getAsJsonObject();
                        String version = jsonObject.has("version")
                                && jsonObject.get("version").isJsonPrimitive()
                                && jsonObject.get("version").getAsJsonPrimitive().isString() ? jsonObject.get("version").getAsString() : "Unknown";

                        boolean chunkCachingEnabled = false;
                        int chunkCachingVersion = 0;

                        if (jsonObject.has("ccp") && jsonObject.get("ccp").isJsonObject()) {
                            JsonObject chunkCachingObject = jsonObject.get("ccp").getAsJsonObject();

                            if (chunkCachingObject.has("enabled"))
                                chunkCachingEnabled = chunkCachingObject.get("enabled").getAsBoolean();

                            if (chunkCachingObject.has("version"))
                                chunkCachingVersion = chunkCachingObject.get("version").getAsInt();
                        }

                        Bukkit.getPluginManager().callEvent(new LabyModPlayerJoinEvent(player,
                                new LabyModConnection(
                                        player.getUniqueId(),
                                        version,
                                        chunkCachingEnabled,
                                        chunkCachingVersion,
                                        AddonManager.getAddons(jsonObject)
                                )
                        ));
                        return;
                    }

                    // Calling the MessageReceiveEvent
                    Bukkit.getPluginManager().callEvent(new LabyModMessageReceiveEvent(player, messageKey, jsonMessage));
                });
            } catch (RuntimeException ignored) {}
        });
    }

    public void disable() {
        // Unregistering the plugin-message listeners
        Bukkit.getMessenger().unregisterIncomingPluginChannel(BukkitCoreSystem.getSystem(), "LABYMOD");
        Bukkit.getMessenger().unregisterIncomingPluginChannel(BukkitCoreSystem.getSystem(), "LMC");
    }

    /**
     * Sends the modified permissions to the given player
     *
     * @param player the player the permissions should be sent to
     */
    public void sendPermissions(GlobalCorePlayer player, Map<LabyPermission, Boolean> permissions) {
        if (permissions.size() > 0) {
            packetUtils.sendPacket(Bukkit.getPlayer(player.getUuid()), packetUtils.getPluginMessagePacket("LMC", api.getBytesToSend(permissions)));
        }
    }

    /**
     * Sends a JSON server-message to the player
     *
     * @param player          the player the message should be sent to
     * @param messageKey      the message's key
     */
    public void sendServerMessage(GlobalCorePlayer player, String messageKey, String json) {
        Player p = Bukkit.getPlayer(player.getUuid());

        // Calling the Bukkit event
        LabyModMessageSendEvent sendEvent = new LabyModMessageSendEvent(p, messageKey, json, false);
        Bukkit.getPluginManager().callEvent(sendEvent);

        // Sending the packet
        if (!sendEvent.isCancelled())
            packetUtils.sendPacket(p, packetUtils.getPluginMessagePacket("LMC", api.getBytesToSend(messageKey, json)));
    }

}
