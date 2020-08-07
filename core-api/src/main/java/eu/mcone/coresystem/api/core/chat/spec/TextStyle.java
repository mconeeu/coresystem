package eu.mcone.coresystem.api.core.chat.spec;

import lombok.Getter;
import net.md_5.bungee.api.ChatColor;

@Getter
public enum TextStyle {

    BOLD(ChatColor.BOLD, 2),
    ITALIC(ChatColor.ITALIC, 1),
    UNDERLINED(ChatColor.UNDERLINE, 3);

    public static final char MARKDOWN_CHAR = '*';

    private final ChatColor color;
    private final int markdownCharsNeeded;

    TextStyle(ChatColor color, int markdownCharsNeeded) {
        this.color = color;
        this.markdownCharsNeeded = markdownCharsNeeded;
    }

    public static TextStyle getTextStyleByCharSize(int size) {
        for (TextStyle style : values()) {
            if (style.markdownCharsNeeded == size) {
                return style;
            }
        }

        return null;
    }

}
