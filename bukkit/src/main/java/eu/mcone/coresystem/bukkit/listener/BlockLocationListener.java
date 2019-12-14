/*
 * Copyright (c) 2017 - 2019 Dominik Lippl, Rufus Maiwald, Felix Schmid and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.bukkit.listener;

import eu.mcone.coresystem.api.bukkit.world.CoreWorld;
import eu.mcone.coresystem.bukkit.BukkitCoreSystem;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class BlockLocationListener implements Listener {

    private final Map<UUID, BlockChangeEntry> blockChangeEntries = new HashMap<>();

    @RequiredArgsConstructor
    public static class BlockChangeEntry {
        private final CoreWorld world;
        private final String name;
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        Player p = e.getPlayer();

        if (blockChangeEntries.containsKey(p.getUniqueId()) && e.getAction().equals(Action.RIGHT_CLICK_BLOCK) && e.getClickedBlock() != null && e.getItem() == null) {
            BlockChangeEntry entry = blockChangeEntries.get(p.getUniqueId());

            if (entry.world.bukkit().equals(p.getWorld())) {
                entry.world.setBlockLocation(entry.name, e.getClickedBlock().getLocation()).save();
                BukkitCoreSystem.getInstance().getMessager().send(p,
                        "§2Die Location §a"+entry.name+"§2 wurde erfolgreich für den geklickten Block " +
                                "mit dem Material §f"+e.getClickedBlock().getType().toString()+"§2 abgespeichert"
                );
            } else {
                BukkitCoreSystem.getSystem().getMessager().send(p,
                        "§4Du hattest den set Befehl in der Welt §c"+entry.world.getName()+"§4 ausgeführt! " +
                        "Falls du eine BlockLocation in dieser Welt setzen willst, gib ihn hier erneut an."
                );
            }

            blockChangeEntries.remove(p.getUniqueId());
        }
    }

    public void addBlockLocationEntry(UUID uuid, BlockChangeEntry entry) {
        blockChangeEntries.put(uuid, entry);
    }

}
