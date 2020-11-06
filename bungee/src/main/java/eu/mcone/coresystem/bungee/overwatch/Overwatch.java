/*
 * Copyright (c) 2017 - 2020 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.bungee.overwatch;

import eu.mcone.coresystem.api.bungee.util.Messenger;
import eu.mcone.coresystem.api.core.GlobalCoreSystem;
import eu.mcone.coresystem.bungee.BungeeCoreSystem;
import eu.mcone.coresystem.bungee.command.OverwatchCMD;
import eu.mcone.coresystem.bungee.command.ReportCMD;
import eu.mcone.coresystem.bungee.command.ReportsCMD;
import eu.mcone.coresystem.bungee.overwatch.punish.PunishManager;
import eu.mcone.coresystem.bungee.overwatch.report.ReportManager;
import eu.mcone.coresystem.bungee.overwatch.trusted.TrustManager;
import eu.mcone.coresystem.bungee.player.BungeeMessenger;
import eu.mcone.coresystem.core.overwatch.GlobalOverwatch;
import lombok.Getter;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.HashSet;

@Getter
public class Overwatch extends GlobalOverwatch implements eu.mcone.coresystem.api.bungee.overwatch.Overwatch {

    private final ReportManager reportManager;
    private final PunishManager punishManager;
    private final TrustManager trustManager;
    private final Messenger messenger;

    private final HashSet<ProxiedPlayer> loggedIn;

    public Overwatch(GlobalCoreSystem instance) {
        super(instance);

        reportManager = new ReportManager(this);
        punishManager = new PunishManager(this);
        trustManager = new TrustManager(this);
        messenger = new BungeeMessenger(instance, "system.bungee.overwatch.prefix");
        loggedIn = new HashSet<>();

        BungeeCoreSystem.getSystem().registerCommands(
                new ReportsCMD(),
                new ReportCMD(this),
                new OverwatchCMD(this)
        );
    }

    public void login(ProxiedPlayer player) {
        loggedIn.add(player);
    }

    public void logout(ProxiedPlayer player) {
        loggedIn.remove(player);
    }

    public boolean isLoggedIn(ProxiedPlayer player) {
        return loggedIn.contains(player);
    }
}
