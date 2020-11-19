package eu.mcone.coresystem.api.bukkit.inventory.settings;

import lombok.Getter;
import org.bukkit.inventory.ItemStack;

@Getter
public class Setting<T extends Option> {

    private final ItemStack item;
    private final T[] options;
    private ChooseListener<T> listener;
    private CurrentOptionFinder<T> optionFinder;

    @SafeVarargs
    public Setting(ItemStack item, T... options) {
        this.item = item;
        this.options = options;
    }

    @SafeVarargs
    public Setting(ItemStack item, ChooseListener<T> listener, CurrentOptionFinder<T> optionFinder, T... options) {
        this(item, options);
        this.listener = listener;
        this.optionFinder = optionFinder;
    }

    public Setting<T> chooseListener(ChooseListener<T> listener) {
        this.listener = listener;
        return this;
    }

    public Setting<T> currentOptionFinder(CurrentOptionFinder<T> optionFinder) {
        this.optionFinder = optionFinder;
        return this;
    }

    public T getNextOption(T option) {
        for (int i = 0; i < options.length; i++) {
            if (options[i].equals(option)) {
                return i < options.length-1 ? options[++i] : options[0];
            }
        }

        return options[0];
    }

}
