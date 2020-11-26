/*
 * Copyright (c) 2017 - 2020 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.bungee.command;

import com.google.common.collect.ImmutableSet;
import eu.mcone.coresystem.api.bungee.CoreSystem;
import eu.mcone.coresystem.api.bungee.command.CorePlayerCommand;
import eu.mcone.coresystem.api.bungee.player.CorePlayer;
import eu.mcone.coresystem.api.core.player.Nick;
import eu.mcone.coresystem.bungee.BungeeCoreSystem;
import eu.mcone.coresystem.bungee.player.CoreNickManager;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.TabExecutor;

import java.util.HashSet;
import java.util.Set;

public class NickCMD extends CorePlayerCommand implements TabExecutor {

    private final CoreNickManager manager;

    public NickCMD(CoreNickManager manager) {
        super("nick", "system.bungee.nick");
        this.manager = manager;
    }

    @Override
    public void onPlayerCommand(ProxiedPlayer p, String[] args) {
        CorePlayer cp = BungeeCoreSystem.getInstance().getCorePlayer(p);

        if (args.length == 0) {
            if (!cp.isNicked()) {
                BungeeCoreSystem.getInstance().getNickManager().nick(p);
            } else {
                BungeeCoreSystem.getInstance().getNickManager().unnick(p);
            }

            return;
        } else if (args.length > 1) {
            if (p.hasPermission("system.bungee.nick.check")) {
                if (args.length == 2 && args[0].equalsIgnoreCase("check")) {
                    Nick nick = manager.getNick(args[1]);

                    if (nick != null) {
                        ProxiedPlayer user = manager.getNickedUser(nick);
                        BungeeCoreSystem.getInstance().getMessenger().sendSuccess(p, "Ein Nick mit dem Namen ![" + nick.getName() + "] existiert und wird von §f" + (user != null ? user.getName() : "niemandem") + "§2 benutzt.");
                    } else {
                        BungeeCoreSystem.getInstance().getMessenger().sendError(p, "Ein Nick mit dem Namen existiert nicht!");
                    }

                    return;
                }
            } else {
                BungeeCoreSystem.getInstance().getMessenger().sendTransl(p, "system.command.noperm");
                return;
            }
        }

        BungeeCoreSystem.getInstance().getMessenger().send(p, "§4Bitte benutze: §c/nick");
    }

    @Override
    public Iterable<String> onTabComplete(CommandSender sender, String[] args) {
        if (sender.hasPermission("system.bungee.nick.check")) {
            String search = args[0];

            if (args.length == 1) {
                if ("check".startsWith(search)) {
                    return ImmutableSet.of("check");
                }
            } else if (args.length == 2) {
                Set<String> matches = new HashSet<>();

                for (Nick nick : CoreSystem.getInstance().getNickManager().getPlayerNicks().keySet()) {
                    if (nick.getName().startsWith(search)) {
                        matches.add(nick.getName());
                    }
                }

                return matches;
            }
        }

        return ImmutableSet.of();
    }

}
