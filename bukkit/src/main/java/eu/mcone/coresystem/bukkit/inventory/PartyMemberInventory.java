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

public class PartyMemberInventory {

    PartyMemberInventory(Player p, String member) {
        Inventory inv = Bukkit.createInventory(null, 36, "§8» §f§l"+member+" §8| §5Aktionen");

        for (int i = 0; i <= 35; i++) {
            inv.setItem(i, ItemFactory.createItem(Material.STAINED_GLASS_PANE, 7, 1, "§8//§oMCONE§8//", true));
        }

        inv.setItem(4, ItemFactory.createSkullItem("§f§l"+member, member, 1, new ArrayList<>()));
        inv.setItem(21, ItemFactory.createItem(Material.NETHER_STAR, 0, 1, "§7Zum §ePartyleader§7 promoten", true));
        inv.setItem(23, ItemFactory.createItem(Material.BARRIER, 0, 1, "§4Aus der Party kicken", true));

        inv.setItem(27, ItemFactory.createItem(Material.IRON_DOOR, 0, 1, "§7§l↩ Zurück zum Partymenü", true));

        p.openInventory(inv);
    }

    public static void click(InventoryClickEvent e, Player p) {
        if ((e.getCurrentItem() == null) || !e.getCurrentItem().hasItemMeta() || e.getSlotType() == InventoryType.SlotType.OUTSIDE) {
            e.setCancelled(true);
        } else if (e.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase("§7§l↩ Zurück zum Partymenü")){
            p.playSound(p.getLocation(), Sound.NOTE_BASS, 1, 1);
            new PartyInventory(p);
        } else if (e.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase("§7Zum §ePartyleader§7 promoten")) {
            SkullMeta meta = (SkullMeta) e.getClickedInventory().getItem(4).getItemMeta();
            String skullOwner = meta.getOwner();

             new PluginMessage(p, "CMD", "party promote "+skullOwner);
            p.closeInventory();
        } else if (e.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase("§4Aus der Party kicken")) {
            SkullMeta meta = (SkullMeta) e.getClickedInventory().getItem(4).getItemMeta();
            String skullOwner = meta.getOwner();

             new PluginMessage(p, "CMD", "party kick "+skullOwner);
            p.closeInventory();
        }
    }

}
