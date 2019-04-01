/*
 * Copyright (c) 2017 - 2019 Dominik Lippl, Rufus Maiwald, Felix Schmid and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.bukkit.listener;

import com.mojang.authlib.properties.Property;
import eu.mcone.coresystem.api.core.player.SkinInfo;
import eu.mcone.coresystem.bukkit.BukkitCoreSystem;
import eu.mcone.coresystem.bukkit.player.BukkitCorePlayer;
import eu.mcone.coresystem.bukkit.player.PermissibleBase;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftHumanEntity;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;

import java.lang.reflect.Field;

public class PlayerLogin implements Listener {

    @EventHandler(priority = EventPriority.LOWEST)
    public void on(PlayerLoginEvent e) {
        Player p = e.getPlayer();
        setPermissions(p);

        Property textures = ((CraftPlayer) p).getHandle().getProfile().getProperties().get("textures").iterator().next();
        new BukkitCorePlayer(BukkitCoreSystem.getInstance(), e.getAddress(), new SkinInfo(p.getName(), textures.getValue(), textures.getSignature()), p);
        p.setDisplayName(p.getName());
    }

    public static void setPermissions(Player p) {
        Field f;

        try {
            f = CraftHumanEntity.class.getDeclaredField("perm");
            f.setAccessible(true);
            f.set(p, new PermissibleBase(p));
            f.setAccessible(false);
        } catch (IllegalAccessException | NoSuchFieldException e1) {
            e1.printStackTrace();
        }
    }

}
