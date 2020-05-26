/*
 * Copyright (c) 2017 - 2020 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.api.bukkit.event;

import eu.mcone.coresystem.api.core.overwatch.report.ReportReason;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.util.List;
import java.util.UUID;

@Getter
@RequiredArgsConstructor
public class PlayerReportedEvent extends Event {

    @Getter
    private static final HandlerList handlerList = new HandlerList();

    private final String reportID;
    private final List<UUID> reporter;
    private final Player reported;
    private final ReportReason reportReason;

    @Override
    public HandlerList getHandlers() {
        return handlerList;
    }

}
