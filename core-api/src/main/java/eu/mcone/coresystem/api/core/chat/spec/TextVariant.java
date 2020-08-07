package eu.mcone.coresystem.api.core.chat.spec;

import lombok.Getter;
import net.md_5.bungee.api.ChatColor;

import java.util.HashMap;
import java.util.Map;

@Getter
public enum TextVariant {

    HIGHLIGHTED('!', new HashMap<TextLevel, String>(){{
        put(TextLevel.INFO, ChatColor.WHITE.toString());
        put(TextLevel.SUCCESS, ChatColor.RED.toString());
        put(TextLevel.WARNING, ChatColor.YELLOW.toString());
        put(TextLevel.ERROR, ChatColor.RED.toString());
    }}),
    INFO('+', new HashMap<TextLevel, String>(){{
        put(TextLevel.INFO, ChatColor.WHITE.toString()+ChatColor.ITALIC.toString());
        put(TextLevel.SUCCESS, ChatColor.WHITE.toString());
        put(TextLevel.WARNING, ChatColor.WHITE.toString());
        put(TextLevel.ERROR, ChatColor.WHITE.toString());
    }}),
    NOTE('#', new HashMap<TextLevel, String>(){{
        for (TextLevel level : TextLevel.values()) {
            put(level, ChatColor.GRAY.toString()+ChatColor.ITALIC.toString());
        }
    }});

    private final char markdown;
    private final Map<TextLevel, String> colors;

    TextVariant(char markdown, Map<TextLevel, String> colors) {
        this.markdown = markdown;
        this.colors = colors;
    }

}
