/*
 * Copyright (c) 2017 Dominik L., Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.bukkit.inventory;

import eu.mcone.coresystem.bukkit.CoreSystem;
import eu.mcone.coresystem.bukkit.channel.PluginMessage;
import eu.mcone.coresystem.bukkit.util.ItemFactory;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.ArrayList;
import java.util.Arrays;

public class FriendsInventory {

    FriendsInventory(Player p) {
        new PluginMessage(p, "FRIENDS", "friends");
    }

    public static void create(Player p, String friends) {
        Inventory inv = org.bukkit.Bukkit.createInventory(null, 54, "§8» §3§lMeine Freunde");

        for (int i = 0; i <= 53; i++) {
            inv.setItem(i, ItemFactory.createItem(Material.STAINED_GLASS_PANE, 7, 1, "§8//§oMCONE§8//", true));
        }



        int i = 0;
        for (String friend : friends.split(",")) {
            if (friend.equals("") || i > 44) continue;

            String[] data = friend.split(":");
            inv.setItem(i, ItemFactory.createSkullItem("§f§l"+data[1], data[1], 1, new ArrayList<>(Arrays.asList(data[2], "", "§8» §f§nRechtsklick§8 | §7§oAktionen"))));

            i++;
        }

        inv.setItem(45, ItemFactory.createItem(Material.IRON_DOOR, 0, 1, "§7§l↩ Zurück zum Profil", true));

        p.openInventory(inv);
    }

    public static void click(InventoryClickEvent e, Player p) {
        if ((e.getCurrentItem() == null) || !e.getCurrentItem().hasItemMeta() || e.getSlotType() == InventoryType.SlotType.OUTSIDE) {
            e.setCancelled(true);
        } else if (e.getCurrentItem().getItemMeta().getDisplayName().equals("§7§l↩ Zurück zum Profil")){
            p.playSound(p.getLocation(), Sound.NOTE_BASS, 1, 1);
            new ProfileInventory(CoreSystem.getCorePlayer(p));
        } else if (e.getCurrentItem().getType().equals(Material.SKULL_ITEM)) {
            SkullMeta meta = (SkullMeta) e.getCurrentItem().getItemMeta();
            String playerName = meta.getOwner();

            new FriendInventory(p, playerName);
            p.playSound(p.getLocation(), Sound.CHICKEN_EGG_POP, 1, 1);
        }
    }

}
