/*
 * Copyright (c) 2017 - 2019 Dominik Lippl, Rufus Maiwald, Felix Schmid and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.api.bukkit.player.profile;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.HashMap;
import java.util.Map;

@NoArgsConstructor
@Getter @Setter
public class PlayerInventoryProfile extends GameProfile {

    private Map<String, ItemStack> items = new HashMap<>();

    public PlayerInventoryProfile(Player p) {
        super(p);

        for (int i = 0; i < p.getInventory().getSize(); i++) {
            ItemStack item = p.getInventory().getItem(i);

            if (item != null) {
                items.put(String.valueOf(i), item);
            }
        }

        ItemStack[] armor = p.getInventory().getArmorContents();
        for (int i=0, x=36; i<4; i++, x++) {
            items.put(String.valueOf(x), armor[i]);
        }
    }

    public void doSetItemInventory(Player p) {
        PlayerInventory inv = p.getInventory();

        ItemStack[] armor = new ItemStack[4];
        for (Map.Entry<String, ItemStack> item : items.entrySet()) {
            int i = Integer.valueOf(item.getKey());

            if (i < 36) {
                inv.setItem(i, item.getValue());
            } else {
                armor[i-36] = item.getValue();
            }
        }

        inv.setArmorContents(armor);
    }

}
