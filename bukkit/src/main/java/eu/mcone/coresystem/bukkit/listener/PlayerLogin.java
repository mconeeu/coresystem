/*
 * Copyright (c) 2017 - 2018 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.coresystem.bukkit.listener;

import eu.mcone.coresystem.api.core.exception.PlayerNotFoundException;
import eu.mcone.coresystem.bukkit.BukkitCoreSystem;
import eu.mcone.coresystem.bukkit.player.BukkitCorePlayer;
import eu.mcone.coresystem.bukkit.player.PermissibleBase;
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
            new BukkitCorePlayer(BukkitCoreSystem.getInstance(), p.getName());
            p.setDisplayName(p.getName());
        } catch (PlayerNotFoundException e1) {
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
