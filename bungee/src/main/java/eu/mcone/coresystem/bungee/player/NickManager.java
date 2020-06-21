/*
 * Copyright (c) 2017 - 2020 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.bungee.player;

import com.mongodb.client.MongoCollection;
import eu.mcone.coresystem.api.bungee.CoreSystem;
import eu.mcone.coresystem.api.bungee.player.CorePlayer;
import eu.mcone.coresystem.api.core.player.Nick;
import eu.mcone.coresystem.bungee.BungeeCoreSystem;
import group.onegaming.networkmanager.core.api.database.Database;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.connection.Server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Updates.inc;

public class NickManager implements eu.mcone.coresystem.api.bungee.player.NickManager {

    private static final Random NICK_CHOOSE_RANDOM = new Random();
    private static final MongoCollection<Nick> NICKS_COLLECTION = BungeeCoreSystem.getSystem().getMongoDB(Database.SYSTEM).getCollection("nicks", Nick.class);

    private final BungeeCoreSystem instance;
    private final HashMap<Nick, ProxiedPlayer> nicks;

    public NickManager(BungeeCoreSystem instance) {
        this.instance = instance;
        this.nicks = new HashMap<>();

        reload();
    }

    @Override
    public void reload() {
        nicks.clear();

        for (Nick nick : NICKS_COLLECTION.find()) {
            nicks.put(nick, null);
        }
    }

    private Nick getNick() {
        List<Nick> available = new ArrayList<>();
        nicks.forEach((skin, p) -> {
            if (p == null) available.add(skin);
        });

        if (!nicks.isEmpty()) {
            return available.get(NICK_CHOOSE_RANDOM.nextInt(available.size()));
        } else {
            return null;
        }
    }

    @Override
    public void nick(ProxiedPlayer p) {
        CorePlayer cp = instance.getCorePlayer(p);
        Nick nick = cp.getCurrentNick() != null ? cp.getCurrentNick() : getNick();
        nicks.put(nick, p);

        if (nick != null) {
            sendNickRequest(p, nick, true);
            ((BungeeCorePlayer) cp).setCurrentNick(nick);
            ((BungeeCorePlayer) cp).setNicked(true);
        } else {
            BungeeCoreSystem.getInstance().getMessenger().send(p, "§4Es ist kein Nickname mehr verfügbar!");
        }
    }

    @Override
    public void unnick(ProxiedPlayer p) {
        CorePlayer cp = instance.getCorePlayer(p);
        CoreSystem.getInstance().getChannelHandler().createInfoRequest(p, "UNNICK");

        NICKS_COLLECTION.updateOne(
                eq("name", cp.getCurrentNick().getName()),
                inc("onlinetime", NICK_CHOOSE_RANDOM.nextInt(100))
        );
        nicks.put(cp.getCurrentNick(), null);

        ((BungeeCorePlayer) cp).setCurrentNick(null);
        ((BungeeCorePlayer) cp).setNicked(false);
    }

    @Override
    public void refreshNicks(Server server) {
        for (ProxiedPlayer p : server.getInfo().getPlayers()) {
            serverSwitched(p);
        }
    }

    @Override
    public void serverSwitched(ProxiedPlayer p) {
        CorePlayer cp = BungeeCoreSystem.getSystem().getCorePlayer(p);

        if (cp.isNicked()) {
            sendNickRequest(p, cp.getCurrentNick(), false);
        }
    }

    @Override
    public void destroy(ProxiedPlayer p) {
        CorePlayer cp = instance.getCorePlayer(p);
        nicks.put(cp.getCurrentNick(), null);
        ((BungeeCorePlayer) cp).setCurrentNick(null);
        ((BungeeCorePlayer) cp).setNicked(false);
    }

    private static void sendNickRequest(ProxiedPlayer p, Nick nick, boolean notify) {
        CoreSystem.getInstance().getChannelHandler().createInfoRequest(p,
                "NICK",
                nick.getName(),
                nick.getGroup().toString(),
                nick.getSkinInfo().getValue(),
                nick.getSkinInfo().getSignature(),
                String.valueOf(nick.getCoins()),
                String.valueOf(nick.getOnlineTime()),
                Boolean.toString(notify)
        );
    }

}
