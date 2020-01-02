/*
 * Copyright (c) 2017 - 2019 Dominik Lippl, Rufus Maiwald, Felix Schmid and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.bungee.command;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import eu.mcone.coresystem.bungee.report.ReportReason;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class ReportCMD extends Command {

    private HashMap<ProxiedPlayer, ProxiedPlayer> reports = new HashMap<>();
    private HashMap<String, Long> zeit = new HashMap<>();

    public ReportCMD() {
        super("report", null);
    }

    public void execute(CommandSender sender, String[] args) {
        ProxiedPlayer player = (ProxiedPlayer) sender;
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF(player.getUniqueId().toString());

        player.getServer().sendData("MC_ONE_REPLAY", out.toByteArray());

        /*if (sender instanceof ProxiedPlayer) {
            final ProxiedPlayer p = (ProxiedPlayer) sender;
            if (!BungeeCoreSystem.getInstance().getCooldownSystem().addAndCheck(BungeeCoreSystem.getInstance(), this.getClass(), p.getUniqueId()))
                return;
            final long millis = System.currentTimeMillis() / 1000;

            if (args.length == 1) {
                if ((args[0].equalsIgnoreCase("list") && (p.hasPermission("system.bungee.report") || (p.hasPermission("system.bungee.*"))))) {
                    BungeeCoreSystem.getSystem().getMySQL(MySQLDatabase.SYSTEM).select("SELECT `id`, `title` FROM `website_ticket` WHERE `cat`='Spielerreport' AND `state`='pending';", rs_reportlist -> {
                        try {
                            int desc = 0;
                            while (rs_reportlist.next()) {
                                desc++;
                            }

                            if (desc > 0) {
                                BungeeCoreSystem.getInstance().getMessager().send(p, "§7Folgende Reports sind noch unbearbeitet:");
                                rs_reportlist.beforeFirst();
                                while (rs_reportlist.next()) {
                                    BungeeCoreSystem.getInstance().getMessager().sendSimple(p, "§7» " + rs_reportlist.getInt("id") + ". §f" + rs_reportlist.getString("title"));
                                }
                                BungeeCoreSystem.getInstance().getMessager().sendSimple(p, "");
                            } else {
                                BungeeCoreSystem.getInstance().getMessager().send(p, "§2Es sind alle Reports erledigt!");
                            }
                        } catch (SQLException e1) {
                            e1.printStackTrace();
                        }
                    });
                } else {
                    BungeeCoreSystem.getInstance().getMessager().send(p, "§7Bitte benutze §e/report §e<Name> §e<Grund>");
                }
            } else if (args.length == 2) {
                if ((args[0].equalsIgnoreCase("accept") && (p.hasPermission("system.bungee.report") || (p.hasPermission("system.bungee.*"))))) {
                    String id = args[1];

                    BungeeCoreSystem.getSystem().getMySQL(MySQLDatabase.SYSTEM).select("SELECT * FROM `website_ticket` WHERE `id`=" + id + " AND `cat`='Spielerreport' AND `state`='pending'", rs -> {
                        try {
                            if (rs.next()) {
                                ProxiedPlayer reportedPlayer = ProxyServer.getInstance().getPlayer(UUID.fromString(rs.getString("target_uuid")));
                                ProxiedPlayer reporter = ProxyServer.getInstance().getPlayer(UUID.fromString(rs.getString("uuid")));

                                if (reportedPlayer != null) {
                                    p.connect(reportedPlayer.getServer().getInfo());

                                    //Syntax: report Daten werden Geupdatet.
                                    BungeeCoreSystem.getSystem().getMySQL(MySQLDatabase.SYSTEM).update("UPDATE `website_ticket` SET `state`='inwork', `team_member`='" + p.getUniqueId() + "' WHERE `id`=" + id + " AND `cat`='Spielerreport'");
                                    BungeeCoreSystem.getSystem().getMySQL(MySQLDatabase.SYSTEM).update("INSERT INTO `website_ticket_msg` (`id`, `ticket_id`, `uuid`, `timestamp`, `msg`) VALUES (NULL, " + id + ", '" + p.getUniqueId() + "', '" + millis + "', '" + p.getName() + " hat den Status des Tickets auf \"inwork\" geändert.')");

                                    if (reporter != null) {
                                        BungeeCoreSystem.getInstance().getMessager().sendSimple(p, "" +
                                                "\n§8§m----------------§r§8 [§7§l!§8] §fSystem §8§m----------------" +
                                                "\n§8[§7§l!§8] §2Du kümmerst dich nun um den Report mit der ID §f" + id + "§2. Schließe den Report mit §f/report close " + id + "§2." +
                                                "\n§8[§7§l!§8] §7Das Report-Ticket findest du unter: §fhttps://www.mcone.eu/dashboard/ticket.php?id=" + id + "" +
                                                "\n§8[§7§l!§8] §7Reporteter Spieler: §c" + reportedPlayer.getName() + "" +
                                                "\n§8[§7§l!§8] §7Titel des Reports: §f" + rs.getString("title") +
                                                "\n§8§m----------------------------------------" +
                                                "\n");
                                        BungeeCoreSystem.getInstance().getMessager().send(reporter, "§7Ein §aTeammitglied §7kümmert sich jetzt um deinen Report!");
                                    } else {
                                        BungeeCoreSystem.getInstance().getMessager().sendSimple(p, "" +
                                                "\n§8§m----------------§r§8 [§7§l!§8] §fSystem §8§m----------------" +
                                                "\n§8[§7§l!§8] §2Du kümmerst dich nun um den Report mit der ID §f" + id + "§2. Schließe den Report mit §f/report close " + id + "§2." +
                                                "\n§8[§7§l!§8] §7Das Report-Ticket findest du unter: §fhttps://www.mcone.eu/dashboard/ticket.php?id=" + id + "" +
                                                "\n§8[§7§l!§8] §7Reporteter Spieler: §c" + reportedPlayer.getName() + "" +
                                                "\n§8[§7§l!§8] §7Titel des Reports: §f" + rs.getString("title") +
                                                "\n§8§m----------------------------------------" +
                                                "\n");
                                    }

                                    if (reports.containsKey(reportedPlayer)) {
                                        this.reports.remove(reportedPlayer);
                                    }

                                    for (ProxiedPlayer player : ProxyServer.getInstance().getPlayers()) {
                                        if (player.hasPermission("system.bungee.report") || player.hasPermission("system.bungee.*")) {
                                            BungeeCoreSystem.getInstance().getMessager().send(player, "§2" + p.getName() + " §7kümmert sich um den Report mit der ID §f" + id);
                                        }
                                    }
                                } else {
                                    BungeeCoreSystem.getInstance().getMessager().send(p, "§4Der reportete Spieler ist nicht mehr online. Schließe den Report mit §c/report close 19");
                                }
                            } else {
                                BungeeCoreSystem.getInstance().getMessager().send(p, "§4Um diesen Report kümmert sich bereits ein anderes Teammitglied, oder der Report ist bereits geschlossen oder existiert nicht!");
                            }
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    });
                } else if (args[0].equalsIgnoreCase("close") || p.hasPermission("system.bungee.report")) {
                    String id = args[1];

                    BungeeCoreSystem.getSystem().getMySQL(MySQLDatabase.SYSTEM).select("SELECT * FROM `website_ticket` WHERE `id`=" + id + " AND `cat`='Spielerreport'", rs -> {
                        try {
                            if (rs.next()) {
                                if (p.getUniqueId().equals(UUID.fromString(rs.getString("team_member"))) || p.hasPermission("group.admin")) {
                                    BungeeCoreSystem.getSystem().getMySQL(MySQLDatabase.SYSTEM).update("UPDATE `website_ticket` SET `state`='closed' WHERE `id`=" + id);
                                    BungeeCoreSystem.getSystem().getMySQL(MySQLDatabase.SYSTEM).update("INSERT INTO `website_ticket_msg` (`id`, `ticket_id`, `uuid`, `timestamp`, `msg`) VALUES (NULL, " + id + ", '" + p.getUniqueId() + "', '" + millis + "', '" + p.getName() + " hat den Status des Tickets auf \"closed\" geändert.')");
                                    BungeeCoreSystem.getInstance().getMessager().send(p, " §2Der Report wurde geschlossen!");
                                } else {
                                    BungeeCoreSystem.getInstance().getMessager().send(p, "§4Du kannst dieses Ticket nicht schließen, da du es nicht angenommen hast. Benutze §c/ticket accept <id> §4um Tickets anzunehmen!");
                                }
                            } else {
                                BungeeCoreSystem.getInstance().getMessager().send(p, "§4Ein Ticket mit dieser ID existiert nicht!");
                            }
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    });
                } else {
                    final ProxiedPlayer reportedPlayer = ProxyServer.getInstance().getPlayer(args[0]);
                    if (reportedPlayer == null) {
                        BungeeCoreSystem.getInstance().getMessager().send(p, "§4Der Spieler §c" + args[0] + "§4 ist nicht online!");
                        return;
                    } else if (reportedPlayer.getName().equals(p.getName())) {
                        BungeeCoreSystem.getInstance().getMessager().send(p, "§4Du kannst dich nicht selber reporten!");
                        return;
                    } else if (reportedPlayer.hasPermission("system.bungee.report") || (reportedPlayer.hasPermission("System.*"))) {
                        BungeeCoreSystem.getInstance().getMessager().send(p, "§4Du kannst keine §cTeammitglieder reporten!");
                        return;
                    }

                    ReportReason reason = ReportReason.getReportReasonByName(args[1]);
                    if (reason != null) {
                        this.zeit.put(sender.getName(), System.currentTimeMillis());
                        this.reports.put(reportedPlayer, p);

                        Report report = new Report(p, reportedPlayer, reason);
                        report.sendToTeam();

                        return;
                    }

                    BungeeCoreSystem.getInstance().getMessager().send(p, "");
                    BungeeCoreSystem.getInstance().getMessager().send(p, "§7Du kannst ausschließlich diese §cReport-Gründe §7verwenden:");
                    BungeeCoreSystem.getInstance().getMessager().send(p, "§7---------------§cHacking§7---------------");
                    BungeeCoreSystem.getInstance().getMessager().send(p, "§cBenutze:");
                    BungeeCoreSystem.getInstance().getMessager().send(p, getReportResons());
                    BungeeCoreSystem.getInstance().getMessager().send(p, "§7-----------------------------------------");
                }
            } else {
                if (p.hasPermission("system.bungee.report")) {
                    BungeeCoreSystem.getInstance().getMessager().send(p, "§4Bitte benutze §c/report <list | accept | close> [<id>]");
                    return;
                }

                BungeeCoreSystem.getInstance().getMessager().sendSimple(p, "");
                BungeeCoreSystem.getInstance().getMessager().send(p, "§7Du kannst ausschließlich diese §cReport-Gründe §7verwenden:");
                BungeeCoreSystem.getInstance().getMessager().send(p, "§7---------------§cHacking§7---------------");
                BungeeCoreSystem.getInstance().getMessager().send(p, "§cBenutze:");
                BungeeCoreSystem.getInstance().getMessager().send(p, getReportResons());
                BungeeCoreSystem.getInstance().getMessager().send(p, "§7-----------------------------------------");
            }
        } else {
            BungeeCoreSystem.getInstance().getMessager().send(sender, BungeeCoreSystem.getInstance().getTranslationManager().get("system.command.consolesender"));
        }*/
    }

    private String getReportResons() {
        StringBuilder reportReasons = new StringBuilder();
        for (int i = 0; i < ReportReason.values().length; i++) {
            if (i + 1 >= ReportReason.values().length) {
                reportReasons.append("§7 [§f").append(ReportReason.values()[i].getName()).append("§7]");
            } else {
                reportReasons.append("§7 [§f").append(ReportReason.values()[i].getName()).append("§7]").append("\n");
            }
        }

        return reportReasons.toString();
    }

    public Iterable<String> onTabComplete(final CommandSender sender, final String[] args) {
        List<String> result = new ArrayList<>();
        if (args.length == 1) {
            result.addAll(Arrays.asList("list", "accept", "close"));
        }

        return result;
    }
}
