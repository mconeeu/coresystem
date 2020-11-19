package eu.mcone.coresystem.api.bukkit.inventory.settings.settings;

import eu.mcone.coresystem.api.bukkit.inventory.settings.ChooseListener;
import eu.mcone.coresystem.api.bukkit.inventory.settings.CurrentOptionFinder;
import eu.mcone.coresystem.api.bukkit.inventory.settings.Option;
import eu.mcone.coresystem.api.bukkit.inventory.settings.Setting;
import eu.mcone.coresystem.api.bukkit.item.ItemBuilder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class BooleanSetting {

    @RequiredArgsConstructor
    public static class BooleanOption implements Option {
        @Getter
        private final ItemStack item;
        private final boolean value;
    }

    private final ItemStack item;

    private String enabledTitle = "§a§lAktiviert", disabledTitle = "§c§lDeaktiviert";
    private String[] enabledDescription, disabledDescription;
    private ChooseListener<Boolean> listener;
    private CurrentOptionFinder<Boolean> optionFinder;

    public BooleanSetting(ItemStack item, String targetFunction) {
        this.item = item;

        this.enabledDescription = new String[]{"§7§oKlicke zum aktivieren", "§7§ovon "+targetFunction};
        this.disabledDescription = new String[]{"§7§oKlicke zum deaktivieren", "§7§ovon "+targetFunction};
    }

    public BooleanSetting setEnabledTitle(String title) {
        this.enabledTitle = title;
        return this;
    }

    public BooleanSetting setDisabledTitle(String title) {
        this.disabledTitle = title;
        return this;
    }

    public BooleanSetting setEnabledDescription(String... description) {
        this.enabledDescription = description;
        return this;
    }

    public BooleanSetting setDisabledDescription(String... description) {
        this.disabledDescription = description;
        return this;
    }

    public BooleanSetting onChoose(ChooseListener<Boolean> listener) {
        this.listener = listener;
        return this;
    }

    public BooleanSetting optionFinder(CurrentOptionFinder<Boolean> optionFinder) {
        this.optionFinder = optionFinder;
        return this;
    }

    public Setting<BooleanOption> create() {
        BooleanOption[] options = new BooleanOption[]{
                new BooleanOption(new ItemBuilder(Material.INK_SACK, 1, DyeColor.LIME.getDyeData()).displayName(enabledTitle).lore(enabledDescription).create(), true),
                new BooleanOption(new ItemBuilder(Material.INK_SACK, 1, DyeColor.RED.getDyeData()).displayName(disabledTitle).lore(disabledDescription).create(), false)
        };

        return new Setting<>(item, options)
                .chooseListener((p, result) -> listener.onChoosed(p, result.value))
                .currentOptionFinder(p -> options[optionFinder.getCurrentOption(p) ? 0 : 1]);
    }

}
