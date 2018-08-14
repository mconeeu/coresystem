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
import com.google.common.primitives.Ints;
import com.mongodb.client.model.UpdateOptions;
import eu.mcone.cloud.api.plugin.CloudAPI;
import eu.mcone.coresystem.api.bungee.CoreSystem;
import eu.mcone.coresystem.api.bungee.player.CorePlayer;
import eu.mcone.coresystem.api.core.exception.RuntimeCoreException;
import eu.mcone.coresystem.api.core.player.Group;
import eu.mcone.coresystem.bungee.BungeeCoreSystem;
import eu.mcone.coresystem.core.player.GlobalCorePlayer;
import eu.mcone.networkmanager.core.api.database.Database;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import org.bson.Document;

import javax.imageio.ImageIO;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.stream.IntStream;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Updates.*;

public class TeamspeakVerifier {

    private final static TS3Config CONFIG = new TS3Config()
            .setHost("f.rmlk-verwaltung.de")
            .setQueryPort(10011)
            .setFloodRate(TS3Query.FloodRate.UNLIMITED)
            .setReconnectStrategy(ReconnectStrategy.linearBackoff());

    private static final int VERIFIED_RANK = 23;

    private final static int[] RELEVANT_GROUPS = {
            VERIFIED_RANK, Group.SPIELVERDERBER.getTsId(), Group.PREMIUM.getTsId(), Group.PREMIUMPLUS.getTsId(), Group.YOUTUBER.getTsId()
    };

    private TS3Query query;
    private TS3ApiAsync api;
    private ServerQueryInfo serverQueryInfo;

    private Map<UUID, String> registering;
    private Map<UUID, TeamspeakIcon> icons;

