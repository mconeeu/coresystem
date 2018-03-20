/*
 * Copyright (c) 2017 - 2018 Dominik L., Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.coresystem.bungee.player;

import eu.mcone.coresystem.bungee.CoreSystem;
import eu.mcone.coresystem.bungee.utils.Messager;
import eu.mcone.coresystem.bungee.utils.PluginMessage;
import eu.mcone.coresystem.lib.mysql.MySQL;
import eu.mcone.coresystem.lib.player.Skin;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.sql.SQLException;
import java.util.HashMap;

public class NickManager {

    private final MySQL mysql;
    private HashMap<Skin, ProxiedPlayer> nicks;

    public NickManager(MySQL mysql) {
        this.mysql = mysql;
        reload();
    }

    public void reload() {
        nicks = new HashMap<>();

        mysql.select("SELECT n.name, t.texture_value, t.texture_signature FROM bungeesystem_nicks n, bukkitsystem_textures t WHERE n.texture = t.name", rs -> {
            try {
                while (rs.next()) {
                    nicks.put(new Skin(rs.getString("n.name"), rs.getString("t.texture_value"), rs.getString("t.texture_signature")), null);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    private Skin getNick() {
        for (HashMap.Entry<Skin, ProxiedPlayer> entry : nicks.entrySet()) {
            if (entry.getValue() == null && ProxyServer.getInstance().getPlayer(entry.getKey().getName()) == null) {
                return entry.getKey();
            }
        }
        return null;
    }

    public void nick(ProxiedPlayer p) {
        CorePlayer cp = CoreSystem.getCorePlayer(p);
        Skin nick = cp.getNick() != null ? cp.getNick() : getNick();
        nicks.put(nick, p);

        if (nick != null) {
            new PluginMessage("Return", p.getServer().getInfo(), "NICK", p.getUniqueId().toString(), nick.getName(), nick.getValue(), nick.getSignature());
            cp.setNick(nick);
            cp.setNicked(true);
        } else {
            Messager.send(p, "§4Es ist kein Nickname mehr verfügbar!");
        }
    }

    public void unnick(ProxiedPlayer p) {
        CorePlayer cp = CoreSystem.getCorePlayer(p);
        new PluginMessage("Return", p.getServer().getInfo(), "UNNICK", p.getUniqueId().toString());
        nicks.put(cp.getNick(), null);
        cp.setNick(null);
        cp.setNicked(false);
    }

    public void destroy(ProxiedPlayer p) {
        CorePlayer cp = CoreSystem.getCorePlayer(p);
        nicks.put(cp.getNick(), null);
        cp.setNick(null);
        cp.setNicked(false);
    }

}
