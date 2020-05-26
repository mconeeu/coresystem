/*
 * Copyright (c) 2017 - 2020 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.api.bukkit.util;

import lombok.Getter;

@Getter
public enum EntitySoundKeys {

    //NMS SOUND LIST: https://pokechu22.github.io/Burger/1.15.2.html#sounds
    PLAYER_HURT("entity.player.hurt"),
    PLAYER_HURT_DROWN("entity.player.hurt_drown"),
    PLAYER_HURT_ON_FIRE("entity.player.hurt_on_fire"),
    ENTITY_ITEM_PICKUP("entity.item.pickup"),
    BLOCK_BUTTON_CLICK("block.button.click");

    private final String nmsSound;

    EntitySoundKeys(String nmsSound) {
        this.nmsSound = nmsSound;
    }
}
