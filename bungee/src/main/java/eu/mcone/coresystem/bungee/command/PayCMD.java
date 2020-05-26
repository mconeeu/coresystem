/*
 * Copyright (c) 2017 - 2020 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.bungee.command;

import eu.mcone.coresystem.api.bungee.CoreSystem;
import eu.mcone.coresystem.api.bungee.player.CorePlayer;
import eu.mcone.coresystem.bungee.BungeeCoreSystem;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class PayCMD extends Command {

    public PayCMD() {
        super("pay");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (sender instanceof ProxiedPlayer) {
            ProxiedPlayer bp = (ProxiedPlayer) sender;

            if (args.length == 2) {
                CorePlayer p = CoreSystem.getInstance().getCorePlayer(bp);
                int amount = Integer.parseInt(args[1]);

                if (args[0].equals("*")) {
                    ServerInfo s = bp.getServer().getInfo();

                    if (p.getCoins() - (amount * (s.getPlayers().size()) - 1) >= 0) {
                        for (ProxiedPlayer t : s.getPlayers()) {
                            if (t != bp) {
                                CoreSystem.getInstance().getCorePlayer(t).addCoins(amount);
                                CoreSystem.getInstance().getMessenger().send(t, "§2Du hast §a" + amount + " Coins§2 von §f" + p.getName() + "§2 bekommen!");
                                p.removeCoins(amount);
                            }
                        }

                        CoreSystem.getInstance().getMessenger().send(bp, "§2Du hast §fallen Spielern§2 erfolgreich §a" + amount + " Coins§2 gegeben!");
                    } else {
                        CoreSystem.getInstance().getMessenger().send(bp, "§4Du hast nicht genügend Coins!");
                    }
                } else {
                    CorePlayer t = CoreSystem.getInstance().getCorePlayer(args[0]);

                    if (t != null) {
                        if (p != t) {
                            if ((p.getCoins() - amount) >= 0) {
                                p.removeCoins(amount);
                                t.addCoins(amount);

                                CoreSystem.getInstance().getMessenger().send(bp, "§2Du hast §f" + t.getName() + "§2 erfolgreich §a" + amount + " Coins§2 gegeben!");
                                CoreSystem.getInstance().getMessenger().send(t.bungee(), "§2Du hast §a" + amount + " Coins §2von §f" + p.getName() + "§2 bekommen!");
                            } else {
                                CoreSystem.getInstance().getMessenger().send(bp, "§4Du hast nicht genügend Coins!");
                            }
                        } else {
                            CoreSystem.getInstance().getMessenger().send(bp, "§4Du kannst dir nicht selbs Coins zahlen, Dummkopf.");
                        }
                    } else {
                        CoreSystem.getInstance().getMessenger().send(bp, "§4Der Spieler ist nicht online!");
                    }
                }
            } else {
                CoreSystem.getInstance().getMessenger().send(bp, "§4Bitte benutze: §c/pay <Spieler> <Anzahl>");
            }
        } else {
            BungeeCoreSystem.getInstance().getMessenger().sendSimple(sender, BungeeCoreSystem.getInstance().getTranslationManager().get("system.command.consolesender"));
        }
    }
}
