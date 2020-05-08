/*
 * Copyright (c) 2017 - 2019 Dominik Lippl, Rufus Maiwald, Felix Schmid and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.bukkit.channel;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import eu.mcone.coresystem.api.bukkit.CoreSystem;
import eu.mcone.coresystem.api.bukkit.event.LabyModMessageReceiveEvent;
import eu.mcone.coresystem.api.bukkit.event.LabyModPlayerJoinEvent;
import eu.mcone.coresystem.api.core.labymod.LabyModConnection;
import eu.mcone.coresystem.bukkit.BukkitCoreSystem;
import eu.mcone.coresystem.core.labymod.AddonManager;
import eu.mcone.coresystem.core.labymod.LMCUtils;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;

@RequiredArgsConstructor
public class LMCListener implements PluginMessageListener {

    private final LMCUtils<?> utils;

    @Override
    public void onPluginMessageReceived(String s, Player player, byte[] bytes) {
        ByteBuf buf = Unpooled.wrappedBuffer(bytes);
        String messageKey = utils.readString(buf, Short.MAX_VALUE);
        String messageContents = utils.readString(buf, Short.MAX_VALUE);
        JsonElement jsonMessage = CoreSystem.getInstance().getJsonParser().parse(messageContents);

        Bukkit.getScheduler().runTask(BukkitCoreSystem.getSystem(), () -> {
            if (!player.isOnline())
                return;

            if (messageKey.equals("INFO") && jsonMessage.isJsonObject()) {
                JsonObject jsonObject = jsonMessage.getAsJsonObject();
                String version = jsonObject.has("version")
                        && jsonObject.get("version").isJsonPrimitive()
                        && jsonObject.get("version").getAsJsonPrimitive().isString() ? jsonObject.get("version").getAsString() : "Unknown";

                boolean chunkCachingEnabled = false, shadowEnabled = false;
                int chunkCachingVersion = 0, shadowVersion = 0;

                if (jsonObject.has("ccp") && jsonObject.get("ccp").isJsonObject()) {
                    JsonObject chunkCachingObject = jsonObject.get("ccp").getAsJsonObject();

                    if (chunkCachingObject.has("enabled"))
                        chunkCachingEnabled = chunkCachingObject.get("enabled").getAsBoolean();

                    if (chunkCachingObject.has("version"))
                        chunkCachingVersion = chunkCachingObject.get("version").getAsInt();
                }

                if (jsonObject.has("shadow") && jsonObject.get("shadow").isJsonObject()) {
                    JsonObject shadowObject = jsonObject.get("shadow").getAsJsonObject();

                    if (shadowObject.has("enabled"))
                        shadowEnabled = shadowObject.get("enabled").getAsBoolean();

                    if (shadowObject.has("version"))
                        shadowVersion = shadowObject.get("version").getAsInt();
                }

                Bukkit.getPluginManager().callEvent(new LabyModPlayerJoinEvent(player,
                        new LabyModConnection(
                                player.getUniqueId(),
                                version,
                                chunkCachingEnabled,
                                shadowEnabled,
                                chunkCachingVersion,
                                shadowVersion,
                                AddonManager.getAddons(jsonObject)
                        )
                ));
                return;
            }

            Bukkit.getPluginManager().callEvent(new LabyModMessageReceiveEvent(player, messageKey, jsonMessage));
        });
    }

}
