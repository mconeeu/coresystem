/*
 * Copyright (c) 2017 Dominik L., Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
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

public class FriendInventory {

    FriendInventory(Player p, String friend) {
        Inventory inv = Bukkit.createInventory(null, 36, "§8» §f§l"+friend+" §8| §7Aktionen");

        for (int i = 0; i <= 35; i++) {
            inv.setItem(i, ItemFactory.createItem(Material.STAINED_GLASS_PANE, 7, 1, "§8//§oMCONE§8//", true));
        }

        inv.setItem(4, ItemFactory.createSkullItem("§f§l"+friend, friend, 1, new ArrayList<>()));
        inv.setItem(20, ItemFactory.createItem(Material.ENDER_PEARL, 0, 1, "§7Teleportieren", true));
        inv.setItem(22, ItemFactory.createItem(Material.CAKE, 0, 1, "§7In §5Party §7einladen", true));
        inv.setItem(24, ItemFactory.createItem(Material.BARRIER, 0, 1, "§4Freund entfernen", true));

        inv.setItem(27, ItemFactory.createItem(Material.IRON_DOOR, 0, 1, "§7§l↩ Zurück zum Freundemenü", true));

        p.openInventory(inv);
    }

    public static void click(InventoryClickEvent e, Player p) {
        if ((e.getCurrentItem() == null) || !e.getCurrentItem().hasItemMeta() || e.getSlotType() == InventoryType.SlotType.OUTSIDE) {
            e.setCancelled(true);
        } else if (e.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase("§7§l↩ Zurück zum Freundemenü")){
            p.playSound(p.getLocation(), Sound.NOTE_BASS, 1, 1);
            new FriendsInventory(p);
        } else if (e.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase("§7Teleportieren")) {
            SkullMeta meta = (SkullMeta) e.getClickedInventory().getItem(4).getItemMeta();
            String skullOwner = meta.getOwner();

            new PluginMessage(p, "CMD", "jump "+skullOwner);
        } else if (e.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase("§7In §5Party §7einladen")) {
            SkullMeta meta = (SkullMeta) e.getClickedInventory().getItem(4).getItemMeta();
            String skullOwner = meta.getOwner();

            new PluginMessage(p, "CMD", "party invite "+skullOwner);
        } else if (e.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase("§4Freund entfernen")) {
            SkullMeta meta = (SkullMeta) e.getClickedInventory().getItem(4).getItemMeta();
            String skullOwner = meta.getOwner();

            new PluginMessage(p, "CMD", "friend remove "+skullOwner);
        }
    }

}
