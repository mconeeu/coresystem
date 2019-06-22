/*
 * Copyright (c) 2017 - 2019 Dominik Lippl, Rufus Maiwald, Felix Schmid and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.api.core.player;

import eu.mcone.coresystem.api.core.translation.Language;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter @Setter
public final class PlayerSettings {

    private boolean enableFriendRequests = true, autoNick = false;
    private Language language = Language.GERMAN;
    private Sender privateMessages = Sender.FRIENDS, partyInvites = Sender.ALL;

    public enum Sender {
        ALL, FRIENDS, NOBODY
    }

}
