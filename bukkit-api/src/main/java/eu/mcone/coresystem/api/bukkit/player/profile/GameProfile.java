/*
 * Copyright (c) 2017 - 2018 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.coresystem.api.bukkit.player.profile;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bukkit.entity.Player;

import javax.annotation.Nullable;

@NoArgsConstructor
@Getter @Setter
public abstract class GameProfile {

    private String uuid;

    public GameProfile(@Nullable Player player) {
        if (player != null) {
            this.uuid = player.getUniqueId().toString();
        }
    }

}
