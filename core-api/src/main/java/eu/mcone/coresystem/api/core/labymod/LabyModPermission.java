/*
 * Copyright (c) 2017 - 2020 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.api.core.labymod;

import lombok.Getter;

public enum LabyModPermission {

    // Permissions that are disabled by default
    IMPROVED_LAVA("Improved Lava", false),
    CROSSHAIR_SYNC("Crosshair sync", false),
    REFILL_FIX("Refill fix", false),

    // GUI permissions
    GUI_ALL("LabyMod GUI", true),
    GUI_POTION_EFFECTS("Potion Effects", true),
    GUI_ARMOR_HUD("Armor HUD", true),
    GUI_ITEM_HUD("Item HUD", true),

    // Permissions that are enabled by default
    BLOCKBUILD("Blockbuild", true),
    TAGS("Tags", true),
    CHAT("Chat features", true),
    ANIMATIONS("Animations", true),
    SATURATION_BAR("Saturation bar", true);

    @Getter
    private final String displayName;
    @Getter
    private final boolean defaultEnabled;

    /**
     * @param displayName    the permission's display-name
     * @param defaultEnabled whether or not this permission is enabled/activated by default
     */
    LabyModPermission(String displayName, boolean defaultEnabled) {
        this.displayName = displayName;
        this.defaultEnabled = defaultEnabled;
    }

}
