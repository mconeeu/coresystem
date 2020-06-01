/*
 * Copyright (c) 2017 - 2020 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.bungee.listener;

import eu.mcone.coresystem.api.bungee.CoreSystem;
import eu.mcone.coresystem.api.bungee.player.CorePlayer;
import eu.mcone.coresystem.bungee.BungeeCoreSystem;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ServerSwitchEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class ServerSwitchListener implements Listener {

    @EventHandler
    public void on(ServerSwitchEvent e) {
        ProxiedPlayer p = e.getPlayer();
        CorePlayer cp = BungeeCoreSystem.getInstance().getCorePlayer(p);

        if (p.getServer() != null) {
            CoreSystem.getInstance().getLabyModAPI().setCurrentServer(p, "MCONE-" + p.getServer().getInfo().getName());
        } else {
            CoreSystem.getInstance().getLabyModAPI().unsetCurrentServer(p);
        }

        if (cp.isNicked()) {
            BungeeCoreSystem.getSystem().getNickManager().serverSwitched(p);
        }

        PostLoginListener.updateTabHeader(p);
    }

}
