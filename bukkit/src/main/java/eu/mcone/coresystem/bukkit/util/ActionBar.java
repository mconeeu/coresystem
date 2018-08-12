/*
 * Copyright (c) 2017 - 2018 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.coresystem.bukkit.util;

import eu.mcone.coresystem.api.bukkit.util.CoreActionBar;
import net.minecraft.server.v1_8_R3.IChatBaseComponent;
import net.minecraft.server.v1_8_R3.PacketPlayOutChat;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

public class ActionBar implements CoreActionBar {

    private String message;
    private int stay;

    public ActionBar() {
        this.stay = -1;
    }

    public ActionBar message(String message) {
        this.message = message;
        return this;
    }

    public ActionBar reset() {
        this.message = null;
        this.stay = -1;

        return this;
    }

    public ActionBar send(Player p) {
        if (message != null && stay > 0) {
            PacketPlayOutChat packet = new PacketPlayOutChat(
                    IChatBaseComponent.ChatSerializer.a("{\"text\":\"" + message + "\"}"), (byte) 2
            );

            ((CraftPlayer) p).getHandle().playerConnection.sendPacket(packet);
        }

        return this;
    }

}
