package eu.mcone.coresystem.api.bukkit.inventory.settings;

import eu.mcone.coresystem.api.bukkit.facades.Sound;
import eu.mcone.coresystem.api.bukkit.inventory.CoreInventory;
import eu.mcone.coresystem.api.bukkit.inventory.CoreItemEvent;
import eu.mcone.coresystem.api.bukkit.inventory.InventoryOption;
import eu.mcone.coresystem.api.bukkit.inventory.InventorySlot;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SettingsInventory extends CoreInventory {

    private static final int MAX_ITEMS = 7;

    private final List<Setting<?>> settings;
    private final CoreItemEvent backAction;

    public SettingsInventory(String title, Player player) {
        this(title, player, null);
    }

    public SettingsInventory(String title, Player player, CoreItemEvent backAction) {
        super(title, player, InventorySlot.ROW_5, InventoryOption.FILL_EMPTY_SLOTS);
        this.settings = new ArrayList<>();
        this.backAction = backAction;
    }

    public SettingsInventory addSetting(Setting<?>... settings) {
        this.settings.addAll(Arrays.asList(settings));
        return this;
    }

    @Override
    public Inventory openInventory() {
        return openInventory(1);
    }

    public Inventory openInventory(int page) {
        if (settings.size() >= (((page - 1) * MAX_ITEMS) + 1)) {
            boolean lastPage = (((page) * MAX_ITEMS) + 1) > settings.size();
            int firstOption = ((page - 1) * MAX_ITEMS);

            int startSlot = InventorySlot.ROW_2_SLOT_2, items = settings.size() - firstOption;
            boolean fewerItems = items < MAX_ITEMS, even = (items % 2) == 0;

            if (fewerItems) {
                startSlot += (MAX_ITEMS-items) / 2;
            }

            for (int i = firstOption, x = startSlot; i < settings.size() && x <= InventorySlot.ROW_2_SLOT_8; i++, x++) {
                if (fewerItems && even && x == InventorySlot.ROW_2_SLOT_5) {
                    i--;
                    continue;
                }

                setSetting(player, x, settings.get(i));
            }

            if (settings.size() > 7) {
                if (page > 1) {
                    setItem(InventorySlot.ROW_5_SLOT_4, LEFT_ITEM, e -> new SettingsInventory(inventory.getTitle(), player, backAction).addSetting(settings.toArray(new Setting<?>[0])).openInventory(page-1));
                } else {
                    setItem(InventorySlot.ROW_5_SLOT_4, LEFT_BLOCKED_ITEM, e -> Sound.error(player));
                }

                if (!lastPage) {
                    setItem(InventorySlot.ROW_5_SLOT_6, RIGHT_ITEM, e -> new SettingsInventory(inventory.getTitle(), player, backAction).addSetting(settings.toArray(new Setting<?>[0])).openInventory(page+1));
                } else {
                    setItem(InventorySlot.ROW_5_SLOT_6, RIGHT_BLOCKED_ITEM, e -> Sound.error(player));
                }
            }

            if (backAction != null) {
                setItem(InventorySlot.ROW_5_SLOT_5, BACK_ITEM, e -> {
                    Sound.error(player);
                    backAction.onClick(e);
                });
            }

            return super.openInventory();
        } else throw new IllegalArgumentException("Could not open Settingsinventory. Page " + page + " does not exist for " + settings.size() + " items!");
    }

    private <T extends Option> void setSetting(Player p, int slot, Setting<T> setting) {
        setItem(slot, setting.getItem());

        T currentOption = setting.getOptionFinder().getCurrentOption(p);
        boolean hasOption = false;
        for (T option : setting.getOptions()) {
            if (option.equals(currentOption)) {
                hasOption = true;
                break;
            }
        }

        if (hasOption) {
            setItem(
                    slot + 9,
                    currentOption.getItem(),
                    makeChooseItemEvent(p, slot+9, setting, currentOption)
            );
        } else throw new IllegalStateException("Cannot update option "+currentOption+" in "+inventory.getTitle()+". The current Option returned via "+setting.getOptionFinder()+" is not in the available options array!");
    }

    private <T extends Option> CoreItemEvent makeChooseItemEvent(Player p, int slot, Setting<T> setting, T currentOption) {
        return e -> {
            T chosenSetting = setting.getNextOption(currentOption);
            setting.getListener().onChosen(p, chosenSetting);

            setItem(slot, chosenSetting.getItem(), makeChooseItemEvent(p, slot, setting, chosenSetting));
            p.updateInventory();
        };
    }

}