    public TeamspeakVerifier() {
        this.registering = new HashMap<>();
        this.icons = new HashMap<>();

        this.query = new TS3Query(CONFIG);
        this.query.connect();

        this.api = query.getAsyncApi();
        this.api.login("mc1net", "icCjZTht");
        this.api.selectVirtualServerByPort(9987);
        this.api.setNickname(BungeeCoreSystem.getSystem().isCloudsystemAvailable() ? "[Bot] mc1net:" + CloudAPI.getInstance().getServerName() : "[Bot] mc1net:Bungee-1");

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
                    CorePlayer p = null;

                    for (CorePlayer player : CoreSystem.getInstance().getOnlineCorePlayers()) {
                        if (player.getName().equalsIgnoreCase(e.getMessage())) p = player;
                    }

                    if (p != null) {
                        if (registering.getOrDefault(p.getUuid(), "").equals(e.getInvokerUniqueId())) {
                            registering.remove(p.getUuid());
                            link(p, e.getInvokerUniqueId());
                        }
                    } else if (registering.containsValue(e.getInvokerUniqueId())) {
                        Set<UUID> toDelete = new HashSet<>();

                        api.sendPrivateMessage(e.getInvokerId(), "[b][color=darkred]Der Spieler [/color][color=red]" + e.getMessage() + "[/color][color=darkred] ist nicht online! Der Vorgang wurde abgebrochen.[/color][/b]");
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
                    api.getClientByUId(e.getUniqueClientIdentifier()).onSuccess(clientInfo -> {
                        System.out.println(Ints.asList(clientInfo.getServerGroups()));
                        if (!Ints.asList(clientInfo.getServerGroups()).contains(VERIFIED_RANK)) {
                            List<CorePlayer> players = new ArrayList<>();

                            for (CorePlayer p : BungeeCoreSystem.getInstance().getOnlineCorePlayers()) {
                                System.out.println(clientInfo.getIp() + " <-> " + p.getIpAdress());
                                if (clientInfo.getIp().equalsIgnoreCase(p.getIpAdress()) && !p.isTeamspeakIdLinked()) {
                                    players.add(p);
                                }
                            }

                            if (players.size() == 1) {
                                registering.put(players.get(0).getUuid(), clientInfo.getUniqueIdentifier());
                                api.sendPrivateMessage(clientInfo.getId(), "[b][color=grey]Bitte schreibe hier deinen [/color][color=white]Minecraftnamen[/color][color=grey], um deinen TeamSpeak Account zu verknüpfen und alle Funktionen des TeamSpeaks nutzen zu können![/color][/b]");
                            }
                        } else if (BungeeCoreSystem.getSystem().getMongoDB(Database.SYSTEM).getCollection("userinfo").find(eq("teamspeak_uid", clientInfo.getUniqueIdentifier())).first() == null) {
                            unsetLinkedGroups(clientInfo);
                            api.sendPrivateMessage(clientInfo.getId(), "[b][color=white]Dir wurde der Verifizierten-Rang entfernt, da du keinen Minecraft-Account mit deiner TeamSpeak-ID verlinkt hast.[/color][/b]");
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
        if (BungeeCoreSystem.getSystem().getMongoDB(Database.SYSTEM).getCollection("userinfo").find(eq("teamspeak_uid", ts3Uid)).first() != null) {
            BungeeCoreSystem.getInstance().getMessager().send(p, "§4Diese TeamSpeak ID wurde bereits von einem anderen Spieler registriert. Bitte melde dich bei unserem Support oder erstelle ein Support Ticket, wenn das deine ID ist.");
        } else {
            api.getClientByUId(ts3Uid)
                    .onSuccess(clientInfo -> {
                        BungeeCoreSystem.getInstance().getMessager().send(p, "§2Bitte wechsle zu deinem TeamSpeak Fenster und gib in dem gerade vom §f[Bot] mc1net§2 geöffneten Chat deinen §aMinecraft-Namen§2 ein, um den Vorgang abzuschließen.");
                        api.sendPrivateMessage(clientInfo.getId(), "[b][color=white]Um die Verknüpfung deines Minecraftaccounts abzuschließen gib bitte hier deinen [/color][color=darkcyan]Minecraftnamen[/color][color=white] ein:[/color][/b]");
                        registering.put(p.getUniqueId(), ts3Uid);
                    })
                    .onFailure(e -> BungeeCoreSystem.getInstance().getMessager().send(p, "§4Der TeamSpeak Account mit der angegeben ID ist nicht auf dem TeamSpeak Server online! (TS-IP: §cmcone.eu§4)"));
        }
    }

    public void unlink(CorePlayer p) {
        if (p.isTeamspeakIdLinked()) {
            api.isClientOnline(p.getTeamspeakUid()).onSuccess(online -> {
                if (online) {
                    api.getClientByUId(p.getTeamspeakUid()).onSuccess(clientInfo ->
                            removeIcon(p.getUuid(), clientInfo.getDatabaseId(), icons.get(p.getUuid()).getIconId(), () ->
                                    unsetLinkedGroups(clientInfo)
                            )
                    ).onFailure(Throwable::printStackTrace);
                } else {
                    api.getDatabaseClientByUId(p.getTeamspeakUid())
                            .onSuccess(databaseClientInfo -> removeIcon(p.getUuid(), databaseClientInfo.getDatabaseId(), icons.get(p.getUuid()).getIconId(), () -> {
                            }))
                            .onFailure(Throwable::printStackTrace);
                }

                ((GlobalCorePlayer) p).setTeamspeakUid(null);
                BungeeCoreSystem.getSystem().getMongoDB(Database.SYSTEM).getCollection("userinfo").updateOne(eq("uuid", p.getUuid().toString()), unset("teamspeak_uid"));
            });
        } else {
            throw new RuntimeCoreException("Player " + p.getName() + " has no linked Teamspeak-UID!");
        }
    }

    private void link(CorePlayer p, String ts3Uid) {
        ((GlobalCorePlayer) p).setTeamspeakUid(ts3Uid);
        BungeeCoreSystem.getSystem().getMongoDB(Database.SYSTEM).getCollection("userinfo").updateOne(eq("uuid", p.getUuid().toString()), set("teamspeak_uid", ts3Uid));

        updateLink(p, clientInfo -> {
            api.sendPrivateMessage(clientInfo.getId(), "[b][color=green]Du hast deine TeamSpeak Identität erfolgreich mit deinem Minecraftaccount verknüpft![/color][/b]").onFailure(Throwable::printStackTrace);
            BungeeCoreSystem.getInstance().getMessager().send(p.bungee(), "§2Deine TeamSpeak Identität wurde erfolgreich verknüpft!");
        });
    }

    public void updateLink(CorePlayer p, CommandFuture.SuccessListener<ClientInfo> listener) {
        if (p.isTeamspeakIdLinked()) {
            api.isClientOnline(p.getTeamspeakUid()).onSuccess(online -> {
                if (online) {
                    api.getClientByUId(p.getTeamspeakUid()).onSuccess(clientInfo -> {
                        try {
                            updateIcon(p.getUuid(), clientInfo);
                            updateLinkedGroups(p, clientInfo);

                            if (listener != null) listener.handleSuccess(clientInfo);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }).onFailure(Throwable::printStackTrace);
                }
            });
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

    private void updateLinkedGroups(CorePlayer p, ClientInfo clientInfo) {
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
        for (Document iconDocuments : BungeeCoreSystem.getSystem().getMongoDB(Database.SYSTEM).getDocumentsInCollection("bungeesystem_teamspeak_icons")) {
            icons.put(UUID.fromString(iconDocuments.getString("uuid")), new TeamspeakIcon(iconDocuments.get("bytes", org.bson.types.Binary.class).getData(), iconDocuments.getLong("icon_id")));
        }
    }

    private void updateIcon(UUID uuid, ClientInfo clientInfo) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream(128);
        ImageIO.write(ImageIO.read(new URL("https://crafatar.com/avatars/" + uuid + "?size=16")), "PNG", out);

        if (icons.containsKey(uuid)) {
            TeamspeakIcon icon = icons.get(uuid);

            if (Arrays.equals(icon.getBytes(), out.toByteArray())) {
                System.out.println("icon byte arrays equal");
            } else {
                long iconId = icon.getIconId();
                boolean delete = true;

                for (HashMap.Entry<UUID, TeamspeakIcon> e : icons.entrySet()) {
                    if (e.getValue().getIconId() == iconId) {
                        delete = false;
                        break;
                    }
                }

                if (delete) {
                    removeIcon(uuid, clientInfo.getDatabaseId(), iconId, () -> addIcon(out.toByteArray(), uuid, clientInfo));
                } else {
                    addIcon(out.toByteArray(), uuid, clientInfo);
                }
            }
        } else {
            addIcon(out.toByteArray(), uuid, clientInfo);
        }
    }

    private void addIcon(byte[] bytes, UUID uuid, ClientInfo clientInfo) {
        api.uploadIconDirect(bytes).onSuccess(iconId -> {
            long icon = iconId;

            System.out.println("uploaded icon direct " + iconId);
            api.addClientPermission(clientInfo.getDatabaseId(), "i_icon_id", (int) icon, false).onFailure(Throwable::printStackTrace);

            BungeeCoreSystem.getSystem().getMongoDB(Database.SYSTEM).getCollection("bungeesystem_teamspeak_icons").updateOne(
                    eq("uuid", uuid.toString()),
                    combine(
                            setOnInsert("uuid", uuid.toString()),
                            set("icon_id", iconId),
                            set("bytes", bytes)
                    ),
                    new UpdateOptions().upsert(true)
            );

            icons.put(uuid, new TeamspeakIcon(bytes, iconId));
        }).onFailure(Throwable::printStackTrace);
    }

    private void removeIcon(UUID uuid, int databaseId, long iconId, Runnable then) {
        api.deleteClientPermission(databaseId, "i_icon_id").onSuccess(aVoid ->
                api.deleteIcon(iconId).onSuccess(bVoid -> {
                    BungeeCoreSystem.getSystem().getMongoDB(Database.SYSTEM).getCollection("bungeesystem_teamspeak_icons").deleteMany(eq("uuid", uuid.toString()));

                    icons.remove(uuid);
                    System.out.println("deleted icon " + icons.get(uuid));
                    then.run();
                }).onFailure(Throwable::printStackTrace)
        ).onFailure(Throwable::printStackTrace);
    }

}
