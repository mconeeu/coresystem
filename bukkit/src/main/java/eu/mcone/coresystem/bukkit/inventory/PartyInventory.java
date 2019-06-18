/*
 * Copyright (c) 2017 - 2019 Dominik Lippl, Rufus Maiwald, Felix Schmid and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.bukkit.inventory;

import eu.mcone.coresystem.api.bukkit.CoreSystem;
import eu.mcone.coresystem.api.bukkit.inventory.CoreInventory;
import eu.mcone.coresystem.api.bukkit.inventory.InventorySlot;
import eu.mcone.coresystem.api.bukkit.item.ItemBuilder;
import eu.mcone.coresystem.api.bukkit.item.Skull;
import eu.mcone.coresystem.bukkit.BukkitCoreSystem;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

class PartyInventory extends CoreInventory {

    PartyInventory() {
        super("§8» §5§lMeine Party", InventorySlot.ROW_6, Option.FILL_EMPTY_SLOTS);
    }

    public void createInventory(Player p) {
        BukkitCoreSystem.getInstance().getChannelHandler().createGetRequest(p, member -> {
            if (!member.equals("false")) {
                String[] members = member.split(",");
                boolean isPartyLeader = isPartyLeader(p, members);

                int i = 0;
                for (String m : members) {
                    if (m.equals("") || i > 44) continue;
                    String[] data = m.split(":");

                    List<String> lores = new ArrayList<>();
                    if (data.length > 2 && data[2].equals("leader")) lores.add("§e\u2600 Leader");
                    if (isPartyLeader && !data[0].equalsIgnoreCase(p.getName()))
                        lores.addAll(Arrays.asList("", "§8» §f§nRechtsklick§8 | §7§oAktionen"));

                    setItem(i, new Skull(data[0], 1).toItemBuilder().displayName("§f§l" + data[0]).lore(lores).create(), e -> {
                        new PartyMemberInventory().createInventory(p, data[0]);
                        p.playSound(p.getLocation(), Sound.CHICKEN_EGG_POP, 1, 1);
                    });
                    i++;
                }

                setItem(InventorySlot.ROW_6_SLOT_1, new ItemBuilder(Material.IRON_DOOR, 1, 0).displayName("§7§l↩ Zurück zum Profil").create(), e -> {
                    p.playSound(p.getLocation(), Sound.NOTE_BASS, 1, 1);
                    new ProfileInventory().openInventory(p);
                });

                if (isPartyLeader)
                    setItem(InventorySlot.ROW_6_SLOT_5, new ItemBuilder(Material.BARRIER, 1, 0).displayName("§cParty löschen").create(), e -> {
                        p.closeInventory();
                        BukkitCoreSystem.getInstance().getChannelHandler().createSetRequest(p, "CMD", "party delete");
                    });

                setItem(InventorySlot.ROW_6_SLOT_9, new ItemBuilder(Material.SLIME_BALL, 1, 0).displayName("§4Party verlassen").create(), e -> {
                    p.closeInventory();
                    BukkitCoreSystem.getInstance().getChannelHandler().createSetRequest(p, "CMD", "party leave");
                });
            } else {
                setItem(InventorySlot.ROW_3_SLOT_5, new ItemBuilder(Material.CAKE, 1, 0).displayName("§5Party erstellen").create(), e -> {
                    p.closeInventory();
                    BukkitCoreSystem.getInstance().getChannelHandler().createSetRequest(p, "CMD", "party create");
                });

                setItem(InventorySlot.ROW_6_SLOT_1, new ItemBuilder(Material.IRON_DOOR, 1, 0).displayName("§7§l↩ Zurück zum Profil").create(), e -> {
                    p.playSound(p.getLocation(), Sound.NOTE_BASS, 1, 1);
                    new ProfileInventory().openInventory(p);
                });
            }
        }, "PARTY", "member");
    }

    private static boolean isPartyLeader(Player p, String[] members) {
        for (String m : members) {
            String[] data = m.split(":");
            if (data[0].equals(p.getName()) && data.length > 2 && data[2].equals("leader")) return true;
        }
        return false;
    }

}
