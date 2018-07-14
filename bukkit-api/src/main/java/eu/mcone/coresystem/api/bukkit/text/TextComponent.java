/*
 * Copyright (c) 2017 - 2018 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.coresystem.api.bukkit.text;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;

@Deprecated
public class TextComponent {

    private net.md_5.bungee.api.chat.TextComponent textComponent;

    public TextComponent() {
        this.textComponent = new net.md_5.bungee.api.chat.TextComponent();
    }

    public TextComponent setText (String text) {
        this.textComponent.setText(text);
        return this;
    }

    public TextComponent setColor(ChatColor color) {
        this.textComponent.setColor(color);
        return this;
    }

    public TextComponent setBold(Boolean bold) {
        this.textComponent.setBold(bold);
        return this;
    }

    public TextComponent setItalic(Boolean italic) {
        this.textComponent.setItalic(italic);
        return this;
    }

    public TextComponent setUnderlined(Boolean underlined) {
        this.textComponent.setUnderlined(underlined);
        return this;
    }

    public TextComponent setStrikethrough(Boolean strikethrough) {
        this.textComponent.setStrikethrough(strikethrough);
        return this;
    }

    public TextComponent setObfuscated(Boolean obfuscated) {
        this.textComponent.setObfuscated(obfuscated);
        return this;
    }

    public TextComponent setInsertion(String insertion) {
        this.textComponent.setInsertion(insertion);
        return this;
    }

    public TextComponent setClickEvent(ClickEvent clickEvent) {
        this.textComponent.setClickEvent(clickEvent);
        return this;
    }

    public TextComponent setHoverEvent(HoverEvent hoverEvent) {
        this.textComponent.setHoverEvent(hoverEvent);
        return this;
    }

    public net.md_5.bungee.api.chat.TextComponent getTextComponent() {
        return this.textComponent;
    }
}
