/*
 * Copyright (c) 2017 - 2020 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.api.bungee.overwatch.report;

import net.md_5.bungee.api.connection.ProxiedPlayer;

public interface ReportManager {

    void sendOpenReports(ProxiedPlayer player);

    void updateCache();

    void closeReport(ProxiedPlayer player);
}
