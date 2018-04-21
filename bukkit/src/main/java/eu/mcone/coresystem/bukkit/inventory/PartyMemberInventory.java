/*
 * Copyright (c) 2017 - 2018 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.coresystem.bukkit.inventory;

import eu.mcone.coresystem.api.bukkit.inventory.CoreInventory;
import eu.mcone.coresystem.api.bukkit.util.ItemBuilder;
import eu.mcone.coresystem.bukkit.BukkitCoreSystem;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

class PartyMemberInventory extends CoreInventory {

    PartyMemberInventory(Player p, String member) {
        super("§8» §f§l"+member+" §8| §5Aktionen", p, 36, Option.FILL_EMPTY_SLOTS);

        setItem(4, ItemBuilder.createSkullItem(member, 1).displayName("§f§l"+member).create());

        setItem(21, new ItemBuilder(Material.NETHER_STAR, 1, 0).displayName("§7Zum §ePartyleader§7 promoten").create(), () -> {
            BukkitCoreSystem.getInstance().getChannelHandler().sendPluginMessage(p, "CMD", "party promote "+member);
            p.closeInventory();
        });

        setItem(23, new ItemBuilder(Material.BARRIER, 1, 0).displayName("§4Aus der Party kicken").create(), () -> {
            BukkitCoreSystem.getInstance().getChannelHandler().sendPluginMessage(p, "CMD", "party kick "+member);
            p.closeInventory();
        });

        setItem(27, new ItemBuilder(Material.IRON_DOOR, 1, 0).displayName("§7§l↩ Zurück zum Partymenü").create(), () -> {
            p.playSound(p.getLocation(), Sound.NOTE_BASS, 1, 1);
            new PartyInventory(p);
        });

        openInventory();
    }

}
