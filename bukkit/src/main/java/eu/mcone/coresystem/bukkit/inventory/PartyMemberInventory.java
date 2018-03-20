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

class PartyMemberInventory extends CoreInventory {

    PartyMemberInventory(Player p, String member) {
        super("§8» §f§l"+member+" §8| §5Aktionen", p, 36, Option.FILL_EMPTY_SLOTS);

        setItem(4, ItemFactory.createSkullItem("§f§l"+member, member, 1, new ArrayList<>()));

        setItem(21, ItemFactory.createItem(Material.NETHER_STAR, 0, 1, "§7Zum §ePartyleader§7 promoten", true), () -> {
            new PluginMessage(p, "CMD", "party promote "+member);
            p.closeInventory();
        });

        setItem(23, ItemFactory.createItem(Material.BARRIER, 0, 1, "§4Aus der Party kicken", true), () -> {
            new PluginMessage(p, "CMD", "party kick "+member);
            p.closeInventory();
        });

        setItem(27, ItemFactory.createItem(Material.IRON_DOOR, 0, 1, "§7§l↩ Zurück zum Partymenü", true), () -> {
                    p.playSound(p.getLocation(), Sound.NOTE_BASS, 1, 1);
                    new PartyInventory(p);
        });

        openInventory();
    }

}
