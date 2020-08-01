/*
 * Copyright (c) 2017 - 2020 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.bukkit.command;

import eu.mcone.coresystem.api.bukkit.command.CorePlayerCommand;
import eu.mcone.coresystem.api.core.overwatch.report.Report;
import eu.mcone.coresystem.bukkit.overwatch.Overwatch;
import eu.mcone.coresystem.bukkit.overwatch.report.ReportInfoInventory;
import eu.mcone.coresystem.bukkit.overwatch.report.ReportInventory;
import eu.mcone.coresystem.bukkit.overwatch.report.ReportsInventory;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class ReportCMD extends CorePlayerCommand {

    private final Overwatch overwatch;

    public ReportCMD(Overwatch overwatch) {
        super("report");
        this.overwatch = overwatch;
    }

    @Override
    public boolean onPlayerCommand(Player player, String[] args) {
        if (args.length >= 1) {
            if (!(args[0].equalsIgnoreCase("accept") || args[0].equalsIgnoreCase("close"))) {
                if (args[0].equalsIgnoreCase("help")) {
                    if (player.hasPermission("overwatch.report.general") || player.hasPermission("overwatch.report.*")) {
                        overwatch.getMessenger().send(player, "§4Bitte benutze: " +
                                "\n§c/report <Spieler> §4oder " +
                                "\n§c/report accept <id> §4oder " +
                                "\n§c/report close §4oder " +
                                "\n§c/report info §4oder " +
                                "\n§c/report list"
                        );
                    } else {
                        overwatch.getMessenger().send(player, "§4Bitte benutze: " +
                                "\n§c/report <Spieler>"
                        );
                    }

                    return true;
                } else if (args[0].equalsIgnoreCase("list")) {
                    if (player.hasPermission("overwatch.report.general") || player.hasPermission("overwatch.report.*")) {
                        new ReportsInventory(overwatch, player);
                    } else {
                        overwatch.getMessenger().send(player, "§4Du hast keine Berechtigung für diesen Befehl!");
                    }

                    return true;
                } else if (args[0].equalsIgnoreCase("confirm")) {
                    overwatch.getReportManager().confirmReport(player);
                    return true;
                } else if (args[0].equalsIgnoreCase("info")) {
                    if (player.hasPermission("overwatch.report.general") || player.hasPermission("overwatch.report.*")) {
                        Report report = overwatch.getReportManager().getCurrentlyEditing(player.getUniqueId());

                        if (report != null) {
                            new ReportInfoInventory(player, report);
                        } else {
                            overwatch.getMessenger().send(player, "§4Du bearbeitest momentan keinen §cReport!");
                        }
                    } else {
                        overwatch.getMessenger().send(player, "§4Du hast keine Berechtigung für diesen Befehl!");
                    }

                    return true;
                } else {
                    String reported = args[0];
                    if (!reported.isEmpty()) {
                        if (!player.hasPermission("overwatch.report.general") || player.hasPermission("overwatch.report.*")) {
                            Player reportedPlayer = Bukkit.getPlayer(reported);

                            if (reportedPlayer != null) {
                                if (player != reportedPlayer) {
                                    if (!(reportedPlayer.hasPermission("overwatch.report.ignore") || reportedPlayer.hasPermission("overwatch.report.*"))) {
                                        new ReportInventory(player, reportedPlayer);
                                    } else {
                                        overwatch.getMessenger().send(player, "§4Du kannst keine §cTeammitglieder §4Reporten!");
                                    }
                                } else {
                                    overwatch.getMessenger().send(player, "§4Du kannst dich nicht selbst reporten!");
                                }
                            } else {
                                overwatch.getMessenger().send(player, "§4Der Spieler §c" + reported + " §4konnte nicht gefunden werden!");
                            }
                        } else {
                            overwatch.getMessenger().send(player, "§4Du kannst niemanden Reporter da du ein Teammitglied bist!");
                        }

                        return true;
                    }
                }
            }
        }

        return true;
    }
}
