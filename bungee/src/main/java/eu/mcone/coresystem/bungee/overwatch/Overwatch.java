/*
 * Copyright (c) 2017 - 2020 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.bungee.overwatch;

import eu.mcone.coresystem.api.bungee.util.Messenger;
import eu.mcone.coresystem.api.core.GlobalCoreSystem;
import eu.mcone.coresystem.bungee.overwatch.punish.PunishManager;
import eu.mcone.coresystem.bungee.overwatch.report.ReportManager;
import eu.mcone.coresystem.bungee.overwatch.trusted.TrustManager;
import eu.mcone.coresystem.core.overwatch.GlobalOverwatch;
import lombok.Getter;

@Getter
public class Overwatch extends GlobalOverwatch implements eu.mcone.coresystem.api.bungee.overwatch.Overwatch {

    private final ReportManager reportManager;
    private final PunishManager punishManager;
    private final TrustManager trustManager;
    private final Messenger messenger;

    public Overwatch(GlobalCoreSystem instance) {
        super(instance);

        reportManager = new ReportManager(this);
        punishManager = new PunishManager(this);
        trustManager = new TrustManager(this);
        messenger = new Messenger("overwatch.prefix");
    }
}
