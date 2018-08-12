/*
 * Copyright (c) 2017 - 2018 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.coresystem.bungee.report;

import eu.mcone.coresystem.bungee.BungeeCoreSystem;
import eu.mcone.coresystem.core.mysql.MySQLDatabase;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class Report {

    private ReportReason reason;
    private int id;
    private ProxiedPlayer reporter;
    private ProxiedPlayer reportedPlayer;

    public Report(ProxiedPlayer reporter, ProxiedPlayer reportedPlayer, ReportReason reason){
        this.reporter = reporter;
        this.reportedPlayer = reportedPlayer;
        this.reason = reason;

        long millis = System.currentTimeMillis() / 1000;
        BungeeCoreSystem.getInstance().getMessager().send(reporter, "§2Du hast erfolgreich den Spieler §f" + reportedPlayer.getName() + "§2 erfolgreich reportet.");

        id = BungeeCoreSystem.getSystem().getMySQL(MySQLDatabase.SYSTEM).updateWithGetId("INSERT INTO `website_ticket` (`cat`, `title`, `uuid`, `target_uuid`, `state`, `created`, `changed`, `team_member`) VALUES ('Spielerreport', 'Report gegen " + reportedPlayer.getName() + " mit dem Grund " + reason.getName() + "', '" + reporter.getUniqueId() + "', '" + reportedPlayer.getUniqueId().toString() + "', 'pending', '" + millis + "', '" + millis + "', NULL)");
        BungeeCoreSystem.getInstance().getMessager().send(reporter, "§2Du kannst den Status deines Reports auf dieser Seite einsehen: §fhttps://www.mcone.eu/dashboard/ticket.php?id=" + id);
    }

    public void sendToTeam(){
        for (ProxiedPlayer p : ProxyServer.getInstance().getPlayers()) {
            if (p.hasPermission("system.bungee.report")){
                BungeeCoreSystem.getInstance().getMessager().send(p,
                        new ComponentBuilder("[ANNEHMEN]")
                                .color(ChatColor.GREEN)
                                .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§2Zum Server...").create()))
                                .event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/report accept " + this.id))
                                .append(TextComponent.fromLegacyText("§7Der Spieler §f" + this.reporter.getName() + " §7hat §e" + this.reportedPlayer.getName() + " §7reportet (ID: " + this.id + "). §7Grund: §c" + reason.getName()))
                                .create()
                );
            }
        }
    }

}
