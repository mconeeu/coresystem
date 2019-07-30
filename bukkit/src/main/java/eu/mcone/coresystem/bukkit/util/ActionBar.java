/*
 * Copyright (c) 2017 - 2019 Dominik Lippl, Rufus Maiwald, Felix Schmid and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.bukkit.util;

import eu.mcone.coresystem.api.bukkit.util.CoreActionBar;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.entity.Player;

public class ActionBar implements CoreActionBar {

    private BaseComponent[] message;

    public ActionBar message(BaseComponent[] message) {
        this.message = message;
        return this;
    }

    public ActionBar reset() {
        this.message = null;

        return this;
    }

    public ActionBar send(Player p) {
        p.spigot().sendMessage(ChatMessageType.ACTION_BAR, message);
        return this;
    }

}
