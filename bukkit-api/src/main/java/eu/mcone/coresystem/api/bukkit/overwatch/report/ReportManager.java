/*
 * Copyright (c) 2017 - 2020 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.api.bukkit.overwatch.report;

import eu.mcone.coresystem.api.core.overwatch.report.ReportReason;
import org.bukkit.inventory.ItemStack;

public interface ReportManager {

    ItemStack getItemForReason(ReportReason reportReason);
}
