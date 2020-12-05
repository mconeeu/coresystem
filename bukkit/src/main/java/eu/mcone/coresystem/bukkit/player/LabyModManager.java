/*
 * Copyright (c) 2017 - 2020 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.bukkit.player;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import eu.mcone.coresystem.api.bukkit.gamemode.Gamemode;
import eu.mcone.coresystem.api.bukkit.player.LabyModBukkitAPI;
import eu.mcone.coresystem.bukkit.BukkitCoreSystem;
import eu.mcone.coresystem.bukkit.channel.LMCListener;
import eu.mcone.coresystem.bukkit.channel.LMCLegacyListener;
import eu.mcone.coresystem.core.labymod.LMCUtils;
import io.netty.buffer.Unpooled;
import net.minecraft.server.v1_8_R3.PacketDataSerializer;
import net.minecraft.server.v1_8_R3.PacketPlayOutCustomPayload;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.util.UUID;

public class LabyModManager extends LMCUtils<Player> implements LabyModBukkitAPI {

    public LabyModManager() {
        Bukkit.getMessenger().registerIncomingPluginChannel(BukkitCoreSystem.getSystem(), "LABYMOD", new LMCLegacyListener(this));
        Bukkit.getMessenger().registerIncomingPluginChannel(BukkitCoreSystem.getSystem(), "LMC", new LMCListener(this));
    }

    public void disable() {
        Bukkit.getMessenger().unregisterIncomingPluginChannel(BukkitCoreSystem.getSystem(), "LABYMOD");
        Bukkit.getMessenger().unregisterIncomingPluginChannel(BukkitCoreSystem.getSystem(), "LMC");
    }

    @Override
    protected boolean keepSettingsOnServerSwitch() {
        return false;
    }

    @Override
    protected void sendLMCMessage(Player player, byte[] message) {
        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(
                new PacketPlayOutCustomPayload("LMC", new PacketDataSerializer(Unpooled.wrappedBuffer(message)))
        );
    }

    @Override
    public void updateGameInfo(Player player, Gamemode gamemode, long startTime, long endTime) {
        super.setCurrentGameInfo(player, gamemode.getName(), startTime, endTime);
    }

    @Override
    public void forceEmote(Player receiver, UUID npcUuid, int emoteId) {
        // List of all forced emotes
        JsonArray array = new JsonArray();

        // Emote and target NPC
        JsonObject forcedEmote = new JsonObject();
        forcedEmote.addProperty("uuid", npcUuid.toString());
        forcedEmote.addProperty("emote_id", emoteId);
        array.add(forcedEmote);

        // Send to LabyMod using the API
        sendServerMessage(receiver, "emote_api", array);
    }

    @Override
    public void forceSticker(Player receiver, UUID npcUuid, short stickerId) {
        // List of all forced stickers
        JsonArray array = new JsonArray();

        // Sticker and target NPC
        JsonObject forcedSticker = new JsonObject();
        forcedSticker.addProperty("uuid", npcUuid.toString());
        forcedSticker.addProperty("sticker_id", stickerId);
        array.add(forcedSticker);

        // Send to LabyMod using the API
        sendServerMessage(receiver, "sticker_api", array);
    }

}
