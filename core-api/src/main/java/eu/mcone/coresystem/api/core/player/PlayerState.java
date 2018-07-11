/*
 * Copyright (c) 2017 - 2018 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.coresystem.api.core.player;

import lombok.Getter;

public enum PlayerState {

    OFFLINE(0, "§coffline"),
    ONLINE(1, "§aonline"),
    AFK(2, "§6afk"),
    BANNED(3, "§cgebannt");

    @Getter
    private int id;
    @Getter
    private String name;

    PlayerState(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public static PlayerState getPlayerStateById(int id) {
        for (PlayerState state : values()) {
            if (state.getId() == id) {
                return state;
            }
        }
        return null;
    }

}
