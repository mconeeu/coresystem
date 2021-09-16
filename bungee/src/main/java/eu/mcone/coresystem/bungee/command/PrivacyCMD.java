/*
 * Copyright (c) 2017 - 2020 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.bungee.command;

import eu.mcone.coresystem.api.bungee.CoreSystem;
import eu.mcone.coresystem.api.bungee.command.CorePlayerCommand;
import eu.mcone.coresystem.api.bungee.facades.Msg;
import eu.mcone.coresystem.api.bungee.player.CorePlayer;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class PrivacyCMD extends CorePlayerCommand {

    public PrivacyCMD() {
        super("privacy", null, "datenschutz", "datenschutzerklärung", "agb", "agbs");
    }

    @Override
    public void onPlayerCommand(ProxiedPlayer bp, String[] args) {
        CorePlayer p = CoreSystem.getInstance().getCorePlayer(bp);

        Msg.send(p.bungee(),
                new ComponentBuilder("§7§oMit dem Spielen auf MC ONE akzeptierst du unsere Datenschutzvereinbarung!\n")
                        .append("[DATENSCHUTZERKLÄRUNG ÖFFNEN]", ComponentBuilder.FormatRetention.NONE)
                        .color(ChatColor.GREEN)
                        .event(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://www.mcone.eu/privacy"))
                        .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Browser öffnen").color(ChatColor.DARK_GREEN).italic(true).create()))
                        .create()
        );
    }

}
