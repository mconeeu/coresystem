/*
 * Copyright (c) 2017 - 2020 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.api.bukkit.overwatch;

import eu.mcone.coresystem.api.bukkit.overwatch.report.ReportManager;
import eu.mcone.coresystem.api.bukkit.broadcast.Messenger;

public interface Overwatch {

    Messenger getMessenger();

    ReportManager getReportManager();
}
