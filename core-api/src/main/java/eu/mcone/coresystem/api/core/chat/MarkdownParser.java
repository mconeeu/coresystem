package eu.mcone.coresystem.api.core.chat;

import eu.mcone.coresystem.api.core.chat.spec.TextLevel;
import eu.mcone.coresystem.api.core.chat.spec.TextStyle;
import eu.mcone.coresystem.api.core.chat.spec.TextVariant;
import net.md_5.bungee.api.ChatColor;

public class MarkdownParser {

    public static String parseMarkdown(String raw, TextLevel level) {
        if (!level.equals(TextLevel.NONE)) {
            char[] rawArr = raw.toCharArray();
            StringBuilder sb = new StringBuilder(level.getColor().toString());
            TextVariant currentVariant = null;

            stringLoop:
            for (int i = 0; i < rawArr.length; i++) {
                char c = rawArr[i];

                if (currentVariant == null) {
                    if (rawArr.length > (i + 1) && rawArr[i + 1] == '[') {
                        for (TextVariant variant : TextVariant.values()) {
                            if (variant.getMarkdown() == c) {
                                currentVariant = variant;
                                sb.append(variant.getColors().get(level));
                                continue stringLoop;
                            }
                        }
                    }

                    sb.append(c);
                } else {
                    if (c == ']') {
                        currentVariant = null;
                        sb.append(level.getColor().toString());
                    } else if (c != '[') {
                        sb.append(c);
                    }
                }
            }

            return parseStyles(sb.toString());
        } else {
            return parseStyles(raw);
        }
    }

    private static String parseStyles(String raw) {
        char[] rawArr = raw.toCharArray();
        StringBuilder sb = new StringBuilder();
        int markdownChars = 0;
        TextStyle currentTextStyle = null;

        for (char c : rawArr) {
            if (c == TextStyle.MARKDOWN_CHAR && markdownChars < 3) {
                markdownChars++;
            } else if (markdownChars > 0) {
                if (currentTextStyle == null) {
                    currentTextStyle = TextStyle.getTextStyleByCharSize(markdownChars);
                    sb.append(currentTextStyle.getColor().toString()).append(c);
                } else {
                    currentTextStyle = null;
                    sb.append(ChatColor.RESET.toString());
                }

                markdownChars = 0;
            } else {
                sb.append(c);
            }
        }

        return sb.toString();
    }

}
