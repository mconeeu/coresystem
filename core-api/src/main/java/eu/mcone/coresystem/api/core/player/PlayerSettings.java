/*
 * Copyright (c) 2017 - 2018 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.coresystem.api.core.player;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter @Setter
public final class PlayerSettings {

    private boolean enableFriendRequests = true, acceptedAgbs = false;
    private Sender privateMessages = Sender.FRIENDS, partyInvites = Sender.ALL;

    public enum Sender {
        ALL, FRIENDS, NOBODY
    }

}
