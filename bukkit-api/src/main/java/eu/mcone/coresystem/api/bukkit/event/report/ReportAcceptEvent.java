/*
 * Copyright (c) 2017 - 2020 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.api.bukkit.event.report;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

@Getter
@RequiredArgsConstructor
public class ReportAcceptEvent extends Event {

    @Getter
    private static final HandlerList handlerList = new HandlerList();

    private final String reportID;
    private final Player player;

    @Override
    public HandlerList getHandlers() {
        return handlerList;
    }
}
