/*
 * Copyright (c) 2017 - 2018 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.coresystem.api.bungee.event;

import eu.mcone.coresystem.api.bungee.player.BungeeCorePlayer;
import lombok.Getter;
import net.md_5.bungee.api.plugin.Event;

public class CoinsChangeEvent extends Event {

    @Getter
    private final BungeeCorePlayer player;

    public CoinsChangeEvent(BungeeCorePlayer p) {
        this.player = p;
    }

}
