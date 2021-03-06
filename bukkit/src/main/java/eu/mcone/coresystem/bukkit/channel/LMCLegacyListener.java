/*
 * Copyright (c) 2017 - 2020 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.bukkit.channel;

import eu.mcone.coresystem.api.bukkit.event.labymod.LabyModPlayerJoinEvent;
import eu.mcone.coresystem.api.core.labymod.LabyModConnection;
import eu.mcone.coresystem.bukkit.BukkitCoreSystem;
import eu.mcone.coresystem.core.labymod.LMCUtils;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;

import java.util.ArrayList;

@RequiredArgsConstructor
public class LMCLegacyListener implements PluginMessageListener {

    private final LMCUtils<?> utils;

    @Override
    public void onPluginMessageReceived(String s, Player player, byte[] bytes) {
        ByteBuf buf = Unpooled.wrappedBuffer(bytes);
        final String version = utils.readString(buf, Short.MAX_VALUE);

        Bukkit.getScheduler().runTask(BukkitCoreSystem.getSystem(), () -> {
            if (!player.isOnline())
                return;

            Bukkit.getPluginManager().callEvent(new LabyModPlayerJoinEvent(player,
                    new LabyModConnection(
                            player.getUniqueId(),
                            version,
                            false, false,
                            0, 0,
                            new ArrayList<>()
                    )
            ));
        });
    }

}
