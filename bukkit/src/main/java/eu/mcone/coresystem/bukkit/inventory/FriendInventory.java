/*
 * Copyright (c) 2017 - 2018 Dominik L., Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.coresystem.bukkit.inventory;

import eu.mcone.coresystem.bukkit.channel.PluginMessage;
import eu.mcone.coresystem.bukkit.util.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

class FriendInventory extends CoreInventory {

    FriendInventory(Player p, String friend) {
        super("§8» §f§l"+friend+" §8| §7Aktionen", p, 36, Option.FILL_EMPTY_SLOTS);

        setItem(4, ItemBuilder.createSkullItem(friend, 1).displayName("§f§l"+friend).create());
        setItem(20, new ItemBuilder(Material.ENDER_PEARL, 1, 0).displayName("§7Teleportieren").create(), () -> new PluginMessage(p, "CMD", "jump "+friend));
        setItem(22, new ItemBuilder(Material.CAKE, 1, 0).displayName("§7In §5Party §7einladen").create(), () -> new PluginMessage(p, "CMD", "party invite "+friend));
        setItem(24, new ItemBuilder(Material.BARRIER, 1, 0).displayName("§4Freund entfernen").create(), () -> new PluginMessage(p, "CMD", "friend remove "+friend));
        setItem(27, new ItemBuilder(Material.IRON_DOOR, 1, 0).displayName("§7§l↩ Zurück zum Freundemenü").create(), () -> {
            p.playSound(p.getLocation(), Sound.NOTE_BASS, 1, 1);
            new FriendsInventory(p);
        });

        openInventory();
    }

}
