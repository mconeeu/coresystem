/*
 * Copyright (c) 2017 - 2020 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.bukkit.util;

import eu.mcone.coresystem.api.bukkit.util.CoreActionBar;
import net.minecraft.server.v1_8_R3.IChatBaseComponent;
import net.minecraft.server.v1_8_R3.PacketPlayOutChat;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

public class ActionBar implements CoreActionBar {

    private String message;

    public ActionBar message(String message) {
        this.message = message;
        return this;
    }

    public ActionBar reset() {
        this.message = null;

        return this;
    }

    public ActionBar send(Player p) {
        if (message != null) {
            PacketPlayOutChat packet = new PacketPlayOutChat(
                    IChatBaseComponent.ChatSerializer.a("{\"text\":\"" + message + "\"}"), (byte) 2
            );

            ((CraftPlayer) p).getHandle().playerConnection.sendPacket(packet);
        }

        return this;
    }

}
