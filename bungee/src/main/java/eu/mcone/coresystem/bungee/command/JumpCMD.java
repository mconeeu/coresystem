/*
 * Copyright (c) 2017 - 2020 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.bungee.command;

import eu.mcone.coresystem.api.bungee.command.CorePlayerCommand;
import eu.mcone.coresystem.bungee.BungeeCoreSystem;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class JumpCMD extends CorePlayerCommand {

    public JumpCMD() {
        super("jump");
    }

    public void onPlayerCommand(ProxiedPlayer p, String[] args) {
        if (args.length == 1) {
            ProxiedPlayer t = ProxyServer.getInstance().getPlayer(args[0]);

            if (t != null) {
                if (p.hasPermission("group.team") || BungeeCoreSystem.getInstance().getCorePlayer(p).getFriendData().getFriends().containsKey(t.getUniqueId())) {
                    ServerInfo tserver = t.getServer().getInfo();

                    if (t.getServer().getInfo() != p.getServer().getInfo()) {
                        p.connect(tserver);

                        BungeeCoreSystem.getInstance().getMessenger().send(p, "§7Du bist zu §f" + t.getName() + "§7 gesprungen!");
                    } else {
                        BungeeCoreSystem.getInstance().getMessenger().send(p, "§4Du bist bereits auf diesem Server!");
                    }
                } else {
                    BungeeCoreSystem.getInstance().getMessenger().send(p, "§4Du kannst nur zu Spielern springen die deine Freunde sind!");
                }
            } else {
                BungeeCoreSystem.getInstance().getMessenger().sendTransl(p, "system.player.notonline");
            }
        } else {
            BungeeCoreSystem.getInstance().getMessenger().send(p, "§4Bitte Benutze: §c/jump <Spieler>");
        }
    }

}
