/*
 * Copyright (c) 2017 - 2019 Dominik Lippl, Rufus Maiwald, Felix Schmid and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.bungee.listener;

import eu.mcone.coresystem.api.bungee.CoreSystem;
import eu.mcone.coresystem.api.bungee.player.CorePlayer;
import eu.mcone.coresystem.bungee.BungeeCoreSystem;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ServerSwitchEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class ServerSwitch implements Listener {

    @EventHandler
    public void on(ServerSwitchEvent e) {
        ProxiedPlayer p = e.getPlayer();
        CorePlayer cp = BungeeCoreSystem.getInstance().getCorePlayer(p);

        if (cp.isNicked()) CoreSystem.getInstance().getChannelHandler().createInfoRequest(p, "NICK");
        e.getPlayer().setTabHeader(
                new ComponentBuilder(ChatColor.translateAlternateColorCodes('&', "§f§lMC ONE §3Minecraftnetzwerk §8» §7"+e.getPlayer().getServer().getInfo().getName())).create(),
                new ComponentBuilder(ChatColor.translateAlternateColorCodes('&', "§7§oPublic Beta 5.0")).create()
        );
    }

}
