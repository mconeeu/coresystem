/*
 * Copyright (c) 2017 - 2020 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.bungee.command;

import eu.mcone.coresystem.api.bungee.command.CorePlayerCommand;
import eu.mcone.coresystem.api.bungee.facades.Transl;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ClickEvent.Action;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class VoteCMD extends CorePlayerCommand {

    public VoteCMD() {
        super("vote");
    }

    @Override
    public void onPlayerCommand(ProxiedPlayer p, String[] args) {
        String[] parts = Transl.get("system.bungee.command.vote", p).split("%button%");

        TextComponent message = new TextComponent(TextComponent.fromLegacyText(parts[0]));

        TextComponent button = new TextComponent(TextComponent.fromLegacyText(Transl.get("system.bungee.command.vote.button", p)));
        button.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText(Transl.get("system.command.hover.openbrowser", p))));
        button.setClickEvent(new ClickEvent(Action.OPEN_URL, "https://vote.minecraftserver.eu/?serverid=128087"));

        message.addExtra(button);
        message.addExtra(new TextComponent(TextComponent.fromLegacyText(parts[1])));

        p.sendMessage(message);
    }

}
