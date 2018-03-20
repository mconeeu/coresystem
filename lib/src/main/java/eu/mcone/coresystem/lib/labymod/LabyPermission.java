/*
 * Copyright (c) 2017 - 2018 Dominik L., Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.coresystem.lib.labymod;

import lombok.Getter;

public enum LabyPermission {

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
    private String displayName;
    @Getter
    private boolean defaultEnabled;

    LabyPermission(String displayName, boolean defaultEnabled) {
        this.displayName = displayName;
        this.defaultEnabled = defaultEnabled;
    }

}
