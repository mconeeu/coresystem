/*
 * Copyright (c) 2017 - 2020 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.bungee.command;

import eu.mcone.coresystem.api.bungee.CoreSystem;
import eu.mcone.coresystem.api.bungee.player.OfflineCorePlayer;
import eu.mcone.coresystem.api.core.exception.PlayerNotResolvedException;
import eu.mcone.coresystem.bungee.BungeeCoreSystem;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class EmeraldsCMD extends Command {

    public EmeraldsCMD() {
        super("emeralds", null, "emerald");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length == 0) {
            if (sender instanceof ProxiedPlayer) {
                BungeeCoreSystem.getInstance().getMessenger().sendSenderSimple(sender, "§7Du hast momentan §a" + CoreSystem.getInstance().getCorePlayer(((ProxiedPlayer) sender)).getEmeralds() + " Emeralds!");
            } else {
                BungeeCoreSystem.getInstance().getMessenger().sendSenderSimple(sender, BungeeCoreSystem.getInstance().getTranslationManager().get("system.command.consolesender"));
            }
        } else if (sender.hasPermission("system.bungee.emeralds")) {
            if (args.length == 1) {
                try {
                    OfflineCorePlayer t = CoreSystem.getInstance().getOfflineCorePlayer(args[0]);
                    BungeeCoreSystem.getInstance().getMessenger().sendSenderSimple(sender, "§7Der Spieler §f" + t.getName() + "§7 hat momentan §a" + t.getEmeralds() + " Emeralds§7!");
                } catch (PlayerNotResolvedException e) {
                    BungeeCoreSystem.getInstance().getMessenger().sendSenderSimple(sender, "§4Der Minecraftaccount mit dem Namen §c" + args[0] + "§4konnte nicht gefunden werden: \n§f§o" + e.getMessage());
                }

                return;
            } else if (args.length == 3 && (args[0].equalsIgnoreCase("add") || args[0].equalsIgnoreCase("remove") || args[0].equalsIgnoreCase("set"))) {
                String name = args[1];
                try {
                    OfflineCorePlayer o = CoreSystem.getInstance().getOfflineCorePlayer(name);
                    int coins = Integer.valueOf(args[2]);

                    if (args[0].equalsIgnoreCase("add")) {
                        o.addEmeralds(coins);
                        BungeeCoreSystem.getInstance().getMessenger().sendSenderSimple(sender, "§2Du hast §f" + name + "§2 erfolgreich §a" + coins + " Emeralds§2 hinzugefügt");
                    } else if (args[0].equalsIgnoreCase("remove")) {
                        o.removeEmeralds(coins);
                        BungeeCoreSystem.getInstance().getMessenger().sendSenderSimple(sender, "§2Du hast §f" + name + "§2 erfolgreich §a" + coins + " Emeralds§2 abgezogen");
                    } else if (args[0].equalsIgnoreCase("set")) {
                        o.setEmeralds(coins);
                        BungeeCoreSystem.getInstance().getMessenger().sendSenderSimple(sender, "§f" + name + "§2 hat nun §a" + coins + " Emeralds§2!");
                    }
                } catch (PlayerNotResolvedException e) {
                    BungeeCoreSystem.getInstance().getMessenger().sendSenderSimple(sender, "§4Der Minecraftaccount mit dem Namen §c" + args[0] + "§4konnte nicht gefunden werden: \n§f§o" + e.getMessage());
                }

                return;
            }

            BungeeCoreSystem.getInstance().getMessenger().sendSenderSimple(sender, "§4Bitte benutze: §c/emeralds <add|remove|set> <Spieler> <Anzahl>");
        } else {
            BungeeCoreSystem.getInstance().getMessenger().sendSenderSimple(sender, BungeeCoreSystem.getInstance().getTranslationManager().get("system.command.noperm"));
        }
    }

}
