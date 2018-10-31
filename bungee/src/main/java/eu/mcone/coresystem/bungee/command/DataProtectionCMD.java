/*
 * Copyright (c) 2017 - 2018 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.coresystem.bungee.command;

import eu.mcone.coresystem.api.bungee.CoreSystem;
import eu.mcone.coresystem.api.bungee.player.CorePlayer;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class DataProtectionCMD extends Command {

    public DataProtectionCMD() {
        super("datenschutz", null, "datenschutzerklärung", "dataprotection", "agb", "agbs");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (sender instanceof ProxiedPlayer) {
            CorePlayer p = CoreSystem.getInstance().getCorePlayer((ProxiedPlayer) sender);

            if (args.length == 1 && (args[0].equalsIgnoreCase("accept") || args[0].equalsIgnoreCase("akzeptieren"))) {
                if (p.getSettings().isAcceptedAgbs()) {
                    CoreSystem.getInstance().getMessager().send(sender, "§4Du hast die Datenschutzerklärung bereits akzeptiert!");
                } else {
                    p.getSettings().setAcceptedAgbs(true);
                    p.updateSettings();

                    CoreSystem.getInstance().getMessager().send(sender, "§2Du hast die Datenschutzerklärung erfolgreich akzeptiert!");
                }
            } else {
                p.sendMessage("");
                ComponentBuilder cb = new ComponentBuilder("Du musst unsere Datenschutzerklärung akzeptieren, um auf MC ONE spielen zu können!\n")
                        .color(ChatColor.DARK_RED)
                        .italic(true)
                        .append("[DATENSCHUTZERKLÄRUNG ÖFFNEN]", ComponentBuilder.FormatRetention.NONE)
                        .color(ChatColor.GRAY)
                        .event(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://www.mcone.eu/datenschutz.php"))
                        .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Browser öffnen").color(ChatColor.GRAY).italic(true).create()));

                if (!p.getSettings().isAcceptedAgbs())
                    cb.append("\n")
                            .append("§a[DATENSCHUTZERKLÄRUNG AKZEPTIEREN]")
                            .color(ChatColor.GREEN)
                            .event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/datenschutz accept"))
                            .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Klicke zum akzeptieren").color(ChatColor.DARK_GREEN).italic(true).create()));

                cb.append("\n");
                CoreSystem.getInstance().getMessager().send(p.bungee(), cb.create());
            }
        }
    }

}
