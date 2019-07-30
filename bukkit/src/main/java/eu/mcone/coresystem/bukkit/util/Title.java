/*
 * Copyright (c) 2017 - 2019 Dominik Lippl, Rufus Maiwald, Felix Schmid and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.bukkit.util;

import eu.mcone.coresystem.api.bukkit.util.CoreTitle;
import org.bukkit.entity.Player;

public class Title implements CoreTitle {

    private String title, subtitle;
    private int fadeIn = 1, stay = 5, fadeOut = 1;

    @Override
    public Title title(String title) {
        this.title = title;
        return this;
    }

    @Override
    public Title subTitle(String subTitle) {
        this.subtitle = subTitle;
        return this;
    }

    @Override
    public Title fadeIn(int fadeIn) {
        this.fadeIn = fadeIn;
        return this;
    }

    @Override
    public Title stay(int stay) {
        this.stay = stay;
        return this;
    }

    @Override
    public Title fadeOut(int fadeOut) {
        this.fadeOut = fadeOut;
        return this;
    }

    @Override
    public CoreTitle reset() {
        this.title = null;
        this.subtitle = null;
        this.fadeIn = -1;
        this.stay = -1;
        this.fadeOut = -1;


        return this;
    }

    @Override
    public Title send(Player p) {
        p.sendTitle(title, subtitle, fadeIn, stay, fadeOut);
        return this;
    }

}
