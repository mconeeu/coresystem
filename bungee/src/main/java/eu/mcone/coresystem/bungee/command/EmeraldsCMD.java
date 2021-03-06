/*
 * Copyright (c) 2017 - 2020 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.bungee.command;

import com.google.common.collect.ImmutableSet;
import eu.mcone.coresystem.api.bungee.CoreSystem;
import eu.mcone.coresystem.api.bungee.command.CoreCommand;
import eu.mcone.coresystem.api.bungee.facades.Msg;
import eu.mcone.coresystem.api.bungee.player.CorePlayer;
import eu.mcone.coresystem.api.bungee.player.OfflineCorePlayer;
import eu.mcone.coresystem.api.core.exception.PlayerNotResolvedException;
import eu.mcone.coresystem.bungee.BungeeCoreSystem;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.TabExecutor;

import java.util.HashSet;
import java.util.Set;

public class EmeraldsCMD extends CoreCommand implements TabExecutor {

    public EmeraldsCMD() {
        super("emeralds", null, "emerald");
    }

    @Override
    public void onCommand(CommandSender sender, String[] args) {
        if (args.length == 0) {
            if (sender instanceof ProxiedPlayer) {
                Msg.send(sender, "§7Du hast momentan §a" + CoreSystem.getInstance().getCorePlayer(((ProxiedPlayer) sender)).getEmeralds() + " Emeralds!");
            } else {
                Msg.sendTransl(sender, "system.command.consolesender");
            }
        } else if (sender.hasPermission("system.bungee.emeralds")) {
            if (args.length == 1) {
                try {
                    OfflineCorePlayer t = CoreSystem.getInstance().getOfflineCorePlayer(args[0]);
                    Msg.send(sender, "§7Der Spieler §f" + t.getName() + "§7 hat momentan §a" + t.getEmeralds() + " Emeralds§7!");
                } catch (PlayerNotResolvedException e) {
                    Msg.send(sender, "§4Der Minecraftaccount mit dem Namen §c" + args[0] + "§4konnte nicht gefunden werden: \n§f§o" + e.getMessage());
                }

                return;
            } else if (args.length == 3 && (args[0].equalsIgnoreCase("add") || args[0].equalsIgnoreCase("remove") || args[0].equalsIgnoreCase("set"))) {
                String name = args[1];
                try {
                    OfflineCorePlayer o = CoreSystem.getInstance().getOfflineCorePlayer(name);
                    int coins = Integer.parseInt(args[2]);

                    if (args[0].equalsIgnoreCase("add")) {
                        o.addEmeralds(coins);
                        Msg.send(sender, "§2Du hast §f" + name + "§2 erfolgreich §a" + coins + " Emeralds§2 hinzugefügt");
                    } else if (args[0].equalsIgnoreCase("remove")) {
                        o.removeEmeralds(coins);
                        Msg.send(sender, "§2Du hast §f" + name + "§2 erfolgreich §a" + coins + " Emeralds§2 abgezogen");
                    } else if (args[0].equalsIgnoreCase("set")) {
                        o.setEmeralds(coins);
                        Msg.send(sender, "§f" + name + "§2 hat nun §a" + coins + " Emeralds§2!");
                    }
                } catch (PlayerNotResolvedException e) {
                    Msg.send(sender, "§4Der Minecraftaccount mit dem Namen §c" + args[0] + "§4konnte nicht gefunden werden: \n§f§o" + e.getMessage());
                }

                return;
            }

            Msg.send(sender, "§4Bitte benutze: §c/emeralds <add|remove|set> <Spieler> <Anzahl>");
        } else {
            Msg.sendTransl(sender, "system.command.noperm");
        }
    }

    @Override
    public Iterable<String> onTabComplete(CommandSender sender, String[] args) {
        if (sender.hasPermission("system.bungee.emeralds")) {
            String search = args[0];
            Set<String> matches = new HashSet<>();

            if (args.length == 1) {
                for (CorePlayer player : BungeeCoreSystem.getSystem().getOnlineCorePlayers()) {
                    if (player.getName().startsWith(search)) {
                        matches.add(player.getName());
                    }
                }

                for (String arg : CoinsCMD.ARGS) {
                    if (arg.startsWith(search)) {
                        matches.add(arg);
                    }
                }

                return matches;
            } else if (args.length == 2) {
                for (CorePlayer player : BungeeCoreSystem.getSystem().getOnlineCorePlayers()) {
                    if (player.getName().startsWith(search)) {
                        matches.add(player.getName());
                    }
                }

                return matches;
            }
        }

        return ImmutableSet.of();
    }

}
