/*
 * Copyright (c) 2017 - 2019 Dominik Lippl, Rufus Maiwald, Felix Schmid and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.bungee.utils.bots.teamspeak;

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
import com.github.theholywaffle.teamspeak3.api.wrapper.Client;
import com.github.theholywaffle.teamspeak3.api.wrapper.ClientInfo;
import com.github.theholywaffle.teamspeak3.api.wrapper.ServerQueryInfo;
import com.google.common.primitives.Ints;
import com.mongodb.client.model.UpdateOptions;
import eu.mcone.cloud.api.plugin.CloudAPI;
import eu.mcone.coresystem.api.bungee.player.CorePlayer;
import eu.mcone.coresystem.api.core.exception.RuntimeCoreException;
import eu.mcone.coresystem.api.core.player.Group;
import eu.mcone.coresystem.bungee.BungeeCoreSystem;
import eu.mcone.coresystem.core.player.GlobalOfflineCorePlayer;
import group.onegaming.networkmanager.core.api.database.Database;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
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

//TODO: Make TeamspeakVerifier as NetworkHandler module
public class TeamspeakVerifier {

    private final static TS3Config CONFIG = new TS3Config()
            .setHost("192.168.2.11")
            .setQueryPort(10011)
            .setFloodRate(TS3Query.FloodRate.UNLIMITED)
            .setReconnectStrategy(ReconnectStrategy.linearBackoff());

    private static final int VERIFIED_RANK = 23;

    private final static int[] RELEVANT_GROUPS = {
            VERIFIED_RANK, Group.SPIELVERDERBER.getTsId(), Group.PREMIUM.getTsId(), Group.PREMIUMPLUS.getTsId(), Group.CREATOR.getTsId()
    };

    private TS3Query query;
    private TS3ApiAsync api;
    private ServerQueryInfo serverQueryInfo;

    private Map<UUID, String> registering;
    private Map<UUID, Integer> registeringCodes;
    private Map<String, UUID> ingameUniqueIds;
    private Map<UUID, TeamspeakIcon> icons;

    public TeamspeakVerifier() {
        this.registering = new HashMap<>();
        this.registeringCodes = new HashMap<>();
        this.ingameUniqueIds = new HashMap<>();
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

    public void sendClientsWithIP(CorePlayer player) {
        //TODO: Implement code system in addRegistering();

        String ip = player.getIpAdress();
        List<Client> results = new ArrayList<>();

        api.getClients().onSuccess(x -> {
            x.forEach(c -> {
                if (c.getIp().equalsIgnoreCase(ip)) {
                    results.add(c);
                }
            });
            if (results.size() == 0) {
                BungeeCoreSystem.getInstance().getMessenger().send(player.bungee(), "§4Ein TeamSpeak Account mit deiner IP-Adresse ist nicht online.");
            } else {
                BungeeCoreSystem.getInstance().getMessenger().send(player.bungee(), "Folgende Identitäten wurden gefunden. Bitte wähle deine aus:");
                results.forEach(v -> {
                    TextComponent tp = new TextComponent("§8 » §f" + v.getNickname());
                    tp.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Klicke hier um dich mit diesem Account zu verbinden").create()));
                    tp.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/ts uidlink " + v.getUniqueIdentifier()));
                    player.bungee().sendMessage(tp);
                });
            }
        });
    }

    public int randInt(int min, int max) {
        return new Random().nextInt((max - min) + 1) + min;
    }

    private void registerEventsAndListeners() {
        api.registerEvents(TS3EventType.TEXT_PRIVATE, TS3EventType.SERVER).onFailure(Throwable::printStackTrace);
        api.addTS3Listeners(new TS3EventAdapter() {
            @Override
            public void onTextMessage(TextMessageEvent e) {
                CorePlayer p = null;
                if (ingameUniqueIds.containsKey(e.getInvokerUniqueId())) {
                    p = BungeeCoreSystem.getSystem().getCorePlayer(ingameUniqueIds.remove(e.getInvokerUniqueId()));
                }
                if (e.getTargetMode().equals(TextMessageTargetMode.CLIENT) && e.getInvokerId() != serverQueryInfo.getId()) {
                    if (p != null) {
                        if (registering.getOrDefault(p.getUuid(), "").equals(e.getInvokerUniqueId())) {
                            String message = e.getMessage();

                            Integer code = null;
                            try {
                                code = Integer.parseInt(message);
                            } catch (NumberFormatException ignored) {
                            }

                            if(code != null) {
                                if(registeringCodes.remove(p.getUuid()).equals(code)) {
                                    registering.remove(p.getUuid());
                                    link(p, e.getInvokerUniqueId());
                                    return;
                                }
                            }
                            registeringCodes.remove(p.getUuid());
                            registering.remove(p.getUuid());
                            api.sendPrivateMessage(e.getInvokerId(), "[b][color=darkred]Der angegebene Code ist inkorrekt. Vorgang abgebrochen.[/color][/b]");
                        }
                    } else if (registering.containsValue(e.getInvokerUniqueId())) {
                        Set<UUID> toDelete = new HashSet<>();

                        api.sendPrivateMessage(e.getInvokerId(), "[b][color=darkred]Der Spieler [/color][color=red]" + e.getMessage() + "[/color][color=darkred] ist nicht online! Der Verifizierungsvorgang wurde abgebrochen.[/color][/b]");
                        for (HashMap.Entry<UUID, String> entry : registering.entrySet()) {
                            if (entry.getValue().equals(e.getInvokerUniqueId())) {
                                BungeeCoreSystem.getInstance().getMessenger().send(ProxyServer.getInstance().getPlayer(entry.getKey()), "§4Der Verknüpfungsvorgang wurde abgebrochen, da der TeamSpeak Client einen anderen Minecraftnamen angegeben hat.");
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
                            api.sendPrivateMessage(clientInfo.getId(), "[b][color=white]Dir wurde der Verifizierten-Rang entfernt, da du keinen OneNetwork-Account mit deiner TeamSpeak-ID verlinkt hast.[/color][/b]");
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
            BungeeCoreSystem.getInstance().getMessenger().send(p, "§4Diese TeamSpeak ID wurde bereits von einem anderen Spieler registriert. Bitte melde dich bei unserem Support oder erstelle ein Support Ticket, wenn das deine ID ist.");
        } else {
            api.getClientByUId(ts3Uid)
                    .onSuccess(clientInfo -> {
                        int code = randInt(1000, 9999);
                        registeringCodes.put(p.getUniqueId(), code);
                        BungeeCoreSystem.getInstance().getMessenger().send(p, "§2Bitte wechsle zu deinem TeamSpeak Fenster und gib in dem gerade vom §f[Bot] mc1net§2 geöffneten Chat folgenden Code ein, um den Vorgang abzuschließen: §e" + code);
                        api.sendPrivateMessage(clientInfo.getId(), "[b]Es wird versucht, sich mit deinem Account zu verbinden. Bitte gebe den in Minecraft erhaltenen Code hier ein:[/b]");
                        registering.put(p.getUniqueId(), ts3Uid);
                        ingameUniqueIds.put(ts3Uid, p.getUniqueId());
                    })
                    .onFailure(e -> BungeeCoreSystem.getInstance().getMessenger().send(p, "§4Der TeamSpeak Account mit der angegeben ID ist nicht auf dem TeamSpeak Server online! (TS-IP: §cmcone.eu§4)"));
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

                ((GlobalOfflineCorePlayer) p).setTeamspeakUid(null);
                BungeeCoreSystem.getSystem().getMongoDB(Database.SYSTEM).getCollection("userinfo").updateOne(eq("uuid", p.getUuid().toString()), unset("teamspeak_uid"));
            });
        } else {
            throw new RuntimeCoreException("Player " + p.getName() + " has no linked Teamspeak-UID!");
        }
    }

    private void link(CorePlayer p, String ts3Uid) {
        ((GlobalOfflineCorePlayer) p).setTeamspeakUid(ts3Uid);
        BungeeCoreSystem.getSystem().getMongoDB(Database.SYSTEM).getCollection("userinfo").updateOne(eq("uuid", p.getUuid().toString()), set("teamspeak_uid", ts3Uid));

        updateLink(p, clientInfo -> {
            api.sendPrivateMessage(clientInfo.getId(), "[b][color=green]Du hast deine TeamSpeak Identität erfolgreich mit deinem Minecraftaccount verknüpft![/color][/b]").onFailure(Throwable::printStackTrace);
            BungeeCoreSystem.getInstance().getMessenger().send(p.bungee(), "§2Deine TeamSpeak Identität wurde erfolgreich verknüpft!");
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
            if (new ArrayList<>(Arrays.asList(Group.PREMIUM, Group.PREMIUMPLUS, Group.CREATOR)).contains(Group.getGroupByTsId(groupId)) && groupId > 0) {
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
        for (Document iconDocuments : BungeeCoreSystem.getSystem().getMongoDB(Database.SYSTEM).getCollection("bungeesystem_teamspeak_icons").find()) {
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

    public boolean isBotRunning() {
        return query.isConnected();
    }

}
