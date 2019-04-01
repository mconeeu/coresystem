/*
 * Copyright (c) 2017 - 2019 Dominik Lippl, Rufus Maiwald, Felix Schmid and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.api.bungee.event;

import eu.mcone.coresystem.api.core.labymod.LabyModConnection;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Event;

@AllArgsConstructor
@Getter
public final class LabyModPlayerJoinEvent extends Event {

    private ProxiedPlayer player;
    private LabyModConnection connection;

}
