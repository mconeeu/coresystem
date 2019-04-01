/*
 * Copyright (c) 2017 - 2019 Dominik Lippl, Rufus Maiwald, Felix Schmid and the MC ONE Minecraftnetwork. All rights reserved
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

public class YoutubeCMD extends Command {

    public YoutubeCMD() {
        super("youtube", null, "yt", "youtuber");
    }

    public void execute(final CommandSender sender, final String[] args) {
        if (args.length == 0) {
            String[] parts = BungeeCoreSystem.getInstance().getTranslationManager().get("system.bungee.command.yt").split("%button%");

            sender.sendMessage(
                    new ComponentBuilder("")
                            .append(TextComponent.fromLegacyText(parts[0]))
                            .append("§7» §3§l§nZum TS Support Channel")
                            .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§7§oTeamSpeak Client öffnen").create()))
                            .event(new ClickEvent(Action.OPEN_URL, "https://connect2ts.mcone.eu"))
                            .append(TextComponent.fromLegacyText(parts[1]))
                            .create()
            );
        }
    }

}
