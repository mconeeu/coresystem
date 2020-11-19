package eu.mcone.coresystem.api.bukkit.inventory.settings;

import eu.mcone.coresystem.api.bukkit.facades.Sound;
import eu.mcone.coresystem.api.bukkit.inventory.CoreInventory;
import eu.mcone.coresystem.api.bukkit.inventory.CoreItemEvent;
import eu.mcone.coresystem.api.bukkit.inventory.InventoryOption;
import eu.mcone.coresystem.api.bukkit.inventory.InventorySlot;
import org.bukkit.DyeColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SettingsInventory extends CoreInventory {

    private static final ItemStack SETTING_PLACEHOLDER = makePlaceholderItem(DyeColor.SILVER);
    private final List<Setting<?>> settings;

    public SettingsInventory(String title, Player player) {
        super(title, player, InventorySlot.ROW_5, InventoryOption.FILL_EMPTY_SLOTS);
        this.settings = new ArrayList<>();
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
        if (settings.size() >= (((page - 1) * 7) + 1)) {
            boolean lastPage = (((page) * 7) + 1) > settings.size();
            int startOption = ((page - 1) * 7);

            for (int i = startOption, x = InventorySlot.ROW_2_SLOT_2; x <= InventorySlot.ROW_2_SLOT_8; i++, x++) {
                try {
                    setSetting(player, x, settings.get(i));
                } catch (IndexOutOfBoundsException e) {
                    setPlaceholder(x);
                }
            }

            if (settings.size() > 7) {
                if (page > 1) {
                    setItem(InventorySlot.ROW_5_SLOT_4, UP_ITEM, e -> new SettingsInventory(inventory.getTitle(), player).addSetting(settings.toArray(new Setting<?>[0])).openInventory(page-1));
                } else {
                    setItem(InventorySlot.ROW_5_SLOT_4, UP_BLOCKED_ITEM, e -> Sound.error(player));
                }

                if (!lastPage) {
                    setItem(InventorySlot.ROW_5_SLOT_6, DOWN_ITEM, e -> new SettingsInventory(inventory.getTitle(), player).addSetting(settings.toArray(new Setting<?>[0])).openInventory(page-1));
                } else {
                    setItem(InventorySlot.ROW_5_SLOT_6, DOWN_BLOCKED_ITEM, e -> Sound.error(player));
                }
            }

            return super.openInventory();
        } else throw new IllegalArgumentException("Could not open Settingsinventory. Page " + page + " does not exist for " + settings.size() + " items!");
    }

    private <T extends Option> void setSetting(Player p, int slot, Setting<T> setting) {
        setItem(slot, setting.getItem());

        T currentOption = setting.getOptionFinder().getCurrentOption(p);
        setItem(
                slot + 9,
                currentOption.getItem(),
                makeChooseItemEvent(p, slot+9, setting, currentOption)
        );
    }

    private void setPlaceholder(int slot) {
        setItem(slot, SETTING_PLACEHOLDER);
        setItem(slot+9, SETTING_PLACEHOLDER);
    }

    private <T extends Option> CoreItemEvent makeChooseItemEvent(Player p, int slot, Setting<T> setting, T currentOption) {
        return e -> {
            T choosedSetting = setting.getNextOption(currentOption);
            setting.getListener().onChoosed(p, choosedSetting);

            setItem(slot, choosedSetting.getItem(), makeChooseItemEvent(p, slot, setting, choosedSetting));
            p.updateInventory();
        };
    }

}
