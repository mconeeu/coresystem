/*
 * Copyright (c) 2017 - 2018 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.coresystem.bungee.command;

import eu.mcone.coresystem.api.bungee.CoreSystem;
import eu.mcone.coresystem.api.bungee.player.OfflineCorePlayer;
import eu.mcone.coresystem.api.core.exception.PlayerNotResolvedException;
import eu.mcone.coresystem.api.core.player.CoinsUtil;
import eu.mcone.coresystem.bungee.BungeeCoreSystem;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class CoinsCMD extends Command{

    public CoinsCMD() {
        super("coins");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (sender instanceof ProxiedPlayer) {
            CoinsUtil coinsAPI = BungeeCoreSystem.getInstance().getCoinsUtil();

            ProxiedPlayer p = (ProxiedPlayer) sender;
            if (!BungeeCoreSystem.getInstance().getCooldownSystem().addAndCheck(BungeeCoreSystem.getInstance(), this.getClass(), p.getUniqueId())) return;

            if (args.length == 0) {
                int coins = coinsAPI.getCoins(p.getUniqueId());
                BungeeCoreSystem.getInstance().getMessager().send(p, "§7Du hast momentan §a" + coins + " Coins!");
            } else if (p.hasPermission("system.bungee.coins")) {
                if (args.length == 1) {
                    if (args[0].equalsIgnoreCase("add")) {
                        BungeeCoreSystem.getInstance().getMessager().send(p, "§4Bitte benutze: §c/coins add <Spieler> <Anzahl>");
                    } else if (args[0].equalsIgnoreCase("remove")) {
                        BungeeCoreSystem.getInstance().getMessager().send(p, "§4Bitte benutze: §c/coins remove <Spieler> <Anzahl>");
                    } else if (args[0].equalsIgnoreCase("set")) {
                        BungeeCoreSystem.getInstance().getMessager().send(p, "§4Bitte benutze: §c/coins set <Spieler> <Anzahl>");
                    } else {
                        String name = args[0];
                        BungeeCoreSystem.getInstance().getMessager().send(p, "§7Der Spieler §f" + name + "§7 hat momentan §a" + coinsAPI.getCoins(name) + " Coins!");
                    }
                } else if (args.length == 2) {
                    if (args[0].equalsIgnoreCase("add")) {
                        BungeeCoreSystem.getInstance().getMessager().send(p, "§4Bitte benutze: §c/coins add <Spieler> <Anzahl>");
                    } else if (args[0].equalsIgnoreCase("remove")) {
                        BungeeCoreSystem.getInstance().getMessager().send(p, "§4Bitte benutze: §c/coins remove <Spieler> <Anzahl>");
                    } else if (args[0].equalsIgnoreCase("set")) {
                        BungeeCoreSystem.getInstance().getMessager().send(p, "§4Bitte benutze: §c/coins set <Spieler> <Anzahl>");
                    } else {
                        BungeeCoreSystem.getInstance().getMessager().send(p, "§4Bitte benutze: §c/coins <add | remove | set> <Spieler> <Anzahl>");
                    }
                } else if (args.length == 3) {
                    if (args[0].equalsIgnoreCase("add")) {
                        String name = args[1];
                        try {
                            OfflineCorePlayer o = CoreSystem.getInstance().getOfflineCorePlayer(name);
                            int coins = Integer.valueOf(args[2]);

                            o.addCoins(coins);
                            BungeeCoreSystem.getInstance().getMessager().send(p, "§2Du hast §f" + name + "§2 erfolgreich §a" + coins + " Coins§2 hinzugefügt");
                        } catch (PlayerNotResolvedException e) {
                            BungeeCoreSystem.getInstance().getMessager().send(p, "§4Dieser Spieler war noch nie auf MC ONE");
                        }
                    } else if (args[0].equalsIgnoreCase("remove")) {
                        String name = args[1];
                        try {
                            OfflineCorePlayer o = CoreSystem.getInstance().getOfflineCorePlayer(name);
                            int coins = Integer.valueOf(args[2]);

                            o.removeCoins(coins);
                            BungeeCoreSystem.getInstance().getMessager().send(p, "§2Du hast §f" + name + "§2 erfolgreich §a" + coins + " Coins §2abgezogen");
                        } catch (PlayerNotResolvedException e) {
                            BungeeCoreSystem.getInstance().getMessager().send(p, "§4Dieser Spieler war noch nie auf MC ONE");
                        }
                    } else if (args[0].equalsIgnoreCase("set")) {
                        String name = args[1];
                        try {
                            OfflineCorePlayer o = CoreSystem.getInstance().getOfflineCorePlayer(name);
                            int coins = Integer.valueOf(args[2]);

                            o.setCoins(coins);
                            BungeeCoreSystem.getInstance().getMessager().send(p, "§f" + name + "§2 hat nun §a" + coins + " Coins§2!");
                        } catch (PlayerNotResolvedException e) {
                            BungeeCoreSystem.getInstance().getMessager().send(p, "§4Dieser Spieler war noch nie auf MC ONE");
                        }
                    } else {
                        BungeeCoreSystem.getInstance().getMessager().send(p, "§4Bitte benutze: §c/coins <add|remove|set> <Spieler> <Anzahl>");
                    }
                } else {
                    BungeeCoreSystem.getInstance().getMessager().send(p, "§4Bitte benutze: §c/coins <add|remove|set> <Spieler> <Anzahl>");
                }
            } else {
                BungeeCoreSystem.getInstance().getMessager().send(p, BungeeCoreSystem.getInstance().getTranslationManager().get("system.command.noperm"));
            }
        } else {
            BungeeCoreSystem.getInstance().getMessager().send(sender, BungeeCoreSystem.getInstance().getTranslationManager().get("system.command.consolesender"));
        }
    }
}
