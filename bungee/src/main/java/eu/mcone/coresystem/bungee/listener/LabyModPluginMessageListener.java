/*
 * Copyright (c) 2017 - 2019 Dominik Lippl, Rufus Maiwald, Felix Schmid and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.bungee.listener;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import eu.mcone.coresystem.api.bungee.event.LabyModMessageReceiveEvent;
import eu.mcone.coresystem.api.bungee.event.LabyModPlayerJoinEvent;
import eu.mcone.coresystem.api.core.labymod.LabyModConnection;
import eu.mcone.coresystem.bungee.BungeeCoreSystem;
import eu.mcone.coresystem.core.labymod.AddonManager;
import eu.mcone.coresystem.core.labymod.LMCUtils;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import lombok.RequiredArgsConstructor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
public class LabyModPluginMessageListener implements Listener {

    private final LMCUtils<?> utils;

    @EventHandler
    public void on(PluginMessageEvent e) {
        if (e.getTag().equals("LABYMOD")) {
            if (!(e.getSender() instanceof ProxiedPlayer))
                return;

            final ProxiedPlayer p = (ProxiedPlayer) e.getSender();

            // Converting the byte array into a byte buffer
            ByteBuf buf = Unpooled.wrappedBuffer(e.getData());

            try {
                // Reading the version from the buffer
                final String version = utils.readString(buf, Short.MAX_VALUE);

                // Calling the event synchronously
                ProxyServer.getInstance().getScheduler().schedule(BungeeCoreSystem.getSystem(), () -> {
                    // Calling the LabyModPlayerJoinEvent
                    ProxyServer.getInstance().getPluginManager().callEvent(new LabyModPlayerJoinEvent(p,
                            new LabyModConnection(
                                    p.getUniqueId(),
                                    version,
                                    false,
                                    0,
                                    new ArrayList<>()
                            )
                    ));
                }, 0L, TimeUnit.SECONDS);
            } catch (RuntimeException ex) {
                ex.printStackTrace();
            }
        } else if (e.getTag().equals("LMC")) {
            if (!(e.getSender() instanceof ProxiedPlayer))
                return;

            final ProxiedPlayer p = (ProxiedPlayer) e.getSender();

            // Converting the byte array into a byte buffer
            ByteBuf buf = Unpooled.wrappedBuffer(e.getData());

            try {
                // Reading the message key
                final String messageKey = utils.readString(buf, Short.MAX_VALUE);
                final String messageContents = utils.readString(buf, Short.MAX_VALUE);
                final JsonElement jsonMessage = BungeeCoreSystem.getSystem().getJsonParser().parse(messageContents);

                // Calling the event synchronously
                ProxyServer.getInstance().getScheduler().schedule(BungeeCoreSystem.getSystem(), () -> {
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

                        ProxyServer.getInstance().getPluginManager().callEvent(new LabyModPlayerJoinEvent(p,
                                new LabyModConnection(
                                        p.getUniqueId(),
                                        version,
                                        chunkCachingEnabled,
                                        chunkCachingVersion,
                                        AddonManager.getAddons(jsonObject)
                                )
                        ));
                        return;
                    }

                    // Calling the LabyModPlayerJoinEvent
                    ProxyServer.getInstance().getPluginManager().callEvent(new LabyModMessageReceiveEvent(p, messageKey, jsonMessage));
                }, 0L, TimeUnit.SECONDS);
            } catch (RuntimeException ex) {
                ex.printStackTrace();
            }
        }
    }

}
