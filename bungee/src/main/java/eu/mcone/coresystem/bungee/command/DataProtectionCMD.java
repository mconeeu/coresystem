/*
 * Copyright (c) 2017 - 2019 Dominik Lippl, Rufus Maiwald, Felix Schmid and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
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

            CoreSystem.getInstance().getMessager().send(p.bungee(),
                    new ComponentBuilder("§7§oMit dem Spielen auf MC ONE akzeptierst du unsere Datenschutzvereinbarung!\n")
                            .color(ChatColor.DARK_RED)
                            .italic(true)
                            .append("[DATENSCHUTZERKLÄRUNG ÖFFNEN]", ComponentBuilder.FormatRetention.NONE)
                            .color(ChatColor.GREEN)
                            .event(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://www.mcone.eu/datenschutz.php"))
                            .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Browser öffnen").color(ChatColor.DARK_GREEN).italic(true).create()))
                            .create()
            );
        }
    }

}
