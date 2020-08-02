/*
 * Copyright (c) 2017 - 2020 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.bukkit.overwatch;

import eu.mcone.coresystem.api.bukkit.CoreSystem;
import eu.mcone.coresystem.api.bukkit.util.Messenger;
import eu.mcone.coresystem.bukkit.BukkitCoreSystem;
import eu.mcone.coresystem.bukkit.command.ReportCMD;
import eu.mcone.coresystem.bukkit.overwatch.report.ReportManager;
import eu.mcone.coresystem.core.overwatch.GlobalOverwatch;
import lombok.Getter;

@Getter
public class Overwatch extends GlobalOverwatch implements eu.mcone.coresystem.api.bukkit.overwatch.Overwatch {

    private final ReportManager reportManager;
    private final Messenger messenger;

    public Overwatch() {
        super(BukkitCoreSystem.getInstance());
        reportManager = new ReportManager(this, CoreSystem.getInstance());
        messenger = new Messenger("overwatch.prefix");

        BukkitCoreSystem.getSystem().registerCommands(
                new ReportCMD(this)
        );
    }
}
