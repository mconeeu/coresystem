/*
 * Copyright (c) 2017 - 2019 Dominik Lippl, Rufus Maiwald, Felix Schmid and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.api.bukkit.player.profile;

import eu.mcone.coresystem.api.bukkit.util.ItemBuilder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@NoArgsConstructor
@Getter @Setter
public class PlayerInventoryProfile extends GameProfile {

    private List<InventoryItem> items = new ArrayList<>();

    public PlayerInventoryProfile(Player p) {
        super(p);

        for (int i = 0; i < p.getInventory().getSize(); i++) {
            ItemStack item = p.getInventory().getItem(i);

            if (item != null) {
                items.add(new InventoryItem(i, item));
            }
        }
    }

    public void doSetItemInventory(Player p) {
        Inventory inv = p.getInventory();
        for (Map.Entry<Integer, ItemStack> item : calculateItems().entrySet()) {
            inv.setItem(item.getKey(), item.getValue());
        }
    }

    public Map<Integer, ItemStack> calculateItems() {
        Map<Integer, ItemStack> items = new HashMap<>();
        this.items.forEach(i -> {
            Map<Enchantment, Integer> enchantments = new HashMap<>();
            i.getEnchantments().forEach((e, x) -> enchantments.put(Enchantment.getByName(e), x));

            items.put(
                    i.getSlot(),
                    new ItemBuilder(i.getMaterial(), i.getAmount(), i.getDurablity())
                            .displayName(i.getDisplayname())
                            .lore(i.getLore())
                            .enchantments(enchantments)
                            .itemFlags(i.getItemFlags().toArray(new ItemFlag[]{}))
                            .unbreakable(i.unbreakable)
                            .create()
            );
        });
        return items;
    }

}
