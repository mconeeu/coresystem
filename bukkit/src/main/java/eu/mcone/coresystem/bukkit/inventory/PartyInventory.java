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
import java.util.List;

public class PartyInventory {

    PartyInventory(Player p) {
        new PluginMessage(p, "PARTY", "member");
    }

    public static void create(Player p, String member) {
        Inventory inv = org.bukkit.Bukkit.createInventory(null, 54, "§8» §5§lMeine Party");

        for (int i = 0; i <= 53; i++) {
            inv.setItem(i, ItemFactory.createItem(Material.STAINED_GLASS_PANE, 7, 1, "§8//§oMCONE§8//", true));
        }

        if (!member.equals("false")) {
            String[] members = member.split(",");
            boolean isPartyLeader = isPartyLeader(p, members);

            int i = 0;
            for (String m : members) {
                if (m.equals("") || i > 44) continue;
                String[] data = m.split(":");

                List<String> lores = new ArrayList<>();
                if (data.length>2 && data[2].equals("leader")) lores.add("§e\u2600 Leader");
                if (isPartyLeader && !data[0].equalsIgnoreCase(p.getName())) lores.addAll(Arrays.asList("", "§8» §f§nRechtsklick§8 | §7§oAktionen"));

                inv.setItem(i, ItemFactory.createSkullItem("§f§l" + data[0], data[0], 1, lores));
                i++;
            }

            inv.setItem(45, ItemFactory.createItem(Material.IRON_DOOR, 0, 1, "§7§l↩ Zurück zum Profil", true));
            if (isPartyLeader) inv.setItem(49, ItemFactory.createItem(Material.BARRIER, 0, 1, "§cParty löschen", true));
            inv.setItem(53, ItemFactory.createItem(Material.SLIME_BALL, 0, 1, "§4Party verlassen", true));
        } else {
            inv.setItem(22, ItemFactory.createItem(Material.CAKE, 0, 1, "§5Party erstellen", true));
            inv.setItem(45, ItemFactory.createItem(Material.IRON_DOOR, 0, 1, "§7§l↩ Zurück zum Profil", true));
        }

        p.openInventory(inv);
    }

    public static void click(InventoryClickEvent e, Player p) {
        if ((e.getCurrentItem() == null) || !e.getCurrentItem().hasItemMeta() || e.getSlotType() == InventoryType.SlotType.OUTSIDE) {
            e.setCancelled(true);
        } else if (e.getCurrentItem().getItemMeta().getDisplayName().equals("§7§l↩ Zurück zum Profil")){
            p.playSound(p.getLocation(), Sound.NOTE_BASS, 1, 1);
            new ProfileInventory(CoreSystem.getCorePlayer(p));
        } else if (e.getCurrentItem().getItemMeta().getDisplayName().equals("§4Party verlassen")){
            p.closeInventory();
             new PluginMessage(p, "CMD" ,"party leave");
        } else if (e.getCurrentItem().getItemMeta().getDisplayName().equals("§cParty löschen")){
            p.closeInventory();
            new PluginMessage(p, "CMD" ,"party delete");
        } else if (e.getCurrentItem().getItemMeta().getDisplayName().equals("§5Party erstellen")){
            p.closeInventory();
            new PluginMessage(p, "CMD" ,"party create");
        } else if (e.getCurrentItem().getType().equals(Material.SKULL_ITEM)) {
            if (e.getCurrentItem().getItemMeta().hasLore() && e.getCurrentItem().getItemMeta().getLore().contains("§8» §f§nRechtsklick§8 | §7§oAktionen")) {
                SkullMeta meta = (SkullMeta) e.getCurrentItem().getItemMeta();
                String playerName = meta.getOwner();

                if (!playerName.equalsIgnoreCase(p.getName())) {
                    new PartyMemberInventory(p, playerName);
                    p.playSound(p.getLocation(), Sound.CHICKEN_EGG_POP, 1, 1);
                }
            }
        }
    }

    private static boolean isPartyLeader(Player p, String[] members) {
        for (String m : members) {
            String data[] = m.split(":");
            if (data[0].equals(p.getName()) && data.length>2 && data[2].equals("leader")) return true;
        }
        return false;
    }

}
