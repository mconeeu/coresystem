/*
 * Copyright (c) 2017 - 2018 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 *
 */

package eu.mcone.coresystem.api.bungee.event;

import eu.mcone.coresystem.api.bungee.player.CorePlayer;
import eu.mcone.coresystem.api.core.translation.Language;
import lombok.Getter;
import net.md_5.bungee.api.plugin.Event;

public class LanguageChangeEvent extends Event {

    @Getter
    private final CorePlayer player;
    @Getter
    private final Language language;

    public LanguageChangeEvent(CorePlayer player, Language language) {
        this.player = player;
        this.language = language;
    }

}
