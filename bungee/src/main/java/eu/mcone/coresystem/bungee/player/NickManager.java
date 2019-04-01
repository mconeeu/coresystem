/*
 * Copyright (c) 2017 - 2019 Dominik Lippl, Rufus Maiwald, Felix Schmid and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.bungee.player;

import eu.mcone.coresystem.api.bungee.CoreSystem;
import eu.mcone.coresystem.api.bungee.player.CorePlayer;
import eu.mcone.coresystem.api.core.player.SkinInfo;
import eu.mcone.coresystem.api.core.util.Random;
import eu.mcone.coresystem.bungee.BungeeCoreSystem;
import eu.mcone.networkmanager.core.api.database.Database;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import org.bson.Document;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class NickManager implements eu.mcone.coresystem.api.bungee.player.NickManager {

    private final BungeeCoreSystem instance;
    private HashMap<SkinInfo, ProxiedPlayer> nicks;

    public NickManager(BungeeCoreSystem instance) {
        this.instance = instance;
        reload();
    }

    public void reload() {
        nicks = new HashMap<>();

        for (Document document_nicks : instance.getMongoDB(Database.SYSTEM).getCollection("bungeesystem_nicks").find()) {
            for (Document document_textures : instance.getMongoDB(Database.SYSTEM).getCollection("bungeesystem_textures").find()) {
                if (document_nicks.getString("texture").equalsIgnoreCase(document_textures.getString("name"))) {
                    nicks.put(instance.getPlayerUtils().constructSkinInfo(document_nicks.getString("name"), document_textures.getString("texture_value"), document_textures.getString("texture_signature")), null);
                }
            }
        }
    }

    private SkinInfo getNick() {
        List<SkinInfo> available = new ArrayList<>();
        nicks.forEach((skin, p) -> {
            if (p == null) available.add(skin);
        });

        return available.get(Random.randomInt(0, available.size()-1));
    }

    public void nick(ProxiedPlayer p) {
        CorePlayer cp = instance.getCorePlayer(p);
        SkinInfo nick = cp.getNickedSkin() != null ? cp.getNickedSkin() : getNick();
        nicks.put(nick, p);

        if (nick != null) {
            CoreSystem.getInstance().getChannelHandler().createInfoRequest(p, "NICK", nick.getName(), nick.getValue(), nick.getSignature());
            ((BungeeCorePlayer) cp).setNickedSkin(nick);
            ((BungeeCorePlayer) cp).setNicked(true);
        } else {
            BungeeCoreSystem.getInstance().getMessager().send(p, "§4Es ist kein Nickname mehr verfügbar!");
        }
    }

    public void unnick(ProxiedPlayer p) {
        CorePlayer cp = instance.getCorePlayer(p);
        CoreSystem.getInstance().getChannelHandler().createInfoRequest(p, "UNNICK");
        nicks.put(cp.getNickedSkin(), null);
        ((BungeeCorePlayer) cp).setNickedSkin(null);
        ((BungeeCorePlayer) cp).setNicked(false);
    }

    public void destroy(ProxiedPlayer p) {
        CorePlayer cp = instance.getCorePlayer(p);
        nicks.put(cp.getNickedSkin(), null);
        ((BungeeCorePlayer) cp).setNickedSkin(null);
        ((BungeeCorePlayer) cp).setNicked(false);
    }

}
