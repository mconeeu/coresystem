/*
 * Copyright (c) 2017 - 2020 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.bungee.listener;

import eu.mcone.coresystem.bungee.BungeeCoreSystem;
import net.md_5.bungee.api.AbstractReconnectHandler;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.event.ServerKickEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class ServerKickListener implements Listener {

    @EventHandler
    public void on(final ServerKickEvent e) {
        ServerInfo s;
        if (e.getPlayer().getServer() != null) {
            s = e.getPlayer().getServer().getInfo();
        }
        else if (BungeeCoreSystem.getInstance().getProxy().getReconnectHandler() != null) {
            s = BungeeCoreSystem.getInstance().getProxy().getReconnectHandler().getServer(e.getPlayer());
        }
        else {
            s = AbstractReconnectHandler.getForcedHost(e.getPlayer().getPendingConnection());
            if (s == null) {
                s = ProxyServer.getInstance().getServerInfo(e.getPlayer().getPendingConnection().getListener().getServerPriority().get(0));
            }
        }
        final ServerInfo kickTo = ProxyServer.getInstance().getServerInfo("Lobby");
        if (s != null && s.equals(kickTo)) {
            return;
        }

        String name = "";
        if (s!=null) name = s.getName()+" ";

        final String reason = BaseComponent.toLegacyText(e.getKickReasonComponent());
        final String moveMsg = "§8[§7§l!§8] §3Netzwerk §8» §4Du wurdest vom Server "+name+"gekickt!\n§7§o"+reason;

        e.setCancelled(true);
        e.setCancelServer(kickTo);

        e.getPlayer().sendMessage(TextComponent.fromLegacyText(moveMsg));
    }

}
