/*
 * Copyright (c) 2017 - 2020 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.api.core.overwatch;

import eu.mcone.coresystem.api.core.overwatch.util.Statistic;

public interface GlobalOverwatch {

    Statistic getStatistic();

    void addBan();

    void addReport();

    void addMute();

    void addBotAttacks(int botAttacks);
}
