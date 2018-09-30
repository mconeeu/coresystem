/*
 * Copyright (c) 2017 - 2018 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.coresystem.bukkit.listener;

import eu.mcone.coresystem.api.bukkit.CoreSystem;
import eu.mcone.coresystem.api.bukkit.event.NpcInteractEvent;
import eu.mcone.coresystem.api.bukkit.npc.NPC;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;

public class PlayerInteractEntity implements Listener {

    @EventHandler
    public void on(PlayerInteractEntityEvent e) {
        Player p = e.getPlayer();

        if (e.getRightClicked() instanceof Player) {
            Player t = (Player) e.getRightClicked();

            for (NPC npc : CoreSystem.getInstance().getCorePlayer(p).getWorld().getNPCs()) {
                if (npc.getUuid().equals(t.getUniqueId())) {
                    Bukkit.getPluginManager().callEvent(new NpcInteractEvent(p, npc, NpcInteractEvent.Action.RIGHT_CLICK));
                    return;
                }
            }
        }
    }

}
