/*
 * Copyright (c) 2017 - 2020 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.api.core.overwatch.report;

import lombok.Getter;

@Getter
public enum ReportPriority {

    //Difference: 10
    EXTREME(1, "§4§lExtrem", "§4", 90, 100),
    //Difference: 30
    HIGH(2, "§c§lHoch", "§c", 60, 90),
    //Difference: 30
    MEDIUM(3, "§e§lMittel", "§e", 30, 60),
    //Difference: 30
    NORMAL(4, "§7§lNormal", "§7", 0, 30);

    private final int level;
    private final String prefix;
    private final String label;
    //Minimal Level for this Priority
    private final int min;
    //Maximal Level for this Priority
    private final int max;

    ReportPriority(int priority, String prefix, String label, int min, int max) {
        this.level = priority;
        this.prefix = prefix;
        this.label = label;
        this.min = min;
        this.max = max;
    }

    public static ReportPriority getWhereLevel(int lvl) {
        for (ReportPriority priority : values()) {
            if (priority.getLevel() == lvl) {
                return priority;
            }
        }

        return null;
    }

    public static ReportPriority getWherePointsLevel(int points) {
        for (ReportPriority priority : values()) {
            if (points > EXTREME.getMax()) {
                return EXTREME;
            } else if (points >= priority.getMin() && points <= priority.getMax()) {
                return priority;
            }
        }

        return null;
    }
}
