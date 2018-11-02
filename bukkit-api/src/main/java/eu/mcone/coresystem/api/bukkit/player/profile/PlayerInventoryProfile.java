/*
 * Copyright (c) 2017 - 2018 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.coresystem.api.bukkit.player.profile;

import eu.mcone.coresystem.api.bukkit.util.ItemBuilder;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import java.util.*;

@NoArgsConstructor
@Getter @Setter
public class PlayerInventoryProfile extends GameProfile {

    private List<InventoryItem> items = new ArrayList<>();

    public PlayerInventoryProfile(Player p) {
        super(p);

        for (int i = 0; i < p.getInventory().getSize(); i++) {
            ItemStack item = p.getInventory().getItem(i);

            if (item != null) {
                Map<String, Integer> enchantments = new HashMap<>();
                item.getEnchantments().forEach((e, x) -> enchantments.put(e.getName(), x));

                items.add(new InventoryItem(
                        i,
                        item.getAmount(),
                        item.getType(),
                        item.getDurability(),
                        item.getItemMeta().getDisplayName(),
                        item.getItemMeta().getLore(),
                        enchantments,
                        item.getItemMeta().getItemFlags(),
                        item.getItemMeta().spigot().isUnbreakable()
                ));
            }
        }
    }

    @NoArgsConstructor
    @AllArgsConstructor
    @Getter @Setter
    public class InventoryItem {

        private int slot, amount;
        private Material material;
        private short durablity;
        private String displayname;
        private List<String> lore;
        private Map<String, Integer> enchantments;
        private Set<ItemFlag> itemFlags;
        boolean unbreakable;

    }

    public void setItemInventory(Player p) {
        Inventory inv = Bukkit.createInventory(p, InventoryType.PLAYER);
        for (Map.Entry<Integer, ItemStack> item : getItemMap().entrySet()) {
            inv.setItem(item.getKey(), item.getValue());
        }
        p.openInventory(inv);
    }

    public Map<Integer, ItemStack> getItemMap() {
        Map<Integer, ItemStack> items = new HashMap<>();
        this.items.forEach(i -> {
            Map<Enchantment, Integer> enchantments = new HashMap<>();
            i.getEnchantments().forEach((e, x) -> enchantments.put(Enchantment.getByName(e), x));

            items.put(
                    i.slot,
                    new ItemBuilder(i.material, i.amount, i.durablity)
                            .displayName(i.displayname)
                            .lore(i.lore)
                            .enchantments(enchantments)
                            .itemFlags(i.itemFlags.toArray(new ItemFlag[]{}))
                            .unbreakable(i.unbreakable)
                            .create()
            );
        });
        return items;
    }

}
