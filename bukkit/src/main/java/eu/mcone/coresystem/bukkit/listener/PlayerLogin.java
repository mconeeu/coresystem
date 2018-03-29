/*
 * Copyright (c) 2017 - 2018 Dominik L., Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.coresystem.bukkit.listener;

import eu.mcone.coresystem.bukkit.player.PermissibleBase;
import eu.mcone.coresystem.bukkit.player.CorePlayer;
import eu.mcone.coresystem.lib.exception.CoreException;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftHumanEntity;
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
        try {
            new CorePlayer(p.getUniqueId(), p.getName());
            p.setDisplayName(p.getName());
        } catch (CoreException e1) {
            e1.printStackTrace();
        }
    }

    public static void setPermissions(Player p) {
        Field fieldAccesor;

        try {
            fieldAccesor = CraftHumanEntity.class.getDeclaredField("perm");
            fieldAccesor.setAccessible(true);
            fieldAccesor.set(p, new PermissibleBase(p));
            fieldAccesor.setAccessible(false);
        } catch (IllegalAccessException | NoSuchFieldException e1) {
            e1.printStackTrace();
        }
    }

}
