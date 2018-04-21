/*
 * Copyright (c) 2017 - 2018 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.coresystem.bungee.player;

import eu.mcone.coresystem.api.core.mysql.MySQL;
import eu.mcone.coresystem.api.core.player.SkinInfo;
import eu.mcone.coresystem.bungee.BungeeCoreSystem;
import eu.mcone.coresystem.bungee.utils.Messager;
import eu.mcone.coresystem.bungee.utils.PluginMessage;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.sql.SQLException;
import java.util.HashMap;

public class NickManager implements eu.mcone.coresystem.api.bungee.player.NickManager {

    private final BungeeCoreSystem instance;
    private final MySQL mysql;
    private HashMap<SkinInfo, ProxiedPlayer> nicks;

    public NickManager(BungeeCoreSystem instance) {
        this.instance = instance;
        this.mysql = instance.getMySQL(1);
        reload();
    }

    public void reload() {
        nicks = new HashMap<>();

        mysql.select("SELECT n.name, t.texture_value, t.texture_signature FROM bungeesystem_nicks n, bukkitsystem_textures t WHERE n.texture = t.name", rs -> {
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
        for (HashMap.Entry<SkinInfo, ProxiedPlayer> entry : nicks.entrySet()) {
            if (entry.getValue() == null && ProxyServer.getInstance().getPlayer(entry.getKey().getName()) == null) {
                return entry.getKey();
            }
        }
        return null;
    }

    public void nick(ProxiedPlayer p) {
        eu.mcone.coresystem.api.bungee.player.BungeeCorePlayer cp = instance.getCorePlayer(p);
        SkinInfo nick = cp.getNickedSkin() != null ? cp.getNickedSkin() : getNick();
        nicks.put(nick, p);

        if (nick != null) {
            new PluginMessage("Return", p.getServer().getInfo(), "NICK", p.getUniqueId().toString(), nick.getName(), nick.getValue(), nick.getSignature());
            cp.setNickedSkin(nick);
            cp.setNicked(true);
        } else {
            Messager.send(p, "§4Es ist kein Nickname mehr verfügbar!");
        }
    }

    public void unnick(ProxiedPlayer p) {
        eu.mcone.coresystem.api.bungee.player.BungeeCorePlayer cp = instance.getCorePlayer(p);
        new PluginMessage("Return", p.getServer().getInfo(), "UNNICK", p.getUniqueId().toString());
        nicks.put(cp.getNickedSkin(), null);
        cp.setNickedSkin(null);
        cp.setNicked(false);
    }

    public void destroy(ProxiedPlayer p) {
        eu.mcone.coresystem.api.bungee.player.BungeeCorePlayer cp = instance.getCorePlayer(p);
        nicks.put(cp.getNickedSkin(), null);
        cp.setNickedSkin(null);
        cp.setNicked(false);
    }

}
