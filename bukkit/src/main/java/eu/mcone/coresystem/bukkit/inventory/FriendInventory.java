/*
 * Copyright (c) 2017 - 2018 Dominik L., Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.coresystem.bukkit.inventory;

import eu.mcone.coresystem.bukkit.channel.PluginMessage;
import eu.mcone.coresystem.bukkit.util.ItemFactory;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.ArrayList;

class FriendInventory extends CoreInventory {

    FriendInventory(Player p, String friend) {
        super("§8» §f§l"+friend+" §8| §7Aktionen", p, 36, Option.FILL_EMPTY_SLOTS);

        setItem(4, ItemFactory.createSkullItem("§f§l"+friend, friend, 1, new ArrayList<>()));
        setItem(20, ItemFactory.createItem(Material.ENDER_PEARL, 0, 1, "§7Teleportieren", true), () -> new PluginMessage(p, "CMD", "jump "+friend));
        setItem(22, ItemFactory.createItem(Material.CAKE, 0, 1, "§7In §5Party §7einladen", true), () -> new PluginMessage(p, "CMD", "party invite "+friend));
        setItem(24, ItemFactory.createItem(Material.BARRIER, 0, 1, "§4Freund entfernen", true), () -> new PluginMessage(p, "CMD", "friend remove "+friend));
        setItem(27, ItemFactory.createItem(Material.IRON_DOOR, 0, 1, "§7§l↩ Zurück zum Freundemenü", true), () -> {
            p.playSound(p.getLocation(), Sound.NOTE_BASS, 1, 1);
            new FriendsInventory(p);
        });

        openInventory();
    }

}
