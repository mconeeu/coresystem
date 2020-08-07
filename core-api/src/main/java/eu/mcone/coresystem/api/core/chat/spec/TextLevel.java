package eu.mcone.coresystem.api.core.chat.spec;

import lombok.Getter;
import net.md_5.bungee.api.ChatColor;

@Getter
public enum TextLevel {

    NONE(ChatColor.RESET),
    INFO(ChatColor.GRAY),
    SUCCESS(ChatColor.DARK_GREEN),
    WARNING(ChatColor.GOLD),
    ERROR(ChatColor.DARK_RED);

    private final ChatColor color;

    TextLevel(ChatColor color) {
        this.color = color;
    }

}
