/*
 * Copyright (c) 2017 - 2019 Dominik Lippl, Rufus Maiwald, Felix Schmid and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.bungee.player;

import com.mongodb.client.MongoCollection;
import eu.mcone.coresystem.api.bungee.CoreSystem;
import eu.mcone.coresystem.api.bungee.player.CorePlayer;
import eu.mcone.coresystem.api.core.exception.SkinNotFoundException;
import eu.mcone.coresystem.api.core.player.Nick;
import eu.mcone.coresystem.api.core.player.SkinInfo;
import eu.mcone.coresystem.api.core.util.Random;
import eu.mcone.coresystem.bungee.BungeeCoreSystem;
import eu.mcone.networkmanager.core.api.database.Database;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class NickManager implements eu.mcone.coresystem.api.bungee.player.NickManager {

    private final MongoCollection<Nick> nicksCollection;

    private final BungeeCoreSystem instance;
    private HashMap<Nick, ProxiedPlayer> nicks;

    public NickManager(BungeeCoreSystem instance) {
        this.instance = instance;
        nicksCollection = instance.getMongoDB(Database.SYSTEM).getCollection("bungeesystem_nicks", Nick.class);
        reload();
    }

    public void reload() {
        nicks = new HashMap<>();

        for (Nick nick : nicksCollection.find()) {
            try {
                SkinInfo info = instance.getPlayerUtils().getSkinFromSkinDatabase(nick.getTexture());

                if (info != null) {
                    nick.setSkinInfo(info);
                    nicks.put(nick, null);
                }
            } catch (SkinNotFoundException e) {
                instance.sendConsoleMessage("§4Cannot find skin data for skin name " + nick.getTexture());
            }
        }

//        for (Document document_nicks : instance.getMongoDB(Database.SYSTEM).getCollection("bungeesystem_nicks").find()) {
//            for (Document document_textures : instance.getMongoDB(Database.SYSTEM).getCollection("bungeesystem_textures").find()) {
//                if (document_nicks.getString("texture").equalsIgnoreCase(document_textures.getString("name"))) {
//                    nicks.put(instance.getPlayerUtils().constructSkinInfo(document_nicks.getString("name"), document_textures.getString("texture_value"), document_textures.getString("texture_signature")), null);
//                }
//            }
//        }
    }

    private Nick getNick() {
        List<Nick> available = new ArrayList<>();
        nicks.forEach((skin, p) -> {
            if (p == null) available.add(skin);
        });

        if (!nicks.isEmpty())
            return available.get(Random.randomInt(0, available.size() - 1));

        return null;
    }

    public void nick(ProxiedPlayer p) {
        CorePlayer cp = instance.getCorePlayer(p);
        Nick nick = cp.getCurrentNick() != null ? cp.getCurrentNick() : getNick();
        nicks.put(nick, p);

        if (nick != null) {
            CoreSystem.getInstance().getChannelHandler().createInfoRequest(p,
                    "NICK",
                    nick.getTexture(),
                    nick.getSkinInfo().getValue(),
                    nick.getSkinInfo().getSignature(),
                    nick.getName(),
                    nick.getGroup().toString(),
                    String.valueOf(nick.getCoins()),
                    String.valueOf(nick.getOnlineTime())
            );

            ((BungeeCorePlayer) cp).setCurrentNick(nick);
            ((BungeeCorePlayer) cp).setNicked(true);
        } else {
            BungeeCoreSystem.getInstance().getMessenger().send(p, "§4Es ist kein Nickname mehr verfügbar!");
        }
    }

    public void unnick(ProxiedPlayer p) {
        CorePlayer cp = instance.getCorePlayer(p);
        CoreSystem.getInstance().getChannelHandler().createInfoRequest(p, "UNNICK");
        nicks.put(cp.getCurrentNick(), null);
        ((BungeeCorePlayer) cp).setCurrentNick(null);
        ((BungeeCorePlayer) cp).setNicked(false);
    }

    public void destroy(ProxiedPlayer p) {
        CorePlayer cp = instance.getCorePlayer(p);
        nicks.put(cp.getCurrentNick(), null);
        ((BungeeCorePlayer) cp).setCurrentNick(null);
        ((BungeeCorePlayer) cp).setNicked(false);
    }

}
