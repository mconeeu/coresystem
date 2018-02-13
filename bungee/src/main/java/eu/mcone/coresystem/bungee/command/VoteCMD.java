/*
 * Copyright (c) 2017 - 2018 Dominik L., Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.coresystem.bungee.command;

import eu.mcone.coresystem.bungee.CoreSystem;
import eu.mcone.coresystem.bungee.utils.Messager;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ClickEvent.Action;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class VoteCMD extends Command {
    public VoteCMD()
    {
    super("vote", null);
    }

    public void execute(final CommandSender sender, final String[] args) {
        if (sender instanceof ProxiedPlayer) {
            if (args.length == 0) {
                String[] parts = CoreSystem.sqlconfig.getConfigValue("CMD-Vote").split("%button%");

                sender.sendMessage(
                        new ComponentBuilder("")
                                .append(TextComponent.fromLegacyText(parts[0]))
                                .append("§7» §3§l§nVoten")
                                .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§7§oWebbrowser Öffnen").create()))
                                .event(new ClickEvent(Action.OPEN_URL, "https://vote.minecraftserver.eu/?serverid=128087"))
                                .append(TextComponent.fromLegacyText(parts[1]))
                                .create()
                );
            }
        } else {
            Messager.sendSimple(sender, CoreSystem.sqlconfig.getConfigValue("System-Konsolen-Sender"));
        }
    }
}

