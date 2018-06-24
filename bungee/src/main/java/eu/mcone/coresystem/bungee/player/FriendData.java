/*
 * Copyright (c) 2017 - 2018 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.coresystem.bungee.player;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@AllArgsConstructor
@Getter @Setter
public class FriendData implements eu.mcone.coresystem.api.bungee.player.FriendData {

    private Map<UUID, String> friends;
    private Map<UUID, String> requests;
    private List<UUID> blocks;

}
