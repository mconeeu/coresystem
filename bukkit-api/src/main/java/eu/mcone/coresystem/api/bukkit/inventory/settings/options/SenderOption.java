package eu.mcone.coresystem.api.bukkit.inventory.settings.options;

import eu.mcone.coresystem.api.bukkit.inventory.settings.Option;
import eu.mcone.coresystem.api.bukkit.item.ItemBuilder;
import eu.mcone.coresystem.api.core.player.PlayerSettings;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

@Getter
public class SenderOption implements Option {

    private final ItemStack item;
    private final PlayerSettings.Sender sender;

    public SenderOption(PlayerSettings.Sender sender, String targetFunction) {
        this.sender = sender;

        switch (sender) {
            case ALL: {
                this.item = new ItemBuilder(Material.INK_SACK, 1, 10).displayName("§a§lVon Jedem").lore("§7§o"+targetFunction+" von", "§7§ojedem Spieler auf dem Netzwerk").create();
                break;
            }
            case FRIENDS: {
                this.item = new ItemBuilder(Material.INK_SACK, 1, 14).displayName("§e§lVon Freunden").lore("§7§o"+targetFunction+" nur von", "§7§oFreunden von dir").create();
                break;
            }
            case NOBODY: {
                this.item = new ItemBuilder(Material.INK_SACK, 1, 1).displayName("§c§lVon Niemandem").lore("§7§o"+targetFunction+" von", "§7§okeinem Spieler auf dem Netzwerk").create();
                break;
            }
            default: {
                this.item = new ItemBuilder(Material.INK_SACK).displayName("§7§lKeine Präferenz").lore("§7§oKlicke um eine Einstellung für "+targetFunction+" festzulegen").create();
            }
        }
    }

    public static SenderOption[] makeSenderOptions(String targetFunction) {
        SenderOption[] senderOptions = new SenderOption[PlayerSettings.Sender.values().length];
        for (int i = 0; i < PlayerSettings.Sender.values().length; i++) {
            senderOptions[i] = new SenderOption(PlayerSettings.Sender.values()[i], targetFunction);
        }

        return senderOptions;
    }

    public static SenderOption get(PlayerSettings.Sender sender, SenderOption... options) {
        for (SenderOption option : options) {
            if (option.sender == sender) {
                return option;
            }
        }

        return null;
    }

}
