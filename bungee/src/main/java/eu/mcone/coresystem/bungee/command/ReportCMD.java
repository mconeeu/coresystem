/*
 * Copyright (c) 2017 - 2020 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.bungee.command;

import com.google.common.collect.ImmutableSet;
import eu.mcone.coresystem.api.bungee.command.CorePlayerCommand;
import eu.mcone.coresystem.api.bungee.player.OfflineCorePlayer;
import eu.mcone.coresystem.api.core.exception.PlayerNotResolvedException;
import eu.mcone.coresystem.api.core.overwatch.report.Report;
import eu.mcone.coresystem.bungee.BungeeCoreSystem;
import eu.mcone.coresystem.bungee.overwatch.Overwatch;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.TabExecutor;

import java.util.HashSet;
import java.util.Set;

public class ReportCMD extends CorePlayerCommand implements TabExecutor {

    private final Overwatch overwatch;

    public ReportCMD(Overwatch overwatch) {
        super("report", null);
        this.overwatch = overwatch;
    }

    public void onPlayerCommand(ProxiedPlayer p, String[] args) {
        if (args.length == 1) {
            if (args[0].equalsIgnoreCase("close")) {
                if (p.hasPermission("system.bungee.overwatch.report")) {
                    if (overwatch.getReportManager().currentlyWorkingOnReport(p.getUniqueId())) {
                        overwatch.getReportManager().closeReport(p);
                        overwatch.getMessenger().send(p, "§7Du hast den Report erfolgreich §aabgeschlosse§7!");
                    } else {
                        overwatch.getMessenger().send(p, "§4Du bearbeitest momentan keinen §cReport!");
                    }
                } else {
                    overwatch.getMessenger().send(p, "§4Du hast keine Berechtigung für diesen Befehl.");
                }
            } else {
                BungeeCoreSystem.getInstance().getChannelHandler().createInfoRequest(p, "CMD", "report " + args[0]);
            }
        } else if (args.length == 2) {
            if (args[0].equalsIgnoreCase("accept")) {
                if (p.hasPermission("system.bungee.overwatch.report")) {
                    String id = args[1];

                    if (!id.isEmpty()) {
                        if (!overwatch.getReportManager().currentlyWorkingOnReport(p.getUniqueId())) {
                            if (overwatch.getReportManager().isReportAlreadyTaken(id)) {
                                try {
                                    Report report = overwatch.getReportManager().getReport(id);
                                    OfflineCorePlayer corePlayer = BungeeCoreSystem.getSystem().getOfflineCorePlayer(report.getMember());
                                    overwatch.getMessenger().send(p, "§4Das §aTeammitglied §7" + corePlayer.getMainGroup().getPrefix() + corePlayer.getName() + " §4kümmert sich bereits um den Report!");
                                } catch (PlayerNotResolvedException e) {
                                    e.printStackTrace();
                                }
                            } else {
                                overwatch.getReportManager().acceptReport(id, p);
                            }
                        } else {
                            overwatch.getMessenger().send(p, "§cDu bearbeitest bereits einen §cReport");
                        }
                    } else {
                        overwatch.getMessenger().send(p, "§4Bitte gib eine ReportID an!");
                    }
                } else {
                    overwatch.getMessenger().send(p, "§4Du hast keine Berechtigung für diesen Befehl.");
                }
            }
        } else {
            BungeeCoreSystem.getInstance().getChannelHandler().createInfoRequest(p, "CMD", "report help");
        }
    }

    @Override
    public Iterable<String> onTabComplete(CommandSender sender, String[] args) {
        if (sender.hasPermission("system.bungee.overwatch.report")) {
            if (args.length == 1) {
                String search = args[0];
                Set<String> matches = new HashSet<>();

                for (String arg : new String[]{"close", "accept"}) {
                    if (arg.startsWith(search)) {
                        matches.add(search);
                    }
                }

                return matches;
            } else if (args.length == 2) {
                String search = args[1];
                Set<String> matches = new HashSet<>();

                for (ProxiedPlayer player : ProxyServer.getInstance().getPlayers()) {
                    if (player != sender && !player.hasPermission("group.team") && player.getName().startsWith(search)) {
                        matches.add(player.getName());
                    }
                }

                return matches;
            }
        }

        return ImmutableSet.of();
    }

}
