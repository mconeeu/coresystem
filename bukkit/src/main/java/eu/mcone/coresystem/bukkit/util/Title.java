/*
 * Copyright (c) 2017 - 2019 Dominik Lippl, Rufus Maiwald, Felix Schmid and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.bukkit.util;

import eu.mcone.coresystem.api.bukkit.util.CoreTitle;
import net.minecraft.server.v1_8_R3.IChatBaseComponent;
import net.minecraft.server.v1_8_R3.PacketPlayOutTitle;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

public class Title implements CoreTitle {

    private String title, subtitle;
    private int fadeIn = 1, stay = 5, fadeOut = 1;

    @Override
    public Title title(String title) {
        this.title = title;
        return this;
    }

    @Override
    public Title subTitle(String subTitle) {
        this.subtitle = subTitle;
        return this;
    }

    @Override
    public Title fadeIn(int fadeIn) {
        this.fadeIn = fadeIn;
        return this;
    }

    @Override
    public Title stay(int stay) {
        this.stay = stay;
        return this;
    }

    @Override
    public Title fadeOut(int fadeOut) {
        this.fadeOut = fadeOut;
        return this;
    }

    @Override
    public CoreTitle reset() {
        this.title = null;
        this.subtitle = null;
        this.fadeIn = -1;
        this.stay = -1;
        this.fadeOut = -1;


        return this;
    }

    @Override
    public Title send(Player p) {
        if (title != null || subtitle != null) {
            if (title != null) {
                IChatBaseComponent chatTitle = IChatBaseComponent.ChatSerializer.a("{\"text\":\"" + title + "\"}");

                ((CraftPlayer) p).getHandle().playerConnection.sendPacket(
                        new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.TITLE, chatTitle)
                );
            }

            if (subtitle != null) {
                IChatBaseComponent chatsubtitle = IChatBaseComponent.ChatSerializer.a("{\"text\":\"" + subtitle + "\"}");

                ((CraftPlayer) p).getHandle().playerConnection.sendPacket(
                        new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.SUBTITLE, chatsubtitle)
                );
            }

            ((CraftPlayer) p).getHandle().playerConnection.sendPacket(
                    new PacketPlayOutTitle(fadeIn * 20, stay * 20, fadeOut * 20)
            );
        }

        return this;
    }

}
