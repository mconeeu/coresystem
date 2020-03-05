/*
 * Copyright (c) 2017 - 2019 Dominik Lippl, Rufus Maiwald, Felix Schmid and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.api.bukkit.player.profile;

import eu.mcone.coresystem.api.bukkit.CoreSystem;
import eu.mcone.coresystem.api.bukkit.inventory.InventorySlot;
import eu.mcone.coresystem.api.bukkit.player.CorePlayer;
import eu.mcone.coresystem.api.bukkit.util.CoreTitle;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.*;

@NoArgsConstructor
@Getter @Setter
public class PlayerInventoryProfile extends PlayerDataProfile {

    public static final String ENDERCHEST_TITLE = "§8Deine Enderkiste";
    private static final LinkedHashMap<String, Integer> ENDERCHEST_SIZES = new LinkedHashMap<String, Integer>(){{
        put("system.bukkit.enderchest.xl", InventorySlot.ROW_6);
        put("system.bukkit.enderchest.l", InventorySlot.ROW_5);
        put("system.bukkit.enderchest.m", InventorySlot.ROW_4);
        put("system.bukkit.enderchest.s", InventorySlot.ROW_3);
        put("system.bukkit.enderchest.xs", InventorySlot.ROW_2);
    }};
    private static final CoreTitle EC_SIZE_CHANGED_TITLE = CoreSystem.getInstance().createTitle()
            .title("§c§lENDERCHEST GRößE VERäNDERT!")
            .subTitle("§4Ein paar Deiner Enderchest-Items sind gedropt")
            .stay(10);

    private Map<String, ItemStack> items = new HashMap<>();
    private Map<String, ItemStack> enderChestItems = new HashMap<>();

    private transient Inventory enderchest = Bukkit.createInventory(null, InventorySlot.ROW_3, ENDERCHEST_TITLE);
    private transient boolean sizeChange = false;

    public PlayerInventoryProfile(Player p, Inventory enderchest, Map<String, Location> homes) {
        this(p, homes);

        for (int i = 0; i < enderchest.getSize(); i++) {
            if (enderchest.getItem(i) != null) {
                enderChestItems.put(String.valueOf(i), enderchest.getItem(i));
            }
        }
    }

    public PlayerInventoryProfile(Player p, Inventory enderchest) {
        this(p);

        for (int i = 0; i < enderchest.getSize(); i++) {
            if (enderchest.getItem(i) != null) {
                enderChestItems.put(String.valueOf(i), enderchest.getItem(i));
            }
        }
    }

    public PlayerInventoryProfile(Player p, Map<String, Location> homes) {
        super(p, homes);

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

    @Override
    public void doSetData(Player p) {
        super.doSetData(p);

        CorePlayer cp = CoreSystem.getInstance().getCorePlayer(p);
        p.getInventory().clear();
        p.getInventory().setArmorContents(null);

        PlayerInventory inv = p.getInventory();
        ItemStack[] armor = new ItemStack[4];
        for (Map.Entry<String, ItemStack> item : items.entrySet()) {
            int i = Integer.parseInt(item.getKey());

            if (i < 36) {
                inv.setItem(i, item.getValue());
            } else {
                armor[i-36] = item.getValue();
            }
        }
        inv.setArmorContents(armor);

        int size = InventorySlot.ROW_3;

        for (Map.Entry<String, Integer> e : ENDERCHEST_SIZES.entrySet()) {
            if (cp.hasPermission(e.getKey())) {
                size = e.getValue();
                break;
            }
        }

        enderchest = Bukkit.createInventory(null, size, ENDERCHEST_TITLE);
        List<ItemStack> leftItems = new ArrayList<>();

        for (Map.Entry<String, ItemStack> e : enderChestItems.entrySet()) {
            int slot = Integer.parseInt(e.getKey());

            if (slot <= enderchest.getSize()) {
                enderchest.setItem(slot, e.getValue());
            } else {
                leftItems.add(e.getValue());
            }
        }

        if (!leftItems.isEmpty()) {
            Map<Integer, ItemStack> dropItems = enderchest.addItem(leftItems.toArray(new ItemStack[0]));
            sizeChange = true;

            if (!dropItems.isEmpty()) {
                for (ItemStack i : dropItems.values()) {
                    p.getWorld().dropItem(p.getLocation().add(0,1,0), i);
                }

                Bukkit.getScheduler().runTaskLaterAsynchronously(CoreSystem.getInstance(), () -> EC_SIZE_CHANGED_TITLE.send(p), 10L);
            }
        }
    }

}
