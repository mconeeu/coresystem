/*
 * Copyright (c) 2017 - 2020 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.api.bukkit.util;

import lombok.Getter;

@Getter
public enum SoundCategories {

    MASTER(0, "master"),
    MUSIC(1, "music"),
    RECORDS(2, "record"),
    WEATHER(3, "weather"),
    BLOCKS(4, "block"),
    HOSTILE(5, "hostile"),
    NEUTRAL(6, "neutral"),
    PLAYERS(7, "player"),
    AMBIENT(8, "ambient"),
    VOICE(9, "voice");

    private int id;
    private String name;

    SoundCategories(int id, String name) {
        this.id = id;
        this.name = name;
    }

    private SoundCategories getCategory(int id) {
        for (SoundCategories categories : values()) {
            if (categories.getId() == id) {
                return categories;
            }
        }

        return null;
    }
}
