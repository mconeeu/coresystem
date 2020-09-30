/*
 * Copyright (c) 2017 - 2020 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.bungee.command;

import eu.mcone.coresystem.api.bungee.player.OfflineCorePlayer;
import eu.mcone.coresystem.api.core.exception.PlayerNotResolvedException;
import eu.mcone.coresystem.api.core.overwatch.report.Report;
import eu.mcone.coresystem.bungee.BungeeCoreSystem;
import eu.mcone.coresystem.bungee.overwatch.Overwatch;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class ReportCMD extends Command {

    private final Overwatch overwatch;

    public ReportCMD(Overwatch overwatch) {
        super("report", null);
        this.overwatch = overwatch;
    }

    public void execute(CommandSender sender, String[] args) {
        if (sender instanceof ProxiedPlayer) {
            final ProxiedPlayer p = (ProxiedPlayer) sender;
            if (!BungeeCoreSystem.getInstance().getCooldownSystem().addAndCheck(this.getClass(), p.getUniqueId()))
                return;

            if (args.length == 1) {
                if (args[0].equalsIgnoreCase("close")) {
                    if (p.hasPermission("system.bungee.overwatch.report.general") || p.hasPermission("system.bungee.overwatch.report.*")) {
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
                    if (p.hasPermission("system.bungee.overwatch.report.general") || p.hasPermission("system.bungee.overwatch.report.*")) {
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
        } else {
            BungeeCoreSystem.getInstance().getMessenger().sendSenderSimple(sender, BungeeCoreSystem.getInstance().getTranslationManager().get("system.command.consolesender"));
        }
    }
}
