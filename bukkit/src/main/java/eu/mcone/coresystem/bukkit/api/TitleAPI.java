/*
 * Copyright (c) 2017 - 2018 Dominik L., Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.coresystem.bukkit.api;

import net.minecraft.server.v1_8_R3.IChatBaseComponent;
import net.minecraft.server.v1_8_R3.PacketPlayOutTitle;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

public class TitleAPI {

    public static void sendTitle(Player p, String title, String subtitle, int fadeIn, int stay, int fadeOut) {
        IChatBaseComponent chatTitle = IChatBaseComponent.ChatSerializer.a ("{\"text\":\""+ title +"\"}");
        IChatBaseComponent chatsubtitle = IChatBaseComponent.ChatSerializer.a ("{\"text\":\""+ subtitle +"\"}");

        PacketPlayOutTitle t = new PacketPlayOutTitle (PacketPlayOutTitle.EnumTitleAction.TITLE, chatTitle);
        PacketPlayOutTitle s = new PacketPlayOutTitle (PacketPlayOutTitle.EnumTitleAction.SUBTITLE, chatsubtitle);
        PacketPlayOutTitle length = new PacketPlayOutTitle(fadeIn * 20, stay * 20, fadeOut * 20);

        ((CraftPlayer) p).getHandle().playerConnection.sendPacket(t);
        ((CraftPlayer) p).getHandle().playerConnection.sendPacket(s);
        ((CraftPlayer) p).getHandle().playerConnection.sendPacket(length);
    }

}
