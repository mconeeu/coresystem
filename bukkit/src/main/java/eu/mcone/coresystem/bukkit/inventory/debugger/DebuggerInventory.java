package eu.mcone.coresystem.bukkit.inventory.debugger;

import eu.mcone.coresystem.api.bukkit.facades.Msg;
import eu.mcone.coresystem.api.bukkit.inventory.CoreInventory;
import eu.mcone.coresystem.api.bukkit.inventory.CoreItemEvent;
import eu.mcone.coresystem.api.bukkit.inventory.InventorySlot;
import eu.mcone.coresystem.api.bukkit.inventory.category.CategoryInventory;
import eu.mcone.coresystem.api.bukkit.item.ItemBuilder;
import eu.mcone.coresystem.bukkit.util.BukkitDebugger;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class DebuggerInventory extends CoreInventory {

    private final BukkitDebugger debugger;
    private int pages, page;

    /**
     * creates a new debugger inventory
     *
     * @param player Player
     */
    public DebuggerInventory(BukkitDebugger bukkitDebugger, Player player) {
        super("§8» Debugger", player, InventorySlot.ROW_6);
        this.debugger = bukkitDebugger;
        update();
        openInventory();
    }

    private void update() {
        if (debugger.getTargets().size() > InventorySlot.ROW_6) {
            pages = InventorySlot.ROW_6 / debugger.getTargets().size();

            if (pages > 1 || page < pages) {
                setItem(InventorySlot.ROW_6_SLOT_9, CategoryInventory.RIGHT_ITEM, e -> constructPage(++page));
            }

            if (page >= 1) {
                setItem(InventorySlot.ROW_6_SLOT_1, CategoryInventory.LEFT_ITEM, e -> constructPage(--page));
            }
        } else {
            int slot = 0;
            for (String target : debugger.getTargets()) {
                setItem(slot, getItem(target), getEvent(target));
                slot++;
            }
        }
    }

    private void constructPage(int next) {
        if (next <= pages) {
            int toSkipp = InventorySlot.ROW_6 * pages, skipped = 0;

            for (String target : debugger.getTargets()) {
                if (skipped >= toSkipp) {
                    setItem(skipped, getItem(target), getEvent(target));
                }

                skipped++;
            }

            page = next;
            update();
        }
    }

    private ItemStack getItem(String target) {
        ItemBuilder builder;
        if (debugger.hasViewerTarget(player, target)) {
            builder = new ItemBuilder(Material.COMMAND, 1).displayName("§f§o" + target).enchantment(Enchantment.KNOCKBACK, 1).itemFlags(ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_ATTRIBUTES);
        } else {
            builder = new ItemBuilder(Material.COMMAND, 1).displayName("§7§o" + target).itemFlags(ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_ATTRIBUTES);
        }

        List<Player> players = debugger.getViewersForTarget(target);
        if (!players.isEmpty()) {
            for (Player player : players) {
                builder.addLore("§8» §f§o" + player.getName());
            }
        }

        return builder.create();
    }

    private CoreItemEvent getEvent(String target) {
        return e -> {
            if (debugger.hasViewerTarget(player, target)) {
                debugger.removeViewerTargets(player, target);
                Msg.sendError(player, "Du erhälst nun von dem debug target §f§l " + target + " §4keine Debug Nachrichten mehr!");
            } else {
                debugger.registerViewerTargets(player, target);
                Msg.sendSuccess(player, "Du erhälst nun alle Debug Nachrichten von dem debug target §f§l" + target);
            }

            update();
            player.updateInventory();
        };
    }
}
