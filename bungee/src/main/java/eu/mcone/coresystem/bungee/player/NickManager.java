/*
 * Copyright (c) 2017 - 2018 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.coresystem.bungee.player;

import eu.mcone.coresystem.api.bungee.CoreSystem;
import eu.mcone.coresystem.api.bungee.player.CorePlayer;
import eu.mcone.coresystem.api.core.player.SkinInfo;
import eu.mcone.coresystem.api.core.util.Random;
import eu.mcone.coresystem.bungee.BungeeCoreSystem;
import eu.mcone.coresystem.core.mysql.Database;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.sql.SQLException;
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

        instance.getMySQL(Database.SYSTEM).select("SELECT n.name, t.texture_value, t.texture_signature FROM bungeesystem_nicks n, bukkitsystem_textures t WHERE n.texture = t.name", rs -> {
            try {
                while (rs.next()) {
                    nicks.put(instance.getPlayerUtils().constructSkinInfo(rs.getString("n.name"), rs.getString("t.texture_value"), rs.getString("t.texture_signature")), null);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
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
