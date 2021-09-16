/*
 * Copyright (c) 2017 - 2020 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.bungee.command;

import com.google.common.collect.ImmutableSet;
import eu.mcone.coresystem.api.bungee.command.CorePlayerCommand;
import eu.mcone.coresystem.api.bungee.facades.Msg;
import eu.mcone.coresystem.api.bungee.player.CorePlayer;
import eu.mcone.coresystem.bungee.BungeeCoreSystem;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.TabExecutor;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class JumpCMD extends CorePlayerCommand implements TabExecutor {

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

                        Msg.send(p, "§7Du bist zu §f" + t.getName() + "§7 gesprungen!");
                    } else {
                        Msg.send(p, "§4Du bist bereits auf diesem Server!");
                    }
                } else {
                    Msg.send(p, "§4Du kannst nur zu Spielern springen die deine Freunde sind!");
                }
            } else {
                Msg.sendTransl(p, "system.player.notonline");
            }
        } else {
            Msg.send(p, "§4Bitte Benutze: §c/jump <Spieler>");
        }
    }

    @Override
    public Iterable<String> onTabComplete(CommandSender sender, String[] args) {
        if (sender instanceof ProxiedPlayer) {
            CorePlayer cp = BungeeCoreSystem.getSystem().getCorePlayer((ProxiedPlayer) sender);

            if (args.length == 1) {
                String search = args[0];
                Set<UUID> friends = cp.getFriendData().getFriends().keySet();
                Set<String> matches = new HashSet<>();

                for (CorePlayer friend : BungeeCoreSystem.getSystem().getOnlineCorePlayers()) {
                    if (friend.getName().startsWith(search) && friends.contains(friend.getUuid())) {
                        matches.add(friend.getName());
                    }
                }

                return matches;
            }
        }

        return ImmutableSet.of();
    }

}
