/*
 * Copyright (c) 2017 - 2020 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.bukkit.inventory;

import eu.mcone.coresystem.api.bukkit.facades.Sound;
import eu.mcone.coresystem.api.bukkit.inventory.CoreInventory;
import eu.mcone.coresystem.api.bukkit.inventory.InventoryOption;
import eu.mcone.coresystem.api.bukkit.inventory.InventorySlot;
import eu.mcone.coresystem.api.bukkit.item.ItemBuilder;
import eu.mcone.coresystem.api.bukkit.item.Skull;
import eu.mcone.coresystem.bukkit.BukkitCoreSystem;
import eu.mcone.coresystem.bukkit.inventory.profile.CoreProfileInventory;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class FriendsInventory extends CoreInventory {

    public FriendsInventory(Player p) {
        super("§8» §9§lMeine Freunde", p, InventorySlot.ROW_6, InventoryOption.FILL_EMPTY_SLOTS);

        BukkitCoreSystem.getInstance().getChannelHandler().createGetRequest(p, friends -> {
            int i = 0;
            for (String friend : friends.split(",")) {
                if (friend.equals("") || i > 44) continue;

                String[] data = friend.split(":");
                setItem(i, new Skull(data[1], 1).toItemBuilder().displayName("§f§l" + data[1]).lore(data[2], "", "§8» §f§nRechtsklick§8 | §7§oAktionen").create(), e -> {
                    new FriendInventory(p, data[1]);
                    Sound.click(p);
                });

                i++;
            }

            setItem(InventorySlot.ROW_6_SLOT_1, new ItemBuilder(Material.IRON_DOOR, 1, 0).displayName("§7§l↩ Zurück zum Profil").create(), e -> {
                Sound.error(p);
                new CoreProfileInventory(p);
            });

            openInventory();
        }, "FRIENDS");
    }

}
