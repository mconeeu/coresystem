/*
 * Copyright (c) 2017 - 2020 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.bungee.player;

import eu.mcone.coresystem.bungee.BungeeCoreSystem;
import eu.mcone.coresystem.bungee.listener.LabyModPluginMessageListener;
import eu.mcone.coresystem.core.labymod.LMCUtils;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.protocol.packet.PluginMessage;

public class LabyModManager extends LMCUtils<ProxiedPlayer> {

    public LabyModManager(BungeeCoreSystem instance) {
        instance.registerEvents(new LabyModPluginMessageListener(this));
    }

    @Override
    protected void sendLMCMessage(ProxiedPlayer player, byte[] message) {
        player.unsafe().sendPacket(new PluginMessage("LMC", message, false));
    }

}
