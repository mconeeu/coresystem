/*
 * Copyright (c) 2017 - 2020 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.api.bungee.event;

import eu.mcone.coresystem.api.bungee.player.CorePlayer;
import eu.mcone.coresystem.api.core.translation.Language;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.md_5.bungee.api.plugin.Event;

@AllArgsConstructor
@Getter
public final class LanguageChangeEvent extends Event {

    private final CorePlayer player;
    private final Language language;

}
