/*
 * Copyright (c) 2017 - 2019 Dominik Lippl, Rufus Maiwald, Felix Schmid and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.bukkit.inventory;

import eu.mcone.coresystem.api.bukkit.inventory.CoreInventory;
import eu.mcone.coresystem.api.bukkit.inventory.InventoryOption;
import eu.mcone.coresystem.api.bukkit.inventory.InventorySlot;
import eu.mcone.coresystem.api.bukkit.item.ItemBuilder;
import eu.mcone.coresystem.api.bukkit.item.Skull;
import eu.mcone.coresystem.bukkit.BukkitCoreSystem;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

class PartyMemberInventory extends CoreInventory {

    PartyMemberInventory(Player p, String member) {
        super("§8» §f§l" + member + " §8| §5Aktionen", p, InventorySlot.ROW_4, InventoryOption.FILL_EMPTY_SLOTS);

        setItem(InventorySlot.ROW_1_SLOT_5, new Skull(member, 1).toItemBuilder().displayName("§f§l" + member).create());

        setItem(InventorySlot.ROW_3_SLOT_4, new ItemBuilder(Material.NETHER_STAR, 1, 0).displayName("§7Zum §ePartyleader§7 promoten").create(), e -> {
            BukkitCoreSystem.getInstance().getChannelHandler().createSetRequest(p, "CMD", "party promote " + member);
            p.closeInventory();
        });

        setItem(InventorySlot.ROW_3_SLOT_6, new ItemBuilder(Material.BARRIER, 1, 0).displayName("§4Aus der Party kicken").create(), e -> {
            BukkitCoreSystem.getInstance().getChannelHandler().createSetRequest(p, "CMD", "party kick " + member);
            p.closeInventory();
        });

        setItem(InventorySlot.ROW_4_SLOT_1, new ItemBuilder(Material.IRON_DOOR, 1, 0).displayName("§7§l↩ Zurück zum Partymenü").create(), e -> {
            p.playSound(p.getLocation(), Sound.NOTE_BASS, 1, 1);
            new PartyInventory(p).openInventory();
        });
    }

}
