/*
 * Copyright (c) 2017 - 2018 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.coresystem.bungee.utils;

import com.github.theholywaffle.teamspeak3.TS3ApiAsync;
import com.github.theholywaffle.teamspeak3.TS3Config;
import com.github.theholywaffle.teamspeak3.TS3Query;
import com.github.theholywaffle.teamspeak3.api.CommandFuture;
import com.github.theholywaffle.teamspeak3.api.TextMessageTargetMode;
import com.github.theholywaffle.teamspeak3.api.event.ClientJoinEvent;
import com.github.theholywaffle.teamspeak3.api.event.TS3EventAdapter;
import com.github.theholywaffle.teamspeak3.api.event.TS3EventType;
import com.github.theholywaffle.teamspeak3.api.event.TextMessageEvent;
import com.github.theholywaffle.teamspeak3.api.reconnect.ReconnectStrategy;
import com.github.theholywaffle.teamspeak3.api.wrapper.ClientInfo;
import com.github.theholywaffle.teamspeak3.api.wrapper.ServerQueryInfo;
import eu.mcone.cloud.api.plugin.CloudAPI;
import eu.mcone.coresystem.api.bungee.player.BungeeCorePlayer;
import eu.mcone.coresystem.api.core.exception.RuntimeCoreException;
import eu.mcone.coresystem.api.core.player.Group;
import eu.mcone.coresystem.bungee.BungeeCoreSystem;
import eu.mcone.coresystem.core.mysql.Database;
import eu.mcone.coresystem.core.player.GlobalCorePlayer;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import javax.imageio.ImageIO;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.IntStream;

public class TeamspeakVerifier {

    private final static TS3Config CONFIG = new TS3Config()
            .setHost("f.rmlk-verwaltung.de")
            .setQueryPort(10011)
            .setFloodRate(TS3Query.FloodRate.UNLIMITED)
            .setReconnectStrategy(ReconnectStrategy.linearBackoff())
            .setEnableCommunicationsLogging(true);

    private final static int[] RELEVANT_GROUPS = {
            23, Group.SPIELVERDERBER.getTsId(), Group.PREMIUM.getTsId(), Group.PREMIUMPLUS.getTsId(), Group.YOUTUBER.getTsId()
    };

    private TS3Query query;
    private TS3ApiAsync api;
    private ServerQueryInfo serverQueryInfo;

    private Map<UUID, String> registering;
    private Map<UUID, Long> icons;

    public TeamspeakVerifier() {
        this.registering = new HashMap<>();
        this.icons = new HashMap<>();

        this.query = new TS3Query(CONFIG);
        this.query.connect();

        this.api = query.getAsyncApi();
        this.api.login("mc1net", "icCjZTht");
        this.api.selectVirtualServerByPort(9987);
        this.api.setNickname(BungeeCoreSystem.isCloudsystemAvailable() ? "[Bot] mc1net:" + CloudAPI.getInstance().getServerName() : "[Bot] mc1net:Bungee-1");

        api.whoAmI().onSuccess(serverQueryInfo -> this.serverQueryInfo = serverQueryInfo);

        loadIcons();
        registerEventsAndListeners();
    }

    private void registerEventsAndListeners() {
        api.registerEvents(TS3EventType.TEXT_PRIVATE, TS3EventType.SERVER).onFailure(Throwable::printStackTrace);
        api.addTS3Listeners(new TS3EventAdapter() {
            @Override
            public void onTextMessage(TextMessageEvent e) {
                if (e.getTargetMode().equals(TextMessageTargetMode.CLIENT) && e.getInvokerId() != serverQueryInfo.getId()) {
                    BungeeCorePlayer p = BungeeCoreSystem.getInstance().getCorePlayer(e.getMessage());

                    if (p != null) {
                        if (registering.getOrDefault(p.getUuid(), "").equals(e.getInvokerUniqueId())) {
                            registering.remove(p.getUuid());
                            link(p, e.getInvokerUniqueId());
                        }
                    } else if (registering.containsValue(e.getInvokerUniqueId())) {
                        Set<UUID> toDelete = new HashSet<>();

                        api.sendPrivateMessage(e.getInvokerId(), "Der Spieler " + e.getMessage() + " ist nicht online! Der Vorgang wurde abgebrochen.");
                        for (HashMap.Entry<UUID, String> entry : registering.entrySet()) {
                            if (entry.getValue().equals(e.getInvokerUniqueId())) {
                                BungeeCoreSystem.getInstance().getMessager().send(ProxyServer.getInstance().getPlayer(entry.getKey()), "§4Der Verknüpfungsvorgang wurde abgebrochen, da der TeamSpeak Client einen anderen Minecraftnamen angegeben hat.");
                                toDelete.add(entry.getKey());
                            }
                        }

                        toDelete.forEach(uuid -> registering.remove(uuid));
                    }
                }
            }

            @Override
            public void onClientJoin(ClientJoinEvent e) {
                if (e.getClientType() == 0) {
                    final String uid = e.getUniqueClientIdentifier();

                    api.getClientByUId(uid).onSuccess(clientInfo -> {
                        List<BungeeCorePlayer> players = new ArrayList<>();

                        for (BungeeCorePlayer p : BungeeCoreSystem.getInstance().getOnlineCorePlayers()) {
                            String ip = p.bungee().getAddress().toString().split("/")[1].split(":")[0];

                            if (clientInfo.getIp().equalsIgnoreCase(ip) && !p.isTeamspeakIdLinked()) {
                                players.add(p);
                            }
                        }

                        if (players.size() == 1) {
                            registering.put(players.get(0).getUuid(), clientInfo.getUniqueIdentifier());
                            api.sendPrivateMessage(clientInfo.getId(), "Bitte schreibe hier deinen Minecraftnamen, um deinen TeamSpeak Account zu verknüpfen und alle Funktionen des TeamSpeaks nutzen zu können!");
                        }
                    }).onFailure(Throwable::printStackTrace);
                }
            }
        });
    }

    public void close() {
        query.exit();
    }

    public void addRegistering(ProxiedPlayer p, String ts3Uid) {
        api.getClientByUId(ts3Uid)
                .onSuccess(clientInfo -> {
                    api.sendPrivateMessage(clientInfo.getId(), "Um die Verknüpfung deines Minecraftaccounts abzuschließen gib bitte hier deinen Minecraft-Namen ein:");
                    registering.put(p.getUniqueId(), ts3Uid);
                })
                .onFailure(e -> BungeeCoreSystem.getInstance().getMessager().send(p, "§4Der TeamSpeak Account mit der angegeben ID ist nicht auf dem TeamSpeak Server online! (TS-IP: §cmcone.eu§4)"));
    }

    public void unlink(BungeeCorePlayer p) {
        if (p.isTeamspeakIdLinked()) {
            api.getClientByUId(p.getTeamspeakUid()).onSuccess(clientInfo -> {
                removeIcon(p.getUuid(), clientInfo);
                unsetLinkedGroups(clientInfo);

                ((GlobalCorePlayer) p).setTeamspeakUid(null);
                BungeeCoreSystem.getSystem().getMySQL(Database.SYSTEM).update("UPDATE userinfo SET teamspeak_uid=NULL WHERE uuid='" + p.getUuid() + "'");
            }).onFailure(Throwable::printStackTrace);
        } else {
            throw new RuntimeCoreException("Player " + p.getName() + " has no linked Teamspeak-UID!");
        }
    }

    private void link(BungeeCorePlayer p, String ts3Uid) {
        ((GlobalCorePlayer) p).setTeamspeakUid(ts3Uid);
        BungeeCoreSystem.getSystem().getMySQL(Database.SYSTEM).update("UPDATE userinfo SET teamspeak_uid='" + ts3Uid + "' WHERE uuid='" + p.getUuid() + "'");

        updateLink(p, clientInfo -> {
            api.sendPrivateMessage(clientInfo.getId(), "Du hast deine TeamSpeak Identität erfolgreich mit deinem Minecraftaccount verknüpft!").onFailure(Throwable::printStackTrace);
            BungeeCoreSystem.getInstance().getMessager().send(p.bungee(), "§2Deine TeamSpeak Identität wurde erfolgreich verknüpft!");
        });
    }

    public void updateLink(BungeeCorePlayer p, CommandFuture.SuccessListener<ClientInfo> listener) {
        if (p.isTeamspeakIdLinked()) {
            api.getClientByUId(p.getTeamspeakUid()).onSuccess(clientInfo -> {
                try {
                    updateIcon(p.getUuid(), clientInfo);
                    updateLinkedGroups(p, clientInfo);

                    if (listener != null) listener.handleSuccess(clientInfo);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).onFailure(Throwable::printStackTrace);
        } else {
            throw new RuntimeCoreException("Player " + p.getName() + " has no linked Teamspeak-UID!");
        }
    }

    private void unsetLinkedGroups(ClientInfo clientInfo) {
        List<Integer> groupIds = new ArrayList<>();
        groupIds.add(23);
        for (int groupId : clientInfo.getServerGroups()) {
            if (new ArrayList<>(Arrays.asList(Group.PREMIUM, Group.PREMIUMPLUS, Group.YOUTUBER)).contains(Group.getGroupByTsId(groupId)) && groupId > 0) {
                groupIds.add(groupId);
            }
        }

        for (int id : groupIds) {
            api.removeClientFromServerGroup(id, clientInfo.getDatabaseId());
        }
    }

    private void updateLinkedGroups(BungeeCorePlayer p, ClientInfo clientInfo) {
        List<Integer> tsGroups = new ArrayList<>();
        for (int groupId : clientInfo.getServerGroups()) {
            if (IntStream.of(RELEVANT_GROUPS).anyMatch(x -> x == groupId)) tsGroups.add(groupId);
        }
        List<Integer> coreGroups = new ArrayList<>();
        coreGroups.add(23);
        for (Group group : p.getGroups()) {
            if (IntStream.of(RELEVANT_GROUPS).anyMatch(x -> x == group.getTsId()) && group.getTsId() > 0)
                coreGroups.add(group.getTsId());
        }

        for (int tsGroup : tsGroups) {
            if (!coreGroups.contains(tsGroup)) {
                api.removeClientFromServerGroup(tsGroup, clientInfo.getDatabaseId()).onFailure(Throwable::printStackTrace);
            }
        }
        for (int coreGroup : coreGroups) {
            if (!tsGroups.contains(coreGroup)) {
                api.addClientToServerGroup(coreGroup, clientInfo.getDatabaseId()).onFailure(Throwable::printStackTrace);
            }
        }
    }

    private void loadIcons() {
        BungeeCoreSystem.getSystem().getMySQL(Database.SYSTEM).select("SELECT * FROM bungeesystem_teamspeak_icons", rs -> {
            try {
                while (rs.next()) {
                    icons.put(UUID.fromString(rs.getString("uuid")), rs.getLong("icon_id"));
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    private void updateIcon(UUID uuid, ClientInfo clientInfo) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream(128);
        ImageIO.write(ImageIO.read(new URL("https://crafatar.com/avatars/" + uuid + "?size=16")), "PNG", out);

        api.uploadIconDirect(out.toByteArray()).onSuccess(iconId -> {
            if (icons.containsKey(uuid)) removeIcon(uuid, clientInfo);
            addIcon(uuid, clientInfo, out, iconId);
        }).onFailure(Throwable::printStackTrace);
    }

    private void addIcon(UUID uuid, ClientInfo clientInfo, ByteArrayOutputStream img, long iconId) {
        api.uploadIconDirect(img.toByteArray()).onSuccess(aVoid -> {
            api.addClientPermission(clientInfo.getDatabaseId(), "i_icon_id", (int) iconId, false).onFailure(Throwable::printStackTrace);
            BungeeCoreSystem.getSystem().getMySQL(Database.SYSTEM).update("INSERT INTO bungeesystem_teamspeak_icons (uuid, icon_id) VALUES ('" + uuid + "', " + iconId + ") ON DUPLICATE KEY UPDATE icon_id=" + iconId);
            icons.put(uuid, iconId);
        }).onFailure(Throwable::printStackTrace);
    }

    private void removeIcon(UUID uuid, ClientInfo clientInfo) {
        api.deleteClientPermission(clientInfo.getDatabaseId(), "i_icon_id").onSuccess(aVoid -> {
            api.deleteIcon(icons.get(uuid)).onFailure(Throwable::printStackTrace);
            BungeeCoreSystem.getSystem().getMySQL(Database.SYSTEM).update("DELETE FROM bungeesystem_teamspeak_icons WHERE uuid='" + uuid + "'");
            icons.remove(uuid);
        }).onFailure(Throwable::printStackTrace);
    }

}
