/*
 * Copyright (c) 2017 - 2018 Dominik L., Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.coresystem.bukkit.inventory;

import eu.mcone.coresystem.bukkit.CoreSystem;
import eu.mcone.coresystem.bukkit.channel.FutureTask;
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

public class FriendsInventory extends CoreInventory {

    FriendsInventory(Player p) {
        super("§8» §3§lMeine Freunde", p, 54, Option.FILL_EMPTY_SLOTS);

        new PluginMessage(player, friends -> {
            int i = 0;
            for (String friend : friends.split(",")) {
                if (friend.equals("") || i > 44) continue;

                String[] data = friend.split(":");
                setItem(i, ItemFactory.createSkullItem("§f§l"+data[1], data[1], 1, new ArrayList<>(Arrays.asList(data[2], "", "§8» §f§nRechtsklick§8 | §7§oAktionen"))), () -> {
                    new FriendInventory(p, data[1]);
                    p.playSound(p.getLocation(), Sound.CHICKEN_EGG_POP, 1, 1);
                });

                i++;
            }

            setItem(45, ItemFactory.createItem(Material.IRON_DOOR, 0, 1, "§7§l↩ Zurück zum Profil", true), () -> {
                p.playSound(p.getLocation(), Sound.NOTE_BASS, 1, 1);
                new ProfileInventory(p);
            });

            openInventory();
        }, "FRIENDS", "friends");
    }

}
