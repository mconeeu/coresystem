/*
 * Copyright (c) 2017 - 2020 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.api.core.overwatch.report;

import lombok.Getter;

@Getter
public enum ReportState {

    CLOSED(3, "§cAbgeschlossen"),
    IN_PROGRESS(2, "§eIn Bearbeitung"),
    OPEN(1, "§aOffen");

    private final int index;
    private final String prefix;

    ReportState(int index, String prefix) {
        this.index = index;
        this.prefix = prefix;
    }
}
