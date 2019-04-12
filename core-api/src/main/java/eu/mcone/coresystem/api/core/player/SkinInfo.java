/*
 * Copyright (c) 2017 - 2019 Dominik Lippl, Rufus Maiwald, Felix Schmid and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.api.core.player;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public class SkinInfo {

    public enum SkinType {
        DATABASE, PLAYER, CUSTOM
    }

    @Getter
    private String name, value, signature;
    @Getter
    private SkinType type;

}
