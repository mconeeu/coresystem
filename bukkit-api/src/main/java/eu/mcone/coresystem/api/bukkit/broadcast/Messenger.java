/*
 * Copyright (c) 2017 - 2020 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.api.bukkit.broadcast;

import eu.mcone.coresystem.api.core.player.GlobalMessenger;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public interface Messenger extends GlobalMessenger<Player, CommandSender> {

    void broadcast(Broadcast broadcast);

    void broadcastSimple(Broadcast broadcast);

}
