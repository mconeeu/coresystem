/*
 * Copyright (c) 2017 - 2018 Dominik L., Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.coresystem.bungee.event;

import eu.mcone.coresystem.bungee.player.CorePlayer;
import lombok.Getter;
import net.md_5.bungee.api.plugin.Event;

public class CoinsChangeEvent extends Event {

    @Getter
    private final CorePlayer player;

    public CoinsChangeEvent(CorePlayer p) {
        this.player = p;
    }

}
