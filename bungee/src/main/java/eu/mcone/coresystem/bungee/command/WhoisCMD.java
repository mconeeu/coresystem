/*
 * Copyright (c) 2017 - 2020 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.bungee.command;

import eu.mcone.coresystem.api.bungee.overwatch.punish.Punish;
import eu.mcone.coresystem.api.bungee.player.OfflineCorePlayer;
import eu.mcone.coresystem.api.core.exception.CoreException;
import eu.mcone.coresystem.api.core.player.Group;
import eu.mcone.coresystem.bungee.BungeeCoreSystem;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class WhoisCMD extends Command {

    public WhoisCMD() {
        super("whois", "system.bungee.whois", "whereis");
    }

    public void execute(CommandSender sender, String[] args) {
        if (args.length == 1) {
            try {
                OfflineCorePlayer p = BungeeCoreSystem.getInstance().getOfflineCorePlayer(args[0]);
                ProxiedPlayer pp = ProxyServer.getInstance().getPlayer(args[0]);
                StringBuilder message = new StringBuilder();

                StringBuilder groups = new StringBuilder();
                for (Group g : p.getGroups()) {
                    groups.append(g.getLabel()).append(" ");
                }

                String permInfo = "§2Hier ein paar Infos über §a" + p.getName() + "§2:" +
                        "\n§8» §7Status: §f" + p.getState().getName() +
                        "\n" +
                        "\n§8» §7UUID: §f" + p.getUuid() +
                        "\n§8» §7Gruppen: " + groups.toString() +
                        "\n";

                String general = "";
                if (pp != null) {
                    general = "§8» §7Server: §f" + pp.getServer().getInfo().getName() +
                            "\n§8» §7IP-Adresse: §f" + pp.getPendingConnection().getAddress() +
                            "\n";
                }

                String banInfo = "";
                String muteInfo = "";
                Punish punish = BungeeCoreSystem.getSystem().getOverwatch().getPunishManager().getPunish(p.getUuid());
                if (punish != null) {
                    if (punish.isBanned()) {
                        banInfo = "\n§8» §7Gebannt: §f" + p.isBanned() +
                                "\n§8» §7Bannzeit: " + BungeeCoreSystem.getSystem().getOverwatch().getPunishManager().getEndeString(p.getBanTime()) +
                                "\n§8» §7Bannpunkte: §f" + p.getBanPoints() +
                                "\n";
                    } else {
                        banInfo = "\n§8» §7Gebannt: §f" + p.isBanned() +
                                "\n§8» §7Bannpunkte: §f" + p.getBanPoints() +
                                "\n";
                    }

                    if (punish.isMuted()) {
                        muteInfo = "\n§8» §7Gemuted: §f" + p.isMuted() +
                                "\n§8» §7Mutezeit: " + BungeeCoreSystem.getSystem().getOverwatch().getPunishManager().getEndeString(p.getMuteTime()) +
                                "\n§8» §7Mutepunkte: §f" + p.getMutePoints() +
                                "\n";
                    } else {
                        muteInfo = "\n§8» §7Gemuted: §f" + p.isMuted() +
                                "\n§8» §7Mutepunkte: §f" + p.getMutePoints();
                    }
                }

                BungeeCoreSystem.getInstance().getMessenger().sendSenderSimple(sender, message.append(permInfo).append(general).append(banInfo).append(muteInfo).toString());
            } catch (CoreException e) {
                BungeeCoreSystem.getInstance().getMessenger().sendSenderSimple(sender, "§4Der Spielername §c" + args[0] + "§4 existiert nicht!");
            }
            return;
        }

        BungeeCoreSystem.getInstance().getMessenger().sendSenderSimple(sender, "§4Bitte benutze: §c/whois <Spieler>");
    }
}
