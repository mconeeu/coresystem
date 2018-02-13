/*
 * Copyright (c) 2017 Dominik L., Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.bukkit.api;

import eu.mcone.coresystem.bukkit.CoreSystem;
import net.minecraft.server.v1_8_R3.IChatBaseComponent;
import net.minecraft.server.v1_8_R3.MinecraftServer;
import net.minecraft.server.v1_8_R3.PacketPlayOutPlayerListHeaderFooter;
import net.minecraft.server.v1_8_R3.PlayerConnection;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;

public class TablistAPI {

    public static void setTabHeaderFooter(Player p, String header, String footer) {
        if (CoreSystem.cfg.getConfig().getBoolean("Tablist-HaderAndFooter")){
            if (header == null) header = "";
            if (footer == null) footer = "";
            PlayerConnection connection = ((CraftPlayer) p).getHandle().playerConnection;

            IChatBaseComponent Title = IChatBaseComponent.ChatSerializer.a("{\"text\": \"" + header + MinecraftServer.getServer().getPropertyManager().properties.getProperty("server-name") + "\"}");
            IChatBaseComponent Foot = IChatBaseComponent.ChatSerializer.a("{\"text\": \"" + footer + "\"}");

            PacketPlayOutPlayerListHeaderFooter headerPacket = new PacketPlayOutPlayerListHeaderFooter(Title);
            try {

                Field field = headerPacket.getClass().getDeclaredField("b");
                field.setAccessible(true);
                field.set(headerPacket, Foot);

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                connection.sendPacket(headerPacket);
            }
        }
    }

}
