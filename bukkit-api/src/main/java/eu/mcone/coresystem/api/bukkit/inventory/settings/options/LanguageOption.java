package eu.mcone.coresystem.api.bukkit.inventory.settings.options;

import eu.mcone.coresystem.api.bukkit.inventory.settings.Option;
import eu.mcone.coresystem.api.bukkit.item.Skull;
import eu.mcone.coresystem.api.core.translation.Language;
import lombok.Getter;
import org.bukkit.inventory.ItemStack;

@Getter
public class LanguageOption implements Option {

    public static final LanguageOption[] LANGUAGE_OPTIONS = new LanguageOption[Language.values().length];
    static {
        for (int i = 0; i < Language.values().length; i++) {
            LANGUAGE_OPTIONS[i] = new LanguageOption(Language.values()[i]);
        }
    }

    private final Language language;
    private final ItemStack item;

    private LanguageOption(Language language) {
        this.language = language;
        this.item = Skull.fromUrl(language.getTextureUrl())
                .toItemBuilder()
                .displayName(language.getLabel()).lore("§7§oKlicke, um deine Sprache auf", "§7§o"+language.getName()+" zu ändern")
                .create();
    }

    public static LanguageOption get(Language language) {
        for (LanguageOption option : LANGUAGE_OPTIONS) {
            if (option.language == language) {
                return option;
            }
        }

        return null;
    }
}
