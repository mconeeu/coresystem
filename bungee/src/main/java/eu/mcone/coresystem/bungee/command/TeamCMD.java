/*
 * Copyright (c) 2017 - 2020 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.bungee.command;

import eu.mcone.coresystem.bungee.BungeeCoreSystem;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ClickEvent.Action;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.plugin.Command;

public class TeamCMD extends Command {

    public TeamCMD() {
        super("team", null);
    }

    public void execute(final CommandSender sender, final String[] args) {
        if (args.length == 0) {
            String[] parts = BungeeCoreSystem.getInstance().getTranslationManager().get("system.bungee.command.team").split("%button%");

            sender.sendMessage(
                    new ComponentBuilder("")
                            .append(TextComponent.fromLegacyText(parts[0]))
                            .append("§7» §3§l§nTeam anzeigen!")
                            .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§7§oWebbrowser Öffnen").create()))
                            .event(new ClickEvent(Action.OPEN_URL, "https://www.mcone.eu/team.php"))
                            .append(TextComponent.fromLegacyText(parts[1]))
                            .create()
            );
        }
    }
}
