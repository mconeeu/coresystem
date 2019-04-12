/*
 * Copyright (c) 2017 - 2019 Dominik Lippl, Rufus Maiwald, Felix Schmid and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.bungee.listener;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import eu.mcone.coresystem.api.bungee.CoreSystem;
import eu.mcone.coresystem.api.bungee.event.LabyModMessageReceiveEvent;
import eu.mcone.coresystem.api.bungee.event.LabyModPlayerJoinEvent;
import eu.mcone.coresystem.api.bungee.event.PlayerSettingsChangeEvent;
import eu.mcone.coresystem.api.bungee.player.CorePlayer;
import eu.mcone.coresystem.api.core.labymod.LabyModConnection;
import eu.mcone.coresystem.api.core.player.PlayerSettings;
import eu.mcone.coresystem.bungee.BungeeCoreSystem;
import eu.mcone.coresystem.bungee.friend.Party;
import eu.mcone.coresystem.core.labymod.AddonManager;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class PluginMessageListener implements Listener {

    @EventHandler
    public void on(PluginMessageEvent e) {
        if (e.getTag().equalsIgnoreCase("BungeeCord")) {
            final DataInputStream in = new DataInputStream(new ByteArrayInputStream(e.getData()));
            final ProxiedPlayer p = ProxyServer.getInstance().getPlayer(e.getReceiver().toString());
            final CorePlayer cp = BungeeCoreSystem.getInstance().getCorePlayer(p);

            try {
                String mainChannel = in.readUTF();

                if (mainChannel.equalsIgnoreCase("MC_ONE_GET")) {
                    String uuid = in.readUTF();
                    String subch = in.readUTF();

                    List<String> out = new ArrayList<>();

                    if (subch.equalsIgnoreCase("FRIENDS")) {
                        StringBuilder result = new StringBuilder();
                        Map<UUID, String> friends = cp.getFriendData().getFriends();

                        for (Map.Entry<UUID, String> friend : friends.entrySet()) {
                            ProxiedPlayer f = ProxyServer.getInstance().getPlayer(friend.getKey());
                            String online = "§coffline";
                            if (f != null) online = "§aonline";

                            result.append(friend.getKey()).append(":").append(friend.getValue()).append(":").append(online).append(",");
                        }

                        out.add(result.toString());
                    } else if (subch.equalsIgnoreCase("PARTY")) {
                        StringBuilder result = new StringBuilder();
                        Party party = Party.getParty(p);

                        if (party != null) {
                            List<ProxiedPlayer> members = party.getMember();
                            for (ProxiedPlayer member : members) {
                                result.append(member.getName()).append(":").append(member.getServer().getInfo().getName());
                                if (member.equals(party.getLeader())) result.append(":leader");
                                result.append(",");
                            }

                            out.add(result.toString());
                        } else {
                            out.add("false");
                        }
                    } else if (subch.equalsIgnoreCase("SERVERS")) {
                        String modus = in.readUTF();

                        StringBuilder result = new StringBuilder();
                        for (ServerInfo s : ProxyServer.getInstance().getServers().values()) {
                            if (s.getName().toLowerCase().contains(modus.toLowerCase()) && s.canAccess(p)) {
                                result.append(s.getName()).append(":").append(s.getPlayers().size()).append(";");
                            }
                        }

                        out.add(result.toString());
                    }

                    CoreSystem.getInstance().getChannelHandler().createReturnRequest(p.getServer(), uuid, (String[]) out.toArray(new String[0]));
                } else if (mainChannel.equals("MC_ONE_SET")) {
                    String subch = in.readUTF();

                    if (subch.equalsIgnoreCase("CMD")) {
                        String input = in.readUTF();

                        ProxyServer.getInstance().getPluginManager().dispatchCommand(p, input);
                    } else if (subch.equalsIgnoreCase("CONNECT")) {
                        String target = in.readUTF();
                        ServerInfo si = ProxyServer.getInstance().getServerInfo(target);

                        if (si != null && si.canAccess(p)) {
                            p.connect(si);
                        }
                    } else if (subch.equalsIgnoreCase("UNNICK")) {
                        BungeeCoreSystem.getInstance().getNickManager().destroy(p);
                    } else if (subch.equalsIgnoreCase("PLAYER_SETTINGS")) {
                        ProxyServer.getInstance().getPluginManager().callEvent(new PlayerSettingsChangeEvent(
                                CoreSystem.getInstance().getCorePlayer(p),
                                CoreSystem.getInstance().getGson().fromJson(in.readUTF(), PlayerSettings.class)
                        ));
                    }
                }
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        } else if (e.getTag().equals("LABYMOD")) {
            if (!(e.getSender() instanceof ProxiedPlayer))
                return;

            final ProxiedPlayer p = (ProxiedPlayer) e.getSender();

            // Converting the byte array into a byte buffer
            ByteBuf buf = Unpooled.wrappedBuffer(e.getData());

            try {
                // Reading the version from the buffer
                final String version = BungeeCoreSystem.getSystem().getLabyModAPI().getApi().readString(buf, Short.MAX_VALUE);

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
                final String messageKey = BungeeCoreSystem.getSystem().getLabyModAPI().getApi().readString(buf, Short.MAX_VALUE);
                final String messageContents = BungeeCoreSystem.getSystem().getLabyModAPI().getApi().readString(buf, Short.MAX_VALUE);
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
