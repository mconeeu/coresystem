/*
 * Copyright (c) 2017 - 2018 Dominik L., Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.coresystem.bukkit.inventory;

import eu.mcone.coresystem.bukkit.channel.PluginMessage;
import eu.mcone.coresystem.bukkit.util.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

public class FriendsInventory extends CoreInventory {

    FriendsInventory(Player p) {
        super("§8» §3§lMeine Freunde", p, 54, Option.FILL_EMPTY_SLOTS);

        new PluginMessage(player, friends -> {
            int i = 0;
            for (String friend : friends.split(",")) {
                if (friend.equals("") || i > 44) continue;

                String[] data = friend.split(":");
                setItem(i, ItemBuilder.createSkullItem(data[1], 1).displayName("§f§l"+data[1]).lore(data[2], "", "§8» §f§nRechtsklick§8 | §7§oAktionen").create(), () -> {
                    new FriendInventory(p, data[1]);
                    p.playSound(p.getLocation(), Sound.CHICKEN_EGG_POP, 1, 1);
                });

                i++;
            }

            setItem(45, new ItemBuilder(Material.IRON_DOOR, 1, 0).displayName("§7§l↩ Zurück zum Profil").create(), () -> {
                p.playSound(p.getLocation(), Sound.NOTE_BASS, 1, 1);
                new ProfileInventory(p);
            });

            openInventory();
        }, "FRIENDS", "friends");
    }

}
