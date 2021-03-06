/*
 * Copyright (c) 2017 - 2020 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
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
@Getter
@Setter
public final class PlayerSettings implements Cloneable {

    private boolean enableFriendRequests = true, autoNick = false;
    private Language language = Language.GERMAN;
    private Sender privateMessages = Sender.FRIENDS, partyInvites = Sender.ALL, joinMeMessages = Sender.ALL;
    private boolean inventoryAnimations = true, playSounds = true;

    public enum Sender {
        ALL, FRIENDS, NOBODY
    }

    @Override
    public PlayerSettings clone() {
        try {
            return (PlayerSettings) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new IllegalStateException();
        }
    }

}
