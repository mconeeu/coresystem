/*
 * Copyright (c) 2017 - 2019 Dominik Lippl, Rufus Maiwald, Felix Schmid and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.bukkit.util;

import eu.mcone.coresystem.api.bukkit.util.CoreTablistInfo;
import org.bukkit.entity.Player;

public class TablistInfo implements CoreTablistInfo {

    private String header, footer;

    public TablistInfo header(String header) {
        this.header = header;
        return this;
    }

    public TablistInfo footer(String footer) {
        this.footer = footer;
        return this;
    }

    public TablistInfo reset() {
        this.header = null;
        this.footer = null;

        return this;
    }

    public TablistInfo send(Player p) {
        if (header == null) header = "";
        if (footer == null) footer = "";

        p.setPlayerListHeaderFooter(header, footer);
        return this;
    }

}
