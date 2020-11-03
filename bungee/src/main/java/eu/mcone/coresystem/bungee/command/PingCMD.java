/*
 * Copyright (c) 2017 - 2020 Dominik Lippl, Rufus Maiwald and the MC ONE Minecraftnetwork. All rights reserved
 * You are not allowed to decompile the code
 */

package eu.mcone.coresystem.bungee.command;

import eu.mcone.coresystem.api.bungee.command.CorePlayerCommand;
import eu.mcone.coresystem.bungee.BungeeCoreSystem;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class PingCMD extends CorePlayerCommand {

    public PingCMD() {
        super("ping", null);
    }

    public void onPlayerCommand(ProxiedPlayer p, String[] args) {
        if (!BungeeCoreSystem.getInstance().getCooldownSystem().addAndCheck(this.getClass(), p.getUniqueId())) return;

        if (args.length == 0) {
            BungeeCoreSystem.getInstance().getMessenger().sendSender(p, "§7Dein Ping: §f" + p.getPing() + "ms");
        } else {
            BungeeCoreSystem.getInstance().getMessenger().sendSender(p, "§4Bitte benutze: §c/ping");
        }
    }
}
