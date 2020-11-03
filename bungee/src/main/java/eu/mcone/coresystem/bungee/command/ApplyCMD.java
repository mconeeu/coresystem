/*
 * Copyright (c) 2017 - 2020 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.bungee.command;

import eu.mcone.coresystem.api.bungee.command.CoreCommand;
import eu.mcone.coresystem.api.bungee.facades.Transl;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ClickEvent.Action;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;

public class ApplyCMD extends CoreCommand {

    public ApplyCMD() {
        super("apply", null, "bewerben");
    }

    public void onCommand(CommandSender sender, String[] args) {
        if (args.length == 0) {
            String[] parts = Transl.get("system.bungee.command.apply", sender).split("%button%");

            TextComponent message = new TextComponent(TextComponent.fromLegacyText(parts[0]));

            TextComponent button = new TextComponent(TextComponent.fromLegacyText(Transl.get("system.bungee.command.apply.button", sender)));
            button.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText(Transl.get("system.command.hover.openbrowser", sender))));
            button.setClickEvent(new ClickEvent(Action.OPEN_URL, "https://www.mcone.eu/apply"));

            message.addExtra(button);
            message.addExtra(new TextComponent(TextComponent.fromLegacyText(parts[1])));

            sender.sendMessage(message);
        }
    }

}
