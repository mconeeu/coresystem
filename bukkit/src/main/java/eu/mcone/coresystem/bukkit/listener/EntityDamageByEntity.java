/*
 * Copyright (c) 2017 - 2019 Dominik Lippl, Rufus Maiwald, Felix Schmid and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.bukkit.listener;

import eu.mcone.coresystem.api.bukkit.CoreSystem;
import eu.mcone.coresystem.api.bukkit.event.NpcInteractEvent;
import eu.mcone.coresystem.api.bukkit.npc.NPC;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class EntityDamageByEntity implements Listener {

    @EventHandler
    public void on(EntityDamageByEntityEvent e) {
        if (e.getDamager() instanceof Player) {
            Player p = (Player) e.getDamager();

            if (e.getEntity() instanceof Player) {
                Player t = (Player) e.getEntity();

                for (NPC npc : CoreSystem.getInstance().getCorePlayer(p).getWorld().getNPCs()) {
                    if (npc.getUuid().equals(t.getUniqueId())) {
                        Bukkit.getPluginManager().callEvent(new NpcInteractEvent(p, npc, NpcInteractEvent.Action.LEFT_CLICK));
                        return;
                    }
                }
            }
        }
    }

}
