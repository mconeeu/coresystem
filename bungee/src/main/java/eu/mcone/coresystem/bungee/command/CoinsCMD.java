/*
 * Copyright (c) 2017 - 2018 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.coresystem.bungee.command;

import eu.mcone.coresystem.api.core.player.CoinsAPI;
import eu.mcone.coresystem.bungee.BungeeCoreSystem;
import eu.mcone.coresystem.bungee.utils.Messager;
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
            CoinsAPI coinsAPI = BungeeCoreSystem.getInstance().getCoinsAPI();

            ProxiedPlayer p = (ProxiedPlayer) sender;
            if (!BungeeCoreSystem.getInstance().getCooldownSystem().addAndCheck(BungeeCoreSystem.getInstance(), this.getClass(), p.getUniqueId())) return;

            if (args.length == 0) {
                int coins = coinsAPI.getCoins(p.getUniqueId());
                Messager.send(p, "§7Du hast momentan §a" + coins + " Coins!");
            } else if (p.hasPermission("system.bungee.coins")) {
                if (args.length == 1) {
                    if (args[0].equalsIgnoreCase("add")) {
                        Messager.send(p, "§4Bitte benutze: §c/coins add <Spieler> <Anzahl>");
                    } else if (args[0].equalsIgnoreCase("remove")) {
                        Messager.send(p, "§4Bitte benutze: §c/coins remove <Spieler> <Anzahl>");
                    } else if (args[0].equalsIgnoreCase("set")) {
                        Messager.send(p, "§4Bitte benutze: §c/coins set <Spieler> <Anzahl>");
                    } else {
                        String name = args[0];
                        if (coinsAPI.isRegistered(name)) {
                            int coins = coinsAPI.getCoins(name);
                            Messager.send(p, "§7Der Spieler §f" + name + "§7 hat momentan §a" + coins + " Coins!");
                        } else {
                            Messager.send(p, "§4Dieser Spieler war noch nie auf MC ONE");
                        }
                    }
                } else if (args.length == 2) {
                    if (args[0].equalsIgnoreCase("add")) {
                        Messager.send(p, "§4Bitte benutze: §c/coins add <Spieler> <Anzahl>");
                    } else if (args[0].equalsIgnoreCase("remove")) {
                        Messager.send(p, "§4Bitte benutze: §c/coins remove <Spieler> <Anzahl>");
                    } else if (args[0].equalsIgnoreCase("set")) {
                        Messager.send(p, "§4Bitte benutze: §c/coins set <Spieler> <Anzahl>");
                    } else {
                        Messager.send(p, "§4Bitte benutze: §c/coins <add | remove | set> <Spieler> <Anzahl>");
                    }
                } else if (args.length == 3) {
                    if (args[0].equalsIgnoreCase("add")) {
                        String name = args[1];
                        if (coinsAPI.isRegistered(name)) {
                            int coins = Integer.valueOf(args[2]);
                            coinsAPI.addCoins(name, coins);
                            Messager.send(p, "§2Du hast §f" + name + "§2 erfolgreich §a" + coins + " Coins§2 hinzugefügt");
                        } else {
                            Messager.send(p, "§4Dieser Spieler war noch nie auf MC ONE");
                        }
                    } else if (args[0].equalsIgnoreCase("remove")) {
                        String name = args[1];
                        if (coinsAPI.isRegistered(name)) {
                            int coins = Integer.valueOf(args[2]);
                            int coins2 = coinsAPI.getCoins(p.getUniqueId());
                            if (coins2 <= 0) {
                                Messager.send(p, " §4Du kannst dem Spieler §f" + name + " §4keine Coins mehr wegnehmen");
                            } else {
                                coinsAPI.removeCoins(name, coins);
                                Messager.send(p, "§2Du hast §f" + name + "§2 erfolgreich §a" + coins + " Coins §2abgezogen");
                            }
                        } else {
                            Messager.send(p, "§4Dieser Spieler war noch nie auf MC ONE");
                        }
                    } else if (args[0].equalsIgnoreCase("set")) {
                        String name = args[1];
                        if (coinsAPI.isRegistered(name)) {
                            int coins = Integer.valueOf(args[2]);
                            coinsAPI.setCoins(name, coins);
                            Messager.send(p, "§f" + name + "§2 hat nun §a" + coins + " Coins§2!");
                        } else {
                            Messager.send(p, "§4Dieser Spieler war noch nie auf MC ONE");
                        }
                    } else {
                        Messager.send(p, "§4Bitte benutze: §c/coins <add|remove|set> <Spieler> <Anzahl>");
                    }
                } else {
                    Messager.send(p, "§4Bitte benutze: §c/coins <add|remove|set> <Spieler> <Anzahl>");
                }
            } else {
                Messager.send(p, " §4Du hast keine Berechtigung für diesen Befehl!");
            }
        } else {
            Messager.send(sender, "§4Dieser Befehl kann nur von einem Spieler ausgeführt werden!");
        }
    }
}
