/*
 * Copyright (c) 2017 - 2020 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.api.bukkit.overwatch.report;

import eu.mcone.coresystem.api.core.overwatch.report.Report;
import eu.mcone.coresystem.api.core.overwatch.report.ReportReason;
import org.bukkit.inventory.ItemStack;

import java.util.Map;
import java.util.UUID;

public interface ReportManager {

    ItemStack getItemForReason(ReportReason reportReason);

    Map<UUID, Report> getToConfirm();

    void setReportMethod(ReportMethod method);
}
