/*
 * Copyright (c) 2017 - 2020 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.api.bungee.overwatch;

import eu.mcone.coresystem.api.bungee.overwatch.report.ReportManager;
import eu.mcone.coresystem.api.bungee.util.Messenger;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.HashSet;

public interface Overwatch {

    ReportManager getReportManager();

    Messenger getMessenger();

    boolean isLoggedIn(ProxiedPlayer player);

    HashSet<ProxiedPlayer> getLoggedIn();
}
