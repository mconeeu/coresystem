/*
 * Copyright (c) 2017 - 2020 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.bukkit.channel;

import eu.mcone.coresystem.api.bukkit.CoreSystem;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;

public class AntiWorldDownloader implements PluginMessageListener {

    @Override
    public void onPluginMessageReceived(String channel, Player player, byte[] bytes) {
        if (channel.equalsIgnoreCase("WDL|INIT") && !player.hasPermission("system.bukkit.allowworlddownload")) {
            CoreSystem.getInstance().getChannelHandler().sendPluginMessage(player, "BungeeCord", "KickPlayer", player.getName(),
                    "§f§lMC ONE §3Minecraftnetzwerk\n" +
                            "§4§oDu wurdest vom Netzwerk gekickt\n" +
                            "§r\n" +
                            "§7WorldDownloader ist auf MC ONE nicht erlaubt!");
        }
    }

}
