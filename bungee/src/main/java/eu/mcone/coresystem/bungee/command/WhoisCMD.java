/*
 * Copyright (c) 2017 - 2018 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.coresystem.bungee.command;

import eu.mcone.coresystem.api.core.exception.CoreException;
import eu.mcone.coresystem.api.core.player.Group;
import eu.mcone.coresystem.bungee.BungeeCoreSystem;
import eu.mcone.coresystem.bungee.ban.BanManager;
import eu.mcone.coresystem.bungee.player.OfflinePlayer;
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
                OfflinePlayer p = new OfflinePlayer(args[0]).loadPermissions().loadBanData();
                ProxiedPlayer pp = ProxyServer.getInstance().getPlayer(args[0]);
                StringBuilder message = new StringBuilder();

                StringBuilder groups = new StringBuilder();
                for (Group g : p.getGroups()) {
                    groups.append(g.getLabel()).append(" ");
                }

                String permInfo = "§2Hier ein paar Infos über §a" + p.getName() + "§2:" +
                        "\n§8» §7Status: §f" + p.getStatus() +
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

                String banInfo;
                if (p.isBanned()) {
                    banInfo = "\n§8» §7Gebannt: §f" + p.isBanned() +
                            "\n§8» §7Bannzeit: " + BanManager.getEndeString(p.getBanTime()) +
                            "\n§8» §7Bannpunkte: §f" + p.getBanPoints() +
                            "\n";
                } else {
                    banInfo = "\n§8» §7Gebannt: §f" + p.isBanned() +
                            "\n§8» §7Bannpunkte: §f" + p.getBanPoints() +
                            "\n";
                }

                String muteInfo;
                if (p.isBanned()) {
                    muteInfo = "\n§8» §7Gemuted: §f" + p.isMuted() +
                            "\n§8» §7Mutezeit: " + BanManager.getEndeString(p.getMuteTime()) +
                            "\n§8» §7Mutepunkte: §f" + p.getMutePoints() +
                            "\n";
                } else {
                    muteInfo = "\n§8» §7Gemuted: §f" + p.isMuted() +
                            "\n§8» §7Mutepunkte: §f" + p.getMutePoints();
                }

                BungeeCoreSystem.getInstance().getMessager().send(sender, message.append(permInfo).append(general).append(banInfo).append(muteInfo).toString());
            } catch (CoreException e) {
                BungeeCoreSystem.getInstance().getMessager().send(sender, "§4Der Spieler " + args[0] + " war noch nie auf MC ONE!");
            }
            return;
        }

        BungeeCoreSystem.getInstance().getMessager().send(sender, "§4Bitte benutze: §c/whois <Spieler>");
    }
}
