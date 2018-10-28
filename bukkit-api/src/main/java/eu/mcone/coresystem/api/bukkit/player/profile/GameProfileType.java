/*
 * Copyright (c) 2017 - 2018 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.coresystem.api.bukkit.player.profile;

public enum GameProfileType {

    SYSTEM_PROFILE(0),
    GAME_PROFILE(1);

    final int id;

    GameProfileType(final int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }
}
