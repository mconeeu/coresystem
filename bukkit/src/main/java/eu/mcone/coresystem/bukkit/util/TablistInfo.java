/*
 * Copyright (c) 2017 - 2018 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.coresystem.bukkit.util;

import eu.mcone.coresystem.api.bukkit.util.CoreTablistInfo;
import net.minecraft.server.v1_8_R3.IChatBaseComponent;
import net.minecraft.server.v1_8_R3.PacketPlayOutPlayerListHeaderFooter;
import net.minecraft.server.v1_8_R3.PlayerConnection;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;

public class TablistInfo implements CoreTablistInfo {

    private String header, footer;

    public TablistInfo header(String header) {
        this.header = header;
        return this;
    }

    public TablistInfo footer(String footer) {
        this.footer = footer;
        return this;
    }

    public TablistInfo reset() {
        this.header = null;
        this.footer = null;

        return this;
    }

    public TablistInfo send(Player p) {
        if (header == null) header = "";
        if (footer == null) footer = "";
        PlayerConnection connection = ((CraftPlayer) p).getHandle().playerConnection;

        IChatBaseComponent head = IChatBaseComponent.ChatSerializer.a("{\"text\": \"" + header + "\"}");
        IChatBaseComponent Foot = IChatBaseComponent.ChatSerializer.a("{\"text\": \"" + footer + "\"}");

        PacketPlayOutPlayerListHeaderFooter headerPacket = new PacketPlayOutPlayerListHeaderFooter(head);
        try {
            Field field = headerPacket.getClass().getDeclaredField("b");
            field.setAccessible(true);
            field.set(headerPacket, Foot);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            connection.sendPacket(headerPacket);
        }

        return this;
    }

}
