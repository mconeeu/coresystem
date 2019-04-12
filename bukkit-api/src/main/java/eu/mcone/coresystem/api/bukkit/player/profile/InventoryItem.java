/*
 * Copyright (c) 2017 - 2019 Dominik Lippl, Rufus Maiwald, Felix Schmid and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.api.bukkit.player.profile;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class InventoryItem {

    private int slot, amount;
    private Material material;
    private short durablity;
    private String displayname;
    private List<String> lore;
    private Map<String, Integer> enchantments;
    private Set<ItemFlag> itemFlags;
    boolean unbreakable;

    public InventoryItem(int slot, ItemStack item) {
        this.slot = slot;
        this.amount = item.getAmount();
        this.material = item.getType();
        this.durablity = item.getDurability();
        this.displayname = item.getItemMeta().getDisplayName();
        this.lore = item.getItemMeta().getLore();

        Map<String, Integer> enchantments = new HashMap<>();
        item.getEnchantments().forEach((e, x) -> enchantments.put(e.getName(), x));
        this.enchantments = enchantments;

        this.itemFlags = item.getItemMeta().getItemFlags();
        this.unbreakable = item.getItemMeta().spigot().isUnbreakable();
    }

}
