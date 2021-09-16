package eu.mcone.coresystem.api.bukkit.inventory.menu;

import eu.mcone.coresystem.api.bukkit.inventory.*;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class MenuInventory extends CoreInventory {

    @Getter
    @Setter
    private int maxItems;
    private final List<CoreItemStack> menuItems;
    private CoreItemStack infoItem;

    public MenuInventory(Player p, String title, int maxItems) {
        super(title, p, getInvSize(maxItems), InventoryOption.FILL_EMPTY_SLOTS);
        this.maxItems = maxItems;
        this.menuItems = new ArrayList<>();
    }

    public void addMenuItem(ItemStack item) {
        addMenuItem(item, null);
    }

    public void addMenuItem(ItemStack item, CoreItemEvent event) {
        if (menuItems.size()+1 <= maxItems) {
            menuItems.add(new CoreItemStack(item, event));
        } else throw new IllegalStateException("Could not add another menu item. Given max size limit from constructor exceeded: "+ maxItems +"!");
    }

    public void setInfoItem(ItemStack item) {
        setInfoItem(item, null);
    }

    public void setInfoItem(ItemStack item, CoreItemEvent event) {
        infoItem = new CoreItemStack(item, event);
    }

    @Override
    protected Inventory createInventory() {
        int maxRows, row;
        if (infoItem != null) {
            setItem(InventorySlot.ROW_1_SLOT_5, infoItem.getItemStack(), infoItem.getCoreItemEvent());
            maxRows = 3;
            row = InventorySlot.ROW_3 - 9;

            if (maxItems > 11) {
                throw new IllegalArgumentException("Could not initialize MenuInventory. Menu item amount must be smaller than or equal to 11 if info item is used!");
            }
        } else {
            maxRows = 4;
            row = InventorySlot.ROW_2 - 9;
        }

        boolean intend = (menuItems.size() <= 4) == (menuItems.size() % 2 != 0);
        for (int i = 0, currentItem = 0; i < maxRows; i++, row+= 9, intend = !intend) {
            int remainingItems = menuItems.size() - currentItem;
            if (remainingItems == 0) {
                break;
            }

            int maxItemAmount = calculateMaxItemAmount(i, menuItems.size(), intend),
                    itemAmount = Math.min(remainingItems, maxItemAmount);
            int[] slots = calculateSlots(intend, itemAmount);

            for (int x = 0; x < itemAmount; x++, currentItem++) {
                CoreItemStack item = menuItems.get(currentItem);
                setItem(row + slots[x], item.getItemStack(), item.getCoreItemEvent());
            }
        }

        return super.createInventory();
    }

    private int calculateMaxItemAmount(int row, int menuItemsSize, boolean intend) {
        int maxItemAmount = intend ? 3 : 4;

        if (menuItemsSize == 5 && row == 0) {
            maxItemAmount = 2;
        } else if (menuItemsSize == 6 && row == 1) {
            maxItemAmount = 2;
        }

        return maxItemAmount;
    }

    private int[] calculateSlots(boolean intend, int itemAmount) {
        int[] slots = null;

        if (intend) {
            switch (itemAmount) {
                case 1: slots = new int[]{InventorySlot.ROW_1_SLOT_5}; break;
                case 2: slots = new int[]{InventorySlot.ROW_1_SLOT_3, InventorySlot.ROW_1_SLOT_7}; break;
                case 3: slots = new int[]{InventorySlot.ROW_1_SLOT_3, InventorySlot.ROW_1_SLOT_5, InventorySlot.ROW_1_SLOT_7}; break;
            }
        } else {
            switch (itemAmount) {
                case 2: slots = new int[]{InventorySlot.ROW_1_SLOT_4, InventorySlot.ROW_1_SLOT_6}; break;
                case 4: slots = new int[]{InventorySlot.ROW_1_SLOT_2, InventorySlot.ROW_1_SLOT_4, InventorySlot.ROW_1_SLOT_6, InventorySlot.ROW_1_SLOT_8}; break;
            }
        }

        if (slots != null) {
            return slots;
        } else throw new IllegalStateException("Could not calculcate slots... Interal error!");
    }

    public static int getInvSize(int menuItems) {
        return (getMenuRowsAmount(menuItems) * 9) + InventorySlot.ROW_3;
    }

    public static int getMenuRowsAmount(int menuItems) {
        if (menuItems > 14) {
            throw new IllegalArgumentException("Could not initialize MenuInventory. Menu item amount must be smaller than or equal to 14");
        } else if (menuItems > 11) {
            return 4;
        } else if (menuItems > 7 || menuItems == 6) {
            return 3;
        } else if (menuItems > 4) {
            return 2;
        } else {
            return 1;
        }
    }

}
